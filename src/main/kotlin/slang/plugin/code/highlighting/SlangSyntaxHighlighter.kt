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
import slang.plugin.psi.SlangTypes

class SlangSyntaxHighlighter: SyntaxHighlighterBase() {

    object SlangTextAttributes {
        val SLANG_VARIABLE_TEXT = createTextAttributesKey("Slang.Variable", IDENTIFIER)
        val SLANG_USER_DEFINED_TYPE_TEXT = createTextAttributesKey("Slang.UserDefinedType", CLASS_NAME)
        val SLANG_BUILTIN_NAME_TEXT = createTextAttributesKey("Slang.BuiltinName", IDENTIFIER)
        val SLANG_KEYWORD_TEXT = createTextAttributesKey("Slang.Keyword", KEYWORD)
        val SLANG_NUMERIC_LITERALS_TEXT = createTextAttributesKey("Slang.Keyword", NUMBER)
        val SLANG_LINE_COMMENT_TEXT = createTextAttributesKey("Slang.LineComment", LINE_COMMENT)
        val SLANG_MULTILINE_COMMENT_TEXT = createTextAttributesKey("Slang.MultilineComment", BLOCK_COMMENT)
        val SLANG_BAD_CHARACTER_TEXT = createTextAttributesKey("Slang.BadCharacter", HighlighterColors.BAD_CHARACTER)
    }

    override fun getHighlightingLexer(): Lexer {
        return SlangLexerAdapter()
    }

    private fun mapTokenToTextAttribute(tokenType: IElementType): TextAttributesKey? {
        return when (tokenType) {
            in (SlangTokenSets.VARIABLES) -> SlangTextAttributes.SLANG_VARIABLE_TEXT
            in (SlangTokenSets.BUILTINS) -> SlangTextAttributes.SLANG_BUILTIN_NAME_TEXT
            in (SlangTokenSets.KEYWORDS) -> SlangTextAttributes.SLANG_KEYWORD_TEXT
            in (SlangTokenSets.NUMERIC_LITERALS) -> SlangTextAttributes.SLANG_NUMERIC_LITERALS_TEXT
            SlangTypes.LINE_COMMENT -> SlangTextAttributes.SLANG_LINE_COMMENT_TEXT
            SlangTypes.MULTILINE_COMMENT -> SlangTextAttributes.SLANG_MULTILINE_COMMENT_TEXT
            TokenType.BAD_CHARACTER -> SlangTextAttributes.SLANG_BAD_CHARACTER_TEXT
            else -> null
        }
    }


    override fun getTokenHighlights(element: IElementType): Array<TextAttributesKey> {
        return pack(mapTokenToTextAttribute(element))
    }

}