package slang.plugin.lsp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import slang.plugin.highlight.SlangColors

class SlangLspSemanticTokensTest {

    private val mapper = SlangLspSemanticTokens()

    /** The complete legend slangd reports (slang-language-server-semantic-tokens.h). */
    private val slangdTokenTypes = listOf(
        "type", "enumMember", "variable", "parameter", "function",
        "property", "namespace", "keyword", "macro", "string",
    )

    @Test
    fun `every slangd token type maps to a colour`() {
        for (type in slangdTokenTypes) {
            assertNotNull("no colour mapping for slangd token type '$type'", mapper.getTextAttributesKey(type, emptyList()))
        }
    }

    @Test
    fun `mappings point at the plugin's own keys`() {
        assertEquals(SlangColors.TYPE, mapper.getTextAttributesKey("type", emptyList()))
        assertEquals(SlangColors.FUNCTION, mapper.getTextAttributesKey("function", emptyList()))
        assertEquals(SlangColors.ENUM_MEMBER, mapper.getTextAttributesKey("enumMember", emptyList()))
        assertEquals(SlangColors.MACRO, mapper.getTextAttributesKey("macro", emptyList()))
    }

    @Test
    fun `token types slangd never sends are left to the platform default`() {
        assertNull(mapper.getTextAttributesKey("number", emptyList()))
        assertNull(mapper.getTextAttributesKey("operator", emptyList()))
        assertNull(mapper.getTextAttributesKey("comment", emptyList()))
        assertNull(mapper.getTextAttributesKey("", emptyList()))
    }
}
