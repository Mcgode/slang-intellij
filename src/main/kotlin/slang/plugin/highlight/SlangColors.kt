package slang.plugin.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

object SlangColors {
    val LINE_COMMENT = createTextAttributesKey("SLANG_LINE_COMMENT", Default.LINE_COMMENT)
    val BLOCK_COMMENT = createTextAttributesKey("SLANG_BLOCK_COMMENT", Default.BLOCK_COMMENT)
    val KEYWORD = createTextAttributesKey("SLANG_KEYWORD", Default.KEYWORD)
    val BUILTIN_TYPE = createTextAttributesKey("SLANG_BUILTIN_TYPE", Default.KEYWORD)
    val IDENTIFIER = createTextAttributesKey("SLANG_IDENTIFIER", Default.IDENTIFIER)
    val NUMBER = createTextAttributesKey("SLANG_NUMBER", Default.NUMBER)
    val STRING = createTextAttributesKey("SLANG_STRING", Default.STRING)
    val PREPROCESSOR = createTextAttributesKey("SLANG_PREPROCESSOR", Default.METADATA)
    val BRACES = createTextAttributesKey("SLANG_BRACES", Default.BRACES)
    val BRACKETS = createTextAttributesKey("SLANG_BRACKETS", Default.BRACKETS)
    val PARENTHESES = createTextAttributesKey("SLANG_PARENTHESES", Default.PARENTHESES)
    val SEMICOLON = createTextAttributesKey("SLANG_SEMICOLON", Default.SEMICOLON)
    val COMMA = createTextAttributesKey("SLANG_COMMA", Default.COMMA)
    val DOT = createTextAttributesKey("SLANG_DOT", Default.DOT)
    val OPERATOR = createTextAttributesKey("SLANG_OPERATOR", Default.OPERATION_SIGN)
    val BAD_CHARACTER = createTextAttributesKey("SLANG_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

    fun keys(key: TextAttributesKey): Array<TextAttributesKey> = arrayOf(key)
    val EMPTY: Array<TextAttributesKey> = emptyArray()
}
