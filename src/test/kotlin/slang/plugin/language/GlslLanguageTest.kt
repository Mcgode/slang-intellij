package slang.plugin.language

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GlslLanguageTest {

    @Test
    fun `GLSL is a dialect of Slang`() {
        assertTrue(GlslLanguage.isKindOf(SlangLanguage))
        assertEquals(SlangLanguage, GlslLanguage.baseLanguage)
    }

    @Test
    fun `associated extensions are lowercase, dot-free and unique`() {
        val exts = GlslFileAssociations.EXTENSIONS
        assertEquals(exts.distinct(), exts)
        assertTrue(exts.all { it.isNotEmpty() && it == it.lowercase() && !it.contains('.') })
        // the extension the file type reports as its default must be in the runtime-associated set
        assertTrue(GlslFileType.INSTANCE.defaultExtension in exts)
    }
}
