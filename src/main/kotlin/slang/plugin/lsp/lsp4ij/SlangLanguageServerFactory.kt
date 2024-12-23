package slang.plugin.lsp.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerEnablementSupport
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import slang.plugin.lsp.SlangLanguageServerProvider

class SlangLanguageServerFactory: LanguageServerFactory, LanguageServerEnablementSupport {

    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        return SlangLanguageServer(project)
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return SlangLanguageClient(project)
    }

    override fun isEnabled(project: Project): Boolean {
        val provider = SlangLanguageServerProvider.getInstance(project)
        val enabled = provider.checkValidLanguageServerFiles()
        if (!enabled)
            provider.tryAutoDownload(project)
        return enabled
    }

    override fun setEnabled(value: Boolean, project: Project) {}
}