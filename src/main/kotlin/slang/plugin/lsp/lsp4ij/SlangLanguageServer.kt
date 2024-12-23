package slang.plugin.lsp.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import slang.plugin.lsp.SlangLanguageServerProvider
import kotlin.io.path.absolutePathString

class SlangLanguageServer(project: Project): OSProcessStreamConnectionProvider() {
    init {
        val provider = SlangLanguageServerProvider.getInstance(project)
        assert(provider.checkValidLanguageServerFiles())
        commandLine = GeneralCommandLine(provider.getSlangDExecutablePath().absolutePathString(), "--stdio")
    }
}