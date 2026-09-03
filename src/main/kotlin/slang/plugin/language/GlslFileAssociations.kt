package slang.plugin.language

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.FileTypes
import slang.plugin.settings.SlangSettings

/**
 * Binds GLSL file extensions to [GlslFileType] at runtime, driven by
 * [SlangSettings.SlangState.glslSupport].
 *
 * This is deliberately not done through a static `<fileType extensions="…">` in `plugin.xml`:
 * `.glsl`, `.vert`, `.frag` … are also claimed by the standalone GLSL plugin and by Rider's
 * built-in HLSL/GLSL support, and a static registration would fight them. Instead we only claim an
 * extension that nothing else owns, and only while the setting is on; turning the setting off
 * releases exactly the extensions we added.
 */
object GlslFileAssociations {

    /** Khronos / glslang stage extensions, plus the generic ones. */
    val EXTENSIONS: List<String> = listOf(
        "glsl", "glslh", "glsli",
        "vert", "frag", "geom", "comp", "tesc", "tese",
        "mesh", "task",
        "rgen", "rint", "rahit", "rchit", "rmiss", "rcall",
    )

    /** Reconcile the extension associations with the current setting. Safe to call repeatedly. */
    fun sync() {
        val enabled = SlangSettings.getInstance().state.glslSupport
        val app = ApplicationManager.getApplication()
        app.invokeLater {
            app.runWriteAction {
                val ftm = FileTypeManager.getInstance()
                for (ext in EXTENSIONS) {
                    val owner = ftm.getFileTypeByExtension(ext)
                    when {
                        enabled && owner == FileTypes.UNKNOWN ->
                            ftm.associateExtension(GlslFileType.INSTANCE, ext)
                        !enabled && owner == GlslFileType.INSTANCE ->
                            ftm.removeAssociatedExtension(GlslFileType.INSTANCE, ext)
                    }
                }
            }
        }
    }
}
