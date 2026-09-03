package slang.plugin.editor

import com.intellij.openapi.util.TextRange
import com.intellij.util.text.CharArrayUtil
import slang.plugin.lexer.SlangLexer
import slang.plugin.lexer.SlangTokenTypes

/**
 * Lexer-based identifier lookup for occurrence highlighting. slangd provides no `documentHighlight`,
 * so "highlight the identifier under the caret" is done locally, the same way [SlangFoldingBuilder]
 * runs its own [SlangLexer] over the document text — no PSI required.
 */
internal object SlangOccurrences {

    /** Range of the identifier token that contains [offset], or null if [offset] is not inside one. */
    fun identifierAt(text: CharSequence, offset: Int): TextRange? {
        val lexer = SlangLexer()
        lexer.start(text, 0, text.length, 0)
        while (lexer.tokenType != null) {
            if (lexer.tokenStart > offset) break
            if (lexer.tokenType == SlangTokenTypes.IDENTIFIER && offset in lexer.tokenStart..lexer.tokenEnd) {
                return TextRange(lexer.tokenStart, lexer.tokenEnd)
            }
            lexer.advance()
        }
        return null
    }

    /** Ranges of every identifier token whose text equals [name]. */
    fun occurrencesOf(text: CharSequence, name: CharSequence): List<TextRange> {
        val result = ArrayList<TextRange>()
        val lexer = SlangLexer()
        lexer.start(text, 0, text.length, 0)
        while (lexer.tokenType != null) {
            if (lexer.tokenType == SlangTokenTypes.IDENTIFIER &&
                lexer.tokenEnd - lexer.tokenStart == name.length &&
                CharArrayUtil.regionMatches(text, lexer.tokenStart, lexer.tokenEnd, name)
            ) {
                result.add(TextRange(lexer.tokenStart, lexer.tokenEnd))
            }
            lexer.advance()
        }
        return result
    }
}
