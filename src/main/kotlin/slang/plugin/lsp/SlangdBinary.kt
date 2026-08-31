package slang.plugin.lsp

import com.intellij.execution.configurations.PathEnvironmentVariableUtil
import com.intellij.openapi.util.SystemInfo
import slang.plugin.settings.SlangSettings
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isExecutable

/**
 * Resolves the `slangd` executable to launch, in priority order:
 *  1. explicit path from settings,
 *  2. `slangd` on the system PATH,
 *  3. a plugin-managed download (not yet implemented — [downloadedBinary] returns null for now).
 */
object SlangdBinary {

    private val exeName: String get() = if (SystemInfo.isWindows) "slangd.exe" else "slangd"

    fun resolve(): Path? {
        val settings = SlangSettings.getInstance().state

        settings.slangdPath?.takeIf { it.isNotBlank() }?.let { configured ->
            val path = Path.of(configured)
            val candidate = if (Files.isDirectory(path)) path.resolve(exeName) else path
            if (candidate.isExecutable()) return candidate
        }

        PathEnvironmentVariableUtil.findInPath(exeName)?.let { return it.toPath() }

        if (settings.autoDownload) {
            downloadedBinary()?.let { return it }
        }
        return null
    }

    /** TODO: fetch a matching slangd from https://github.com/shader-slang/slang/releases into the plugin cache. */
    private fun downloadedBinary(): Path? = null
}
