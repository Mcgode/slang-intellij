package slang.plugin.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.SystemInfo
import java.nio.file.Files
import java.nio.file.Path

/**
 * Reads the Slang release version of a `slangd` install. `slangd` itself has no `--version`, but its
 * sibling `slangc -v` prints e.g. `2026.16.1`.
 */
object SlangdVersion {

    private val LOG = logger<SlangdVersion>()
    private val VERSION_RE = Regex("""\b(\d{4}\.\d+(?:\.\d+)?(?:-[0-9A-Za-z.]+)?)\b""")

    /** Blocking — call off the EDT. Returns null if `slangc` is missing or the call fails. */
    fun of(slangd: Path): String? {
        val slangc = slangd.resolveSibling(if (SystemInfo.isWindows) "slangc.exe" else "slangc")
        if (!Files.isExecutable(slangc)) return null
        return try {
            val output = ExecUtil.execAndGetOutput(GeneralCommandLine(slangc.toString(), "-v"), 5_000)
            parse(output.stdout + "\n" + output.stderr)
        } catch (e: Exception) {
            LOG.info("could not read slangd version from $slangc", e)
            null
        }
    }

    internal fun parse(text: String): String? = VERSION_RE.find(text)?.groupValues?.get(1)

    /** True when [detected] is a known version older than [expected]. Unknown → false (don't nag). */
    fun isOlderThan(detected: String?, expected: String): Boolean =
        detected != null && compare(detected, expected) < 0

    internal fun compare(a: String, b: String): Int {
        val pa = a.substringBefore('-').split('.').map { it.toIntOrNull() ?: 0 }
        val pb = b.substringBefore('-').split('.').map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(pa.size, pb.size)) {
            val diff = pa.getOrElse(i) { 0 } - pb.getOrElse(i) { 0 }
            if (diff != 0) return diff
        }
        return 0
    }
}
