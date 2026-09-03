package slang.plugin.lexer

import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import slang.plugin.lexer.SlangTokenTypes as T

/**
 * Hand-written lexer for syntax highlighting, brace matching, comment toggling and folding.
 *
 * This is deliberately shallow: it does not model the preprocessor or disambiguate types from
 * expressions. Semantic tokenization is provided by the Slang language server (slangd). Keeping this
 * local and synchronous is what keeps the typing loop off the LSP round-trip path.
 */
class SlangLexer : LexerBase() {

    private var buffer: CharSequence = ""
    private var endOffset = 0
    private var tokenStart = 0
    private var tokenEnd = 0
    private var tokenType: IElementType? = null

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.endOffset = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        advance()
    }

    override fun getState(): Int = 0
    override fun getBufferSequence(): CharSequence = buffer
    override fun getBufferEnd(): Int = endOffset
    override fun getTokenType(): IElementType? = tokenType
    override fun getTokenStart(): Int = tokenStart
    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        tokenStart = tokenEnd
        if (tokenStart >= endOffset) {
            tokenType = null
            return
        }
        val c = buffer[tokenStart]
        tokenType = if (c.isWhitespace()) {
            scanWhile { it.isWhitespace() }
            TokenType.WHITE_SPACE
        } else if (c == '/' && peek(1) == '/') {
            scanLineComment()
        } else if (c == '/' && peek(1) == '*') {
            scanBlockComment()
        } else if (isDirectiveStart()) {
            scanDirective()
        } else if (c == '"') {
            scanString()
        } else if (c.isDigit() || (c == '.' && peek(1).isDigit())) {
            scanNumber()
        } else if (isIdentifierStart(c)) {
            scanIdentifier()
        } else {
            scanPunctuation()
        }
    }

    private fun peek(ahead: Int): Char {
        val i = tokenStart + ahead
        return if (i in 0 until endOffset) buffer[i] else ' '
    }

    private inline fun scanWhile(pred: (Char) -> Boolean) {
        var i = tokenStart
        while (i < endOffset && pred(buffer[i])) i++
        tokenEnd = i
    }

    private fun scanLineComment(): IElementType {
        var i = tokenStart + 2
        while (i < endOffset && buffer[i] != '\n') {
            if (buffer[i] == '\\' && i + 1 < endOffset && (buffer[i + 1] == '\n' || buffer[i + 1] == '\r')) {
                i += 2
            } else {
                i++
            }
        }
        tokenEnd = i
        return T.LINE_COMMENT
    }

    private fun scanBlockComment(): IElementType {
        var i = tokenStart + 2
        while (i < endOffset) {
            if (buffer[i] == '*' && i + 1 < endOffset && buffer[i + 1] == '/') {
                i += 2
                break
            }
            i++
        }
        tokenEnd = i.coerceAtMost(endOffset)
        return T.BLOCK_COMMENT
    }

    /** A `#` that is the first non-whitespace character on its line begins a preprocessor directive. */
    private fun isDirectiveStart(): Boolean {
        if (buffer[tokenStart] != '#') return false
        var i = tokenStart - 1
        while (i >= 0) {
            val ch = buffer[i]
            if (ch == '\n') return true
            if (!ch.isWhitespace()) return false
            i--
        }
        return true
    }

    private fun scanDirective(): IElementType {
        var i = tokenStart + 1
        while (i < endOffset && buffer[i] != '\n') {
            if (buffer[i] == '\\' && i + 1 < endOffset && (buffer[i + 1] == '\n' || buffer[i + 1] == '\r')) {
                i += 2
            } else if (buffer[i] == '/' && i + 1 < endOffset && (buffer[i + 1] == '/' || buffer[i + 1] == '*')) {
                break
            } else {
                i++
            }
        }
        tokenEnd = i
        return T.PREPROCESSOR_DIRECTIVE
    }

    private fun scanString(): IElementType {
        var i = tokenStart + 1
        while (i < endOffset) {
            val ch = buffer[i]
            if (ch == '\\') {
                i += 2
            } else if (ch == '"') {
                i++
                break
            } else if (ch == '\n') {
                break
            } else {
                i++
            }
        }
        tokenEnd = i.coerceAtMost(endOffset)
        return T.STRING
    }

    private fun scanNumber(): IElementType {
        var i = tokenStart
        if (buffer[i] == '0' && i + 1 < endOffset && (buffer[i + 1] == 'x' || buffer[i + 1] == 'X')) {
            i += 2
            while (i < endOffset && buffer[i].isLetterOrDigit()) i++
        } else {
            while (i < endOffset) {
                val ch = buffer[i]
                val prev = if (i > 0) buffer[i - 1] else ' '
                if (ch.isDigit() || ch == '.' || ch == 'e' || ch == 'E') {
                    i++
                } else if ((ch == '+' || ch == '-') && (prev == 'e' || prev == 'E')) {
                    i++
                } else if (ch == 'f' || ch == 'F' || ch == 'h' || ch == 'H' ||
                    ch == 'u' || ch == 'U' || ch == 'l' || ch == 'L'
                ) {
                    i++
                } else {
                    break
                }
            }
        }
        tokenEnd = i
        return T.NUMBER
    }

    private fun isIdentifierStart(c: Char) = c == '_' || c.isLetter()
    private fun isIdentifierPart(c: Char) = c == '_' || c.isLetterOrDigit()

    private fun scanIdentifier(): IElementType {
        var i = tokenStart + 1
        while (i < endOffset && isIdentifierPart(buffer[i])) i++
        tokenEnd = i
        val text = buffer.subSequence(tokenStart, i).toString()
        return when (text) {
            in T.KEYWORDS -> T.KEYWORD
            in T.BUILTIN_TYPES -> T.BUILTIN_TYPE
            else -> T.IDENTIFIER
        }
    }

    private fun scanPunctuation(): IElementType {
        val c = buffer[tokenStart]
        tokenEnd = tokenStart + 1
        return when (c) {
            '{' -> T.LBRACE
            '}' -> T.RBRACE
            '(' -> T.LPAREN
            ')' -> T.RPAREN
            '[' -> T.LBRACKET
            ']' -> T.RBRACKET
            ';' -> T.SEMICOLON
            ',' -> T.COMMA
            '.' -> T.DOT
            in OPERATOR_CHARS -> {
                var i = tokenStart + 1
                while (i < endOffset && buffer[i] in OPERATOR_CHARS) i++
                tokenEnd = i
                T.OPERATOR
            }
            else -> TokenType.BAD_CHARACTER
        }
    }

    private companion object {
        private const val OPERATOR_CHARS = "+-*/%=!<>&|^~?:"
    }
}
