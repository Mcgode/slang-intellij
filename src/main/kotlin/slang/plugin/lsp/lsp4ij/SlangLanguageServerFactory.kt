package slang.plugin.lsp.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerEnablementSupport
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import slang.plugin.lsp.SlangLanguageServerDownloader
import slang.plugin.lsp.SlangLanguageServerProvider
import slang.plugin.settings.SlangConfigService

class SlangLanguageServerFactory: LanguageServerFactory, LanguageServerEnablementSupport {

    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        return SlangLanguageServer(project)
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return SlangLanguageClient(project)
    }

    override fun isEnabled(project: Project): Boolean {
        val slangConfig = SlangConfigService.getInstance(project)
        val provider = SlangLanguageServerProvider.getInstance(project)
        if (slangConfig.enableLsp && slangConfig.useLsp4ij)
            return false
        val enabled = provider.checkValidLanguageServerFiles()
        if (!enabled)
            provider.tryAutoDownload(project)  {
                if (it == SlangLanguageServerDownloader.Status.Downloaded) {
                    LanguageServerManager.getInstance(project).start("slangLanguageServer")
                }
            }
        return enabled
    }

    override fun setEnabled(value: Boolean, project: Project) {}
}