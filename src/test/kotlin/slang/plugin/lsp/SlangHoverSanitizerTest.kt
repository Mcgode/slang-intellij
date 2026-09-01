package slang.plugin.lsp

import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.MarkupContent
import org.eclipse.lsp4j.Range
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class SlangHoverSanitizerTest {

    @Test
    fun `null result is passed through`() {
        assertNull(sanitizeHover(null))
    }

    @Test
    fun `hover with null contents becomes null even when a range is set`() {
        val hover = Hover().apply { range = Range() }
        assertNull(sanitizeHover(hover))
    }

    @Test
    fun `a real hover is left untouched`() {
        val hover = Hover(MarkupContent("markdown", "float3 x"))
        assertSame(hover, sanitizeHover(hover))
    }
}
