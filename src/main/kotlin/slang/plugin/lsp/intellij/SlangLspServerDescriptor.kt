package slang.plugin.lsp.intellij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerDescriptor
import org.eclipse.lsp4j.ConfigurationItem
import slang.plugin.language.SlangFileType
import slang.plugin.lsp.SlangJsonConfigService
import slang.plugin.lsp.SlangLanguageServerProvider
import kotlin.io.path.absolutePathString

@Suppress("UnstableApiUsage")
class SlangLspServerDescriptor(project: Project, private val provider: SlangLanguageServerProvider)
    : LspServerDescriptor(project, "Slang")
{
    override fun createCommandLine(): GeneralCommandLine {
        return GeneralCommandLine(
            provider.getSlangDExecutablePath().absolutePathString(),
            "--stdio"
        )
    }

    override fun isSupportedFile(file: VirtualFile): Boolean {
        return FileTypeManager.getInstance().getFileTypeByFile(file) is SlangFileType
    }

    override fun getWorkspaceConfiguration(item: ConfigurationItem): Any? {
        val jsonConfig = SlangJsonConfigService.getInstance(project)

        jsonConfig.prepareJsonConfig(project)

        when (item.section) {
            "slang.predefinedMacros",
            "slang.additionalSearchPaths"
                -> return jsonConfig.getEntry<List<String>>(item.section)
        }
        return super.getWorkspaceConfiguration(item)
    }
}