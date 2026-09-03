package slang.plugin.lsp

import com.intellij.openapi.util.SystemInfo
import com.intellij.util.EnvironmentUtil
import slang.plugin.settings.SlangSettings
import slang.plugin.settings.SlangdSource
import java.io.File
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
        return findInPath(exeName)
    }

    /**
     * First executable named [exe] on `PATH`. Uses [EnvironmentUtil] so the shell `PATH` is seen
     * even when the IDE was not started from a terminal (notably on macOS).
     *
     * Implemented manually instead of using PathEnvironmentVariableUtil API, as it is undergoing change
     */
    private fun findInPath(exe: String): Path? {
        val path = EnvironmentUtil.getValue("PATH") ?: return null
        return path.splitToSequence(File.pathSeparatorChar)
            .filter { it.isNotBlank() }
            .map { Path.of(it).resolve(exe) }
            .firstOrNull { it.isExecutable() }
    }

    fun isPluginManaged(binary: Path): Boolean = binary == SlangdDownload.installedBinary()
}
