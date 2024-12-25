package slang.plugin.lsp.intellij

import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerManager
import com.intellij.platform.lsp.api.LspServerSupportProvider
import slang.plugin.language.SlangFileType
import slang.plugin.lsp.SlangLanguageServerProvider
import slang.plugin.settings.SlangConfigService

@Suppress("UnstableApiUsage")
class SlangLspServerSupportProvider: LspServerSupportProvider {

    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        serverStarter: LspServerSupportProvider.LspServerStarter
    ) {
        val slangConfig = SlangConfigService.getInstance(project)
        if (!slangConfig.enableLsp || !slangConfig.useIntellijLspClient)
            return

        if (FileTypeManager.getInstance().getFileTypeByFile(file) is SlangFileType) {
            val provider = SlangLanguageServerProvider.getInstance(project)
            if (provider.checkValidLanguageServerFiles())
                serverStarter.ensureServerStarted(SlangLspServerDescriptor(project, provider))
            else
                provider.tryAutoDownload(project) {
                    LspServerManager.getInstance(project).startServersIfNeeded(this.javaClass)
                }
        }
    }

}