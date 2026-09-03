package slang.plugin.lexer

import com.intellij.psi.TokenType
import org.junit.Assert.assertEquals
import org.junit.Test
import slang.plugin.lexer.SlangTokenTypes as T

class SlangLexerTest {

    private fun tokens(text: String): List<Pair<Any?, String>> {
        val lexer = SlangLexer()
        lexer.start(text)
        val result = mutableListOf<Pair<Any?, String>>()
        while (lexer.tokenType != null) {
            result += lexer.tokenType to text.substring(lexer.tokenStart, lexer.tokenEnd)
            lexer.advance()
        }
        return result
    }

    private fun significant(text: String) =
        tokens(text).filter { it.first != TokenType.WHITE_SPACE }

    @Test
    fun `keywords and builtin types are classified`() {
        assertEquals(
            listOf(
                T.KEYWORD to "struct",
                T.IDENTIFIER to "Foo",
                T.LBRACE to "{",
                T.BUILTIN_TYPE to "float3",
                T.IDENTIFIER to "p",
                T.SEMICOLON to ";",
                T.RBRACE to "}",
            ),
            significant("struct Foo { float3 p; }"),
        )
    }

    @Test
    fun `glsl types and qualifiers are classified`() {
        assertEquals(
            listOf(
                T.KEYWORD to "layout",
                T.LPAREN to "(",
                T.IDENTIFIER to "location",
                T.OPERATOR to "=",
                T.NUMBER to "0",
                T.RPAREN to ")",
                T.KEYWORD to "in",
                T.BUILTIN_TYPE to "vec3",
                T.IDENTIFIER to "pos",
                T.SEMICOLON to ";",
            ),
            significant("layout(location = 0) in vec3 pos;"),
        )
    }

    @Test
    fun `line comment consumes to end of line`() {
        assertEquals(
            listOf(T.LINE_COMMENT to "// hello", T.KEYWORD to "let"),
            significant("// hello\nlet"),
        )
    }

    @Test
    fun `block comment spans lines`() {
        assertEquals(listOf(T.BLOCK_COMMENT to "/* a\n b */"), significant("/* a\n b */"))
    }

    @Test
    fun `preprocessor directive is one token with line continuation`() {
        assertEquals(
            listOf(T.PREPROCESSOR_DIRECTIVE to "#define A(x) \\\n  ((x))"),
            significant("#define A(x) \\\n  ((x))"),
        )
    }

    @Test
    fun `hex and float literals`() {
        assertEquals(
            listOf(T.NUMBER to "0xFF", T.NUMBER to "1.5f", T.NUMBER to "2e-3"),
            significant("0xFF 1.5f 2e-3"),
        )
    }
}
