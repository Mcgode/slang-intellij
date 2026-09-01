package slang.plugin.lsp

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import slang.plugin.settings.SlangSettings
import slang.plugin.settings.SlangdSource
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isExecutable

/**
 * Resolves the `slangd` executable to launch, according to `slangdSource`:
 *  - SYSTEM: the configured path, or failing that `slangd` on `PATH`,
 *  - PLUGIN: the plugin-managed download.
 */
object SlangdBinary {

    private val exeName: String get() = if (SystemInfo.isWindows) "slangd.exe" else "slangd"

    fun resolve(): Path? = when (SlangSettings.getInstance().state.slangdSource) {
        SlangdSource.SYSTEM -> systemBinary()
        SlangdSource.PLUGIN -> SlangdDownload.installedBinary()
    }

    /** The system `slangd`: configured path first, then `PATH`. Null if neither resolves. */
    fun systemBinary(): Path? {
        SlangSettings.getInstance().state.slangdPath?.takeIf { it.isNotBlank() }?.let { configured ->
            val path = Path.of(configured)
            val candidate = if (Files.isDirectory(path)) path.resolve(exeName) else path
            if (candidate.isExecutable()) return candidate
        }
        return PathEnvironmentVariableUtil.findInPath(exeName)?.toPath()
    }

    fun isPluginManaged(binary: Path): Boolean = binary == SlangdDownload.installedBinary()
}
