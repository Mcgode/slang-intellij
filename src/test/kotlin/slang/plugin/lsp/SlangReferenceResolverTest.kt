package slang.plugin.lsp

import com.intellij.openapi.util.TextRange
import org.junit.Assert.assertEquals
import org.junit.Test
import slang.plugin.lsp.SlangReferenceResolver.Companion.filterSameDeclaration
import slang.plugin.lsp.SlangReferenceResolver.Companion.normalizePath
import slang.plugin.lsp.SlangReferenceResolver.DefKey

class SlangReferenceResolverTest {

    private fun r(start: Int, end: Int) = TextRange(start, end)

    /** Position map: pretend every range is on its own line (line == startOffset) at column 0. */
    private val posOf: (TextRange) -> Pair<Int, Int> = { it.startOffset to 0 }

    @Test
    fun `keeps uses that resolve to the caret's declaration, drops others`() {
        val caret = r(100, 101)                       // a use of the local
        val caretKey = DefKey("/a.slang", 120, 0)     // the local is declared at line 120
        val field = DefKey("/a.slang", 50, 0)         // a same-named struct field
        val candidates = listOf(r(50, 51), r(100, 101), r(120, 121), r(140, 141))
        val defs = mapOf(
            r(50, 51) to field,
            r(100, 101) to caretKey,
            r(140, 141) to caretKey,
            // r(120,121) is the declaration itself — matched by position, never resolved
        )

        val kept = filterSameDeclaration(caret, caretKey, candidates, posOf) { defs[it] }

        assertEquals(listOf(r(100, 101), r(120, 121), r(140, 141)), kept)
    }

    @Test
    fun `the declaration is matched by position without a resolve call`() {
        val caret = r(10, 11)
        val caretKey = DefKey("/a.slang", 200, 0)
        val declRange = r(200, 201)
        var resolvedDecl = false

        val kept = filterSameDeclaration(caret, caretKey, listOf(r(10, 11), declRange), posOf) {
            if (it == declRange) { resolvedDecl = true; null } else null
        }

        assertEquals(listOf(r(10, 11), declRange), kept)
        assertEquals(false, resolvedDecl)
    }

    @Test
    fun `always keeps the caret occurrence`() {
        val caret = r(10, 11)
        val kept = filterSameDeclaration(caret, DefKey("/a.slang", 1, 0), listOf(r(10, 11), r(20, 21)), posOf) { null }
        assertEquals(listOf(r(10, 11)), kept)
    }

    @Test
    fun `matches a use whose declaration is at the same position but a differently spelled path`() {
        val caret = r(10, 11)
        val caretKey = DefKey("/a.slang", 5, 2)
        val use = r(30, 31)
        val kept = filterSameDeclaration(caret, caretKey, listOf(r(10, 11), use), posOf) {
            if (it == use) DefKey("/A.SLANG", 5, 2) else null
        }
        assertEquals(listOf(r(10, 11), use), kept)
    }

    @Test
    fun `normalizePath decodes and collapses file uris`() {
        assertEquals(
            normalizePath("file:///home/x/shaders/./a.slang"),
            normalizePath("file:///home/x/shaders/b/../a.slang"),
        )
        assertEquals("/home/x/my shader.slang", normalizePath("file:///home/x/my%20shader.slang"))
    }

    @Test
    fun `normalizePath falls back to the raw string for a non-uri`() {
        assertEquals("not a uri", normalizePath("not a uri"))
    }
}
