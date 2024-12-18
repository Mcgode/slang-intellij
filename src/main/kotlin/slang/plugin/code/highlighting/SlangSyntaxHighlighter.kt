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

    object SlangTextAttributes {
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
        val SLANG_FUNCTION_DECL_TEXT = createTextAttributesKey("Slang.FunctionDeclaration", FUNCTION_DECLARATION)
        val SLANG_PARAMETER_TEXT = createTextAttributesKey("Slang.Parameter", PARAMETER)
        val SLANG_STRUCT_NAME_TEXT = createTextAttributesKey("Slang.StructName", CLASS_NAME)
        val SLANG_CLASS_NAME_TEXT = createTextAttributesKey("Slang.ClassName", CLASS_NAME)
    }

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