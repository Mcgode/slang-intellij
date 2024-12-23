package slang.plugin.code.highlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.ui.JBColor

object SlangTextAttributes {
    val SLANG_BAD_CHARACTER_TEXT = createTextAttributesKey("Slang.BadCharacter", HighlighterColors.BAD_CHARACTER)

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

    val SLANG_FUNCTION_DECL_TEXT = createTextAttributesKey("Slang.FunctionDeclaration", FUNCTION_DECLARATION)
    val SLANG_PARAMETER_TEXT = createTextAttributesKey("Slang.Parameter", PARAMETER)

    val SLANG_KEYWORDS_TEXT = createTextAttributesKey("Slang.Keywords", KEYWORD)

    val SLANG_MACRO_TEXT = createTextAttributesKey(
        "Slang.Macro",
        TextAttributes(JBColor(0x1F542E, 0x908D25), null, null, null, 0))

    val SLANG_TYPE_TEXT = createTextAttributesKey("Slang.Type", CLASS_NAME)
    val SLANG_STRUCT_NAME_TEXT = createTextAttributesKey("Slang.StructName", SLANG_TYPE_TEXT)
    val SLANG_CLASS_NAME_TEXT = createTextAttributesKey("Slang.ClassName", SLANG_TYPE_TEXT)
    val SLANG_NAMESPACE_TEXT = createTextAttributesKey("Slang.Namespace", SLANG_STRUCT_NAME_TEXT)

    val SLANG_VARIABLE_TEXT = createTextAttributesKey("Slang.Variable", LOCAL_VARIABLE)
    val SLANG_INSTANCE_VARIABLE = createTextAttributesKey("Slang.InstanceVariable", INSTANCE_FIELD)

    val SLANG_ENUM_MEMBER_TEXT = createTextAttributesKey("Slang.EnumMember", CONSTANT)
}