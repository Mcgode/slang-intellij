package slang.plugin.lsp.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl

class SlangLanguageClient(project: Project): LanguageClientImpl(project) {
}