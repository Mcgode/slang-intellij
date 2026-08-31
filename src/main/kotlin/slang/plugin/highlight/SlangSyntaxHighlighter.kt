package slang.plugin.highlight

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import slang.plugin.lexer.SlangLexer
import slang.plugin.lexer.SlangTokenTypes as T

class SlangSyntaxHighlighter : SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer = SlangLexer()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> = when (tokenType) {
        T.LINE_COMMENT -> KEYS_LINE_COMMENT
        T.BLOCK_COMMENT -> KEYS_BLOCK_COMMENT
        T.KEYWORD -> KEYS_KEYWORD
        T.BUILTIN_TYPE -> KEYS_BUILTIN_TYPE
        T.NUMBER -> KEYS_NUMBER
        T.STRING -> KEYS_STRING
        T.PREPROCESSOR_DIRECTIVE -> KEYS_PREPROCESSOR
        T.LBRACE, T.RBRACE -> KEYS_BRACES
        T.LBRACKET, T.RBRACKET -> KEYS_BRACKETS
        T.LPAREN, T.RPAREN -> KEYS_PARENS
        T.SEMICOLON -> KEYS_SEMICOLON
        T.COMMA -> KEYS_COMMA
        T.DOT -> KEYS_DOT
        T.OPERATOR -> KEYS_OPERATOR
        TokenType.BAD_CHARACTER -> KEYS_BAD_CHAR
        else -> SlangColors.EMPTY
    }

    private companion object {
        val KEYS_LINE_COMMENT = SlangColors.keys(SlangColors.LINE_COMMENT)
        val KEYS_BLOCK_COMMENT = SlangColors.keys(SlangColors.BLOCK_COMMENT)
        val KEYS_KEYWORD = SlangColors.keys(SlangColors.KEYWORD)
        val KEYS_BUILTIN_TYPE = SlangColors.keys(SlangColors.BUILTIN_TYPE)
        val KEYS_NUMBER = SlangColors.keys(SlangColors.NUMBER)
        val KEYS_STRING = SlangColors.keys(SlangColors.STRING)
        val KEYS_PREPROCESSOR = SlangColors.keys(SlangColors.PREPROCESSOR)
        val KEYS_BRACES = SlangColors.keys(SlangColors.BRACES)
        val KEYS_BRACKETS = SlangColors.keys(SlangColors.BRACKETS)
        val KEYS_PARENS = SlangColors.keys(SlangColors.PARENTHESES)
        val KEYS_SEMICOLON = SlangColors.keys(SlangColors.SEMICOLON)
        val KEYS_COMMA = SlangColors.keys(SlangColors.COMMA)
        val KEYS_DOT = SlangColors.keys(SlangColors.DOT)
        val KEYS_OPERATOR = SlangColors.keys(SlangColors.OPERATOR)
        val KEYS_BAD_CHAR = SlangColors.keys(SlangColors.BAD_CHARACTER)
    }
}
