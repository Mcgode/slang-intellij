package slang.plugin.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * GLSL shader files, handled through `slangd` (see [GlslLanguage]).
 *
 * No extensions are bound to this type by default — it conflicts with the standalone GLSL plugin
 * and with Rider's built-in support. Extensions are associated at runtime, only for the ones no
 * other plugin claims, when the user turns on GLSL support in the settings
 * ([slang.plugin.language.ShaderFileAssociations]).
 */
class GlslFileType private constructor() : LanguageFileType(GlslLanguage) {

    // "SlangGLSL", not "GLSL": a bare "GLSL" file type name collides with the standalone GLSL
    // plugin and trips the IDE's startup file-type-conflict check ("conflicts with IDEA CORE").
    // The name is an internal id; the user sees getDescription().
    override fun getName(): String = "SlangGLSL"

    override fun getDescription(): String = "GLSL shader file (via slangd)"

    override fun getDefaultExtension(): String = "glsl"

    override fun getIcon(): Icon = SlangIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = GlslFileType()
    }
}
