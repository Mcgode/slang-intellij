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

    override fun getName(): String = "HLSL"

    override fun getDescription(): String = "HLSL shader file (via slangd)"

    override fun getDefaultExtension(): String = "hlsl"

    override fun getIcon(): Icon = SlangIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = HlslFileType()
    }
}
