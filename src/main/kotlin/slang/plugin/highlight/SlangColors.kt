package slang.plugin.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey

object SlangColors {
    // Semantic tokens reported by the Slang language server (slangd). slangd sends 10 token types
    // and no modifiers; these are mapped in SlangLspSemanticTokens.
    val TYPE = createTextAttributesKey("SLANG_TYPE", Default.CLASS_NAME)
    val FUNCTION = createTextAttributesKey("SLANG_FUNCTION", Default.FUNCTION_DECLARATION)
    val PARAMETER = createTextAttributesKey("SLANG_PARAMETER", Default.PARAMETER)
    val PROPERTY = createTextAttributesKey("SLANG_PROPERTY", Default.INSTANCE_FIELD)
    val VARIABLE = createTextAttributesKey("SLANG_VARIABLE", Default.LOCAL_VARIABLE)
    val NAMESPACE = createTextAttributesKey("SLANG_NAMESPACE", Default.CLASS_REFERENCE)
    val ENUM_MEMBER = createTextAttributesKey("SLANG_ENUM_MEMBER", Default.CONSTANT)
    val MACRO = createTextAttributesKey("SLANG_MACRO", Default.METADATA)

    val LINE_COMMENT = createTextAttributesKey("SLANG_LINE_COMMENT", Default.LINE_COMMENT)
    val BLOCK_COMMENT = createTextAttributesKey("SLANG_BLOCK_COMMENT", Default.BLOCK_COMMENT)
    val KEYWORD = createTextAttributesKey("SLANG_KEYWORD", Default.KEYWORD)
    val BUILTIN_TYPE = createTextAttributesKey("SLANG_BUILTIN_TYPE", TYPE)
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
