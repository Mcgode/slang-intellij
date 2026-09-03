package slang.plugin.language

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShaderLanguageTest {

    @Test
    fun `GLSL and HLSL are dialects of Slang`() {
        for (lang in listOf(GlslLanguage, HlslLanguage)) {
            assertTrue("$lang should be a kind of Slang", lang.isKindOf(SlangLanguage))
            assertEquals(SlangLanguage, lang.baseLanguage)
        }
    }

    @Test
    fun `associated extensions are lowercase, dot-free and unique`() {
        for (exts in listOf(ShaderFileAssociations.GLSL_EXTENSIONS, ShaderFileAssociations.HLSL_EXTENSIONS)) {
            assertEquals(exts.distinct(), exts)
            assertTrue(exts.all { it.isNotEmpty() && it == it.lowercase() && !it.contains('.') })
        }
    }

    @Test
    fun `each file type's default extension is in its associated set`() {
        assertTrue(GlslFileType.INSTANCE.defaultExtension in ShaderFileAssociations.GLSL_EXTENSIONS)
        assertTrue(HlslFileType.INSTANCE.defaultExtension in ShaderFileAssociations.HLSL_EXTENSIONS)
    }

    @Test
    fun `GLSL and HLSL extension sets do not overlap`() {
        val overlap = ShaderFileAssociations.GLSL_EXTENSIONS intersect ShaderFileAssociations.HLSL_EXTENSIONS.toSet()
        assertTrue("unexpected shared extensions: $overlap", overlap.isEmpty())
    }
}
