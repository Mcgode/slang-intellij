package slang.plugin.editor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SlangOccurrencesTest {

    private fun idAt(textWithCaret: String): String? {
        val caret = textWithCaret.indexOf('|')
        val text = textWithCaret.replace("|", "")
        val range = SlangOccurrences.identifierAt(text, caret) ?: return null
        return text.substring(range.startOffset, range.endOffset)
    }

    private fun occurrences(text: String, name: String): List<Int> =
        SlangOccurrences.occurrencesOf(text, name).map { it.startOffset }

    @Test
    fun `identifierAt returns the token under the caret`() {
        assertEquals("color", idAt("float3 co|lor = 0;"))
        assertEquals("color", idAt("float3 |color = 0;")) // at the start
        assertEquals("color", idAt("float3 color| = 0;")) // just past the end
    }

    @Test
    fun `identifierAt is null off an identifier`() {
        assertNull(idAt("float3 color =| 0;"))   // operator
        assertNull(idAt("float3| color = 0;"))   // whitespace after keyword — caret past 'float3'? it's on space
        assertNull(idAt("return| x;"))           // caret at end of the 'return' keyword
        assertNull(idAt("// comm|ent\n"))        // inside a comment
    }

    @Test
    fun `occurrencesOf finds every identifier token with that name`() {
        //      x here ───┐     and here ───┐   ┌── and here
        val text = "float x = 1;\nfloat y = x + x;\n"
        assertEquals(listOf(6, 23, 27), occurrences(text, "x"))
    }

    @Test
    fun `occurrencesOf matches whole tokens only`() {
        val text = "int index = idx + indexOf;"
        assertEquals(listOf(4), occurrences(text, "index"))
    }

    @Test
    fun `occurrencesOf ignores matches inside comments and strings`() {
        val text = "int width;\n// width in pixels\nstring s = \"width\";\n"
        assertEquals(listOf(4), occurrences(text, "width"))
    }

    @Test
    fun `occurrencesOf does not treat keywords as identifiers`() {
        assertEquals(emptyList<Int>(), occurrences("if (a) return;", "if"))
    }
}
