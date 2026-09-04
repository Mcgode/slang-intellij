package slang.plugin.language

import com.intellij.lang.Language

/**
 * GLSL as a dialect of [SlangLanguage].
 *
 * `slangd` has no dedicated GLSL mode — the Slang compiler front-end switches to GLSL parsing when a
 * source file begins with a `#version` directive, regardless of the file extension or the LSP
 * `languageId`. So a GLSL file is, to the language server, just another document; treating it as a
 * Slang dialect lets it reuse the lexer, highlighter and editor support unchanged while still
 * showing up as its own "GLSL" file type.
 */
// ID is "SlangGLSL", not "GLSL": a bare "GLSL" collides with the standalone GLSL plugin's language,
// which makes the platform report this plugin as conflicting with the IDE at startup. The
// user-facing name stays "GLSL" via getDisplayName() / the file type description.
object GlslLanguage : Language(SlangLanguage, "SlangGLSL") {
    private fun readResolve(): Any = GlslLanguage
    override fun getDisplayName(): String = "GLSL"
}
