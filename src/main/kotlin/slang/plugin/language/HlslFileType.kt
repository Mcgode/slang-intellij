package slang.plugin.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * HLSL and HLSL-adjacent shader files (`.hlsl`, `.hlsli`, `.fx`, and Unreal's `.usf` / `.ush`),
 * handled through `slangd` (see [HlslLanguage]).
 *
 * As with [GlslFileType], no extensions are bound by default — `.hlsl` in particular collides with
 * Rider's built-in support. Extensions are associated at runtime when the user turns on HLSL
 * support in the settings ([ShaderFileAssociations]).
 */
class HlslFileType private constructor() : LanguageFileType(HlslLanguage) {

    // "SlangHLSL", not "HLSL": a bare "HLSL" file type name collides with the HLSL support bundled
    // in Rider / CLion Nova and trips the IDE's startup file-type-conflict check ("conflicts with
    // IDEA CORE"). The name is an internal id; the user sees getDescription().
    override fun getName(): String = "SlangHLSL"

    override fun getDescription(): String = "HLSL shader file (via slangd)"

    override fun getDefaultExtension(): String = "hlsl"

    override fun getIcon(): Icon = SlangIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = HlslFileType()
    }
}
