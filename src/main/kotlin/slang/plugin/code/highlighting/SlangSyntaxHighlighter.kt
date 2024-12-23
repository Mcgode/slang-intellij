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
import slang.plugin.psi.types.SlangTypes

class SlangSyntaxHighlighter: SyntaxHighlighterBase() {

    override fun getHighlightingLexer(): Lexer {
        return SlangLexerAdapter()
    }

    private fun mapTokenToTextAttribute(tokenType: IElementType): TextAttributesKey? {
        return when (tokenType) {
            in (SlangTokenSets.NUMERIC_LITERALS) -> SlangTextAttributes.SLANG_NUMERIC_LITERALS_TEXT
            in (SlangTokenSets.BRACES) -> SlangTextAttributes.SLANG_BRACE_TEXT
            in (SlangTokenSets.PARENTHESIS) -> SlangTextAttributes.SLANG_PAREN_TEXT
            in (SlangTokenSets.BRACKETS) -> SlangTextAttributes.SLANG_BRACKET_TEXT
            SlangTypes.STRING_LITERAL -> SlangTextAttributes.SLANG_STRING_LITERAL_TEXT
            SlangTypes.LINE_COMMENT -> SlangTextAttributes.SLANG_LINE_COMMENT_TEXT
            SlangTypes.MULTILINE_COMMENT -> SlangTextAttributes.SLANG_MULTILINE_COMMENT_TEXT
            SlangTypes.COMMA -> SlangTextAttributes.SLANG_COMMA_TEXT
            SlangTypes.DOT -> SlangTextAttributes.SLANG_DOT_TEXT
            SlangTypes.SEMICOLON -> SlangTextAttributes.SLANG_SEMICOLON_TEXT
            SlangTypes.FUNCTION_NAME -> SlangTextAttributes.SLANG_FUNCTION_DECL_TEXT
            SlangTypes.PARAMETER_NAME -> SlangTextAttributes.SLANG_PARAMETER_TEXT
            SlangTypes.STRUCT_NAME -> SlangTextAttributes.SLANG_STRUCT_NAME_TEXT
            SlangTypes.CLASS_NAME -> SlangTextAttributes.SLANG_CLASS_NAME_TEXT
            TokenType.BAD_CHARACTER -> SlangTextAttributes.SLANG_BAD_CHARACTER_TEXT
            else -> null
        }
    }


    override fun getTokenHighlights(element: IElementType): Array<TextAttributesKey> {
        return pack(mapTokenToTextAttribute(element))
    }

}