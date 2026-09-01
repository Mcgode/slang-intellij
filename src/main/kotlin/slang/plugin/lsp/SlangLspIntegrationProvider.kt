package slang.plugin.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.intellij.platform.lsp.api.ProjectWideLspClientDescriptor
import com.intellij.platform.lsp.api.customization.LspCustomization
import org.eclipse.lsp4j.ConfigurationItem
import slang.plugin.SlangBundle
import slang.plugin.language.SlangFileType
import slang.plugin.settings.SlangSettings
import slang.plugin.settings.SlangSettingsConfigurable
import slang.plugin.settings.SlangdSource
import java.util.concurrent.atomic.AtomicBoolean

class SlangLspIntegrationProvider : LspIntegrationProvider {

    private val missingNotified = AtomicBoolean(false)
    private val versionChecked = AtomicBoolean(false)

    override fun fileOpened(project: Project, file: VirtualFile, clientStarter: LspIntegrationProvider.LspClientStarter) {
        if (file.fileType != SlangFileType.INSTANCE) return

        val binary = SlangdBinary.resolve()
        if (binary == null) {
            onSlangdMissing(project)
            return
        }

        project.getService(SlangLspServerCustomization::class.java).ensureInstalled()
        clientStarter.ensureClientStarted(SlangLspClientDescriptor(project, binary.toString()))
        maybeWarnOutdated(project, binary)
    }

    /** One-off, off-EDT: if the system slangd is older than the plugin-managed version, offer to switch. */
    private fun maybeWarnOutdated(project: Project, binary: java.nio.file.Path) {
        if (SlangdBinary.isPluginManaged(binary)) return
        if (!versionChecked.compareAndSet(false, true)) return

        ApplicationManager.getApplication().executeOnPooledThread {
            val detected = SlangdVersion.of(binary) ?: return@executeOnPooledThread
            if (!SlangdVersion.isOlderThan(detected, SlangdDownload.VERSION)) return@executeOnPooledThread

            NotificationGroupManager.getInstance()
                .getNotificationGroup("Slang")
                .createNotification(
                    SlangBundle.message("notification.slangd.notFound.title"),
                    SlangBundle.message("notification.slangd.outdated", detected, SlangdDownload.VERSION),
                    NotificationType.WARNING,
                )
                .addAction(NotificationAction.createSimple(SlangBundle.message("notification.slangd.action.useBundled")) {
                    SlangSettings.getInstance().state.slangdSource = SlangdSource.PLUGIN
                    if (SlangdDownload.installedBinary() == null) {
                        SlangdDownload.startDownload(project)
                    } else {
                        SlangdDownload.restart(project)
                    }
                })
                .notify(project)
        }
    }

    private fun onSlangdMissing(project: Project) {
        if (SlangdDownload.isDownloading) return

        val settings = SlangSettings.getInstance().state
        if (settings.slangdSource == SlangdSource.PLUGIN && settings.autoDownload) {
            SlangdDownload.startDownload(project)
            return
        }

        if (!missingNotified.compareAndSet(false, true)) return
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Slang")
            .createNotification(
                SlangBundle.message("notification.slangd.notFound.title"),
                SlangBundle.message("notification.slangd.notFound.content"),
                NotificationType.WARNING,
            )
            .addAction(NotificationAction.createSimple(SlangBundle.message("notification.slangd.action.download")) {
                settings.slangdSource = SlangdSource.PLUGIN
                SlangdDownload.startDownload(project)
            })
            .addAction(NotificationAction.createSimple(SlangBundle.message("notification.slangd.action.configure")) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, SlangSettingsConfigurable::class.java)
            })
            .notify(project)
    }
}

private class SlangLspClientDescriptor(
    private val slangProject: Project,
    private val slangdPath: String,
) : ProjectWideLspClientDescriptor(slangProject, "Slang") {

    override fun isSupportedFile(file: VirtualFile): Boolean = file.fileType == SlangFileType.INSTANCE

    override fun getLanguageId(file: VirtualFile): String = "slang"

    override fun createCommandLine(): GeneralCommandLine = GeneralCommandLine(slangdPath)

    override fun createInitializationOptions(): Any = SlangLspConfig.initializationOptions(slangProject)

    override fun getWorkspaceConfiguration(item: ConfigurationItem): Any? =
        SlangLspConfig.configurationFor(item.section, slangProject)

    override val lspCustomization: LspCustomization = object : LspCustomization() {
        override val semanticTokensCustomizer = SlangLspSemanticTokens()
    }
}
