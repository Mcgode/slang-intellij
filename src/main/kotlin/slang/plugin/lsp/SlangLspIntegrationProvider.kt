package slang.plugin.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspIntegrationProvider
import com.intellij.platform.lsp.api.ProjectWideLspClientDescriptor
import com.intellij.platform.lsp.api.customization.LspCustomization
import org.eclipse.lsp4j.ConfigurationItem
import slang.plugin.SlangBundle
import slang.plugin.language.SlangFileType
import java.util.concurrent.atomic.AtomicBoolean

class SlangLspIntegrationProvider : LspIntegrationProvider {

    private val missingNotified = AtomicBoolean(false)

    override fun fileOpened(project: Project, file: VirtualFile, clientStarter: LspIntegrationProvider.LspClientStarter) {
        if (file.fileType != SlangFileType.INSTANCE) return

        val binary = SlangdBinary.resolve()
        if (binary == null) {
            if (missingNotified.compareAndSet(false, true)) {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("Slang")
                    .createNotification(
                        SlangBundle.message("notification.slangd.notFound.title"),
                        SlangBundle.message("notification.slangd.notFound.content"),
                        NotificationType.WARNING,
                    )
                    .notify(project)
            }
            return
        }

        clientStarter.ensureClientStarted(SlangLspClientDescriptor(project, binary.toString()))
    }
}

private class SlangLspClientDescriptor(
    project: Project,
    private val slangdPath: String,
) : ProjectWideLspClientDescriptor(project, "Slang") {

    override fun isSupportedFile(file: VirtualFile): Boolean = file.fileType == SlangFileType.INSTANCE

    override fun getLanguageId(file: VirtualFile): String = "slang"

    override fun createCommandLine(): GeneralCommandLine = GeneralCommandLine(slangdPath)

    override fun createInitializationOptions(): Any = SlangLspConfig.initializationOptions()

    override fun getWorkspaceConfiguration(item: ConfigurationItem): Any? =
        SlangLspConfig.configurationFor(item.section)

    override val lspCustomization: LspCustomization = object : LspCustomization() {
        override val semanticTokensCustomizer = SlangLspSemanticTokens()
    }
}
