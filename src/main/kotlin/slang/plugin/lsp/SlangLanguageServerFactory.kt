package slang.plugin.lsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider

class SlangLanguageServerFactory: LanguageServerFactory {

    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        return SlangLanguageServer(project)
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return SlangLanguageClient(project)
    }
}