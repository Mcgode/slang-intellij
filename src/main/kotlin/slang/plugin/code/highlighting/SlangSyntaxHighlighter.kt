package slang.plugin.code.highlighting

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import slang.plugin.psi.SlangLexerAdapter
import slang.plugin.psi.SlangTokenSets
import slang.plugin.psi.SlangOldTypes
import slang.plugin.psi.types.SlangTypes

class SlangSyntaxHighlighter: SyntaxHighlighterBase() {

    object SlangTextAttributes {
        val SLANG_VARIABLE_TEXT = createTextAttributesKey("Slang.Variable", IDENTIFIER)
        val SLANG_USER_DEFINED_TYPE_TEXT = createTextAttributesKey("Slang.UserDefinedType", CLASS_REFERENCE)
        val SLANG_BUILTIN_NAME_TEXT = createTextAttributesKey("Slang.BuiltinName", KEYWORD)
        val SLANG_KEYWORD_TEXT = createTextAttributesKey("Slang.Keyword", KEYWORD)
        val SLANG_NUMERIC_LITERALS_TEXT = createTextAttributesKey("Slang.NumericLiteral", NUMBER)
        val SLANG_STRING_LITERAL_TEXT = createTextAttributesKey("Slang.StringLiteral", STRING)
        val SLANG_LINE_COMMENT_TEXT = createTextAttributesKey("Slang.LineComment", LINE_COMMENT)
        val SLANG_MULTILINE_COMMENT_TEXT = createTextAttributesKey("Slang.MultilineComment", BLOCK_COMMENT)
        val SLANG_BRACE_TEXT = createTextAttributesKey("Slang.Brace", BRACES)
        val SLANG_BRACKET_TEXT = createTextAttributesKey("Slang.Bracket", BRACKETS)
        val SLANG_COMMA_TEXT = createTextAttributesKey("Slang.Comma", COMMA)
        val SLANG_DOT_TEXT = createTextAttributesKey("Slang.Dot", DOT)
        val SLANG_PAREN_TEXT = createTextAttributesKey("Slang.Parentheses", PARENTHESES)
        val SLANG_SEMICOLON_TEXT = createTextAttributesKey("Slang.Semicolon", SEMICOLON)
        val SLANG_BAD_CHARACTER_TEXT = createTextAttributesKey("Slang.BadCharacter", HighlighterColors.BAD_CHARACTER)
    }

    override fun getHighlightingLexer(): Lexer {
        return SlangLexerAdapter()
    }

    private fun mapTokenToTextAttribute(tokenType: IElementType): TextAttributesKey? {
        return when (tokenType) {
            in (SlangTokenSets.VARIABLES) -> SlangTextAttributes.SLANG_VARIABLE_TEXT
//            SlangTypes.USER_TYPE_NAME -> SlangTextAttributes.SLANG_USER_DEFINED_TYPE_TEXT
            in (SlangTokenSets.BUILTINS) -> SlangTextAttributes.SLANG_BUILTIN_NAME_TEXT
            in (SlangTokenSets.KEYWORDS) -> SlangTextAttributes.SLANG_KEYWORD_TEXT
            in (SlangTokenSets.NUMERIC_LITERALS) -> SlangTextAttributes.SLANG_NUMERIC_LITERALS_TEXT
//            SlangTypes.STRING_LITERAL -> SlangTextAttributes.SLANG_STRING_LITERAL_TEXT
            SlangOldTypes.BOOL_LITERAL -> SlangTextAttributes.SLANG_KEYWORD_TEXT
            SlangOldTypes.LINE_COMMENT -> SlangTextAttributes.SLANG_LINE_COMMENT_TEXT
            SlangOldTypes.MULTILINE_COMMENT -> SlangTextAttributes.SLANG_MULTILINE_COMMENT_TEXT
            SlangTypes.COMMA -> SlangTextAttributes.SLANG_COMMA_TEXT
//            SlangTypes.DOT -> SlangTextAttributes.SLANG_DOT_TEXT
            SlangTypes.SEMICOLON -> SlangTextAttributes.SLANG_SEMICOLON_TEXT
            TokenType.BAD_CHARACTER -> SlangTextAttributes.SLANG_BAD_CHARACTER_TEXT
            else -> null
        }
    }


    override fun getTokenHighlights(element: IElementType): Array<TextAttributesKey> {
        return pack(mapTokenToTextAttribute(element))
    }

}