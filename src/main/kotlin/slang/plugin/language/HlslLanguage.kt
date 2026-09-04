package slang.plugin.language

import com.intellij.lang.Language

/**
 * HLSL as a dialect of [SlangLanguage].
 *
 * Slang's front-end is a near-superset of HLSL, and `slangd` parses HLSL (and HLSL-adjacent files
 * such as Unreal's `.usf` / `.ush`) with the same machinery — completion, hover, diagnostics,
 * go-to-definition all work. Treating it as a Slang dialect reuses the lexer, highlighter and
 * editor support unchanged while still giving these files their own "HLSL" file type.
 */
object HlslLanguage : Language(SlangLanguage, "HLSL") {
    private fun readResolve(): Any = HlslLanguage
    override fun getDisplayName(): String = "HLSL"
}
