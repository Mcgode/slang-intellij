package slang.plugin.language

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.FileTypes
import slang.plugin.settings.SlangSettings

/**
 * Binds GLSL / HLSL file extensions to [GlslFileType] / [HlslFileType] at runtime, driven by the
 * `glslSupport` / `hlslSupport` settings.
 *
 * This is deliberately not a static `<fileType extensions="…">` in `plugin.xml`: `.glsl`, `.hlsl`,
 * `.frag` … are also claimed by the standalone GLSL plugin and by Rider's built-in support, and a
 * static registration would fight them. Instead an extension is claimed only while its setting is
 * on and only if no other file type already owns it; turning the setting off releases exactly what
 * was added.
 */
object ShaderFileAssociations {

    /**
     * Extensions Slang's front-end maps to `SourceLanguage::GLSL` (verified against slangc / slangd
     * 2026.16.1). Non-standard header extensions (`.glslh`, `.glsli`) are *not* recognised — slangd
     * parses them as Slang — so shared GLSL code belongs in a `.glsl` file.
     */
    val GLSL_EXTENSIONS: List<String> = listOf(
        "glsl",
        "vert", "frag", "geom", "comp", "tesc", "tese",
        "mesh", "task",
        "rgen", "rint", "rahit", "rchit", "rmiss", "rcall",
    )

    /**
     * HLSL and HLSL-adjacent extensions. Only `.hlsl` and `.fx` are in Slang's extension table;
     * `slangd` still serves the rest, parsing them as Slang (a near-superset of HLSL), so
     * completion / hover / diagnostics work. Legacy corners of `.fx` / `.cginc` may show extra
     * diagnostics.
     */
    val HLSL_EXTENSIONS: List<String> = listOf(
        "hlsl", "hlsli", "fx", "fxh", "usf", "ush", "compute", "cginc",
    )

    /** Reconcile every extension association with the current settings. Safe to call repeatedly. */
    fun sync() {
        val state = SlangSettings.getInstance().state
        reconcile(
            GlslFileType.INSTANCE to (state.glslSupport to GLSL_EXTENSIONS),
            HlslFileType.INSTANCE to (state.hlslSupport to HLSL_EXTENSIONS),
        )
    }

    private fun reconcile(vararg groups: Pair<FileType, Pair<Boolean, List<String>>>) {
        val app = ApplicationManager.getApplication()
        app.invokeLater {
            app.runWriteAction {
                val ftm = FileTypeManager.getInstance()
                for ((fileType, spec) in groups) {
                    val (enabled, extensions) = spec
                    for (ext in extensions) {
                        val owner = ftm.getFileTypeByExtension(ext)
                        when {
                            enabled && owner == FileTypes.UNKNOWN -> ftm.associateExtension(fileType, ext)
                            !enabled && owner == fileType -> ftm.removeAssociatedExtension(fileType, ext)
                        }
                    }
                }
            }
        }
    }
}
