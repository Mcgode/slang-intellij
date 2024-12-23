package slang.plugin.lsp

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl

class SlangLanguageClient(project: Project): LanguageClientImpl(project) {
}