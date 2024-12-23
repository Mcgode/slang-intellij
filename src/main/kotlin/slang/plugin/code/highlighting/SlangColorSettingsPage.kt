package slang.plugin.code.highlighting

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import slang.plugin.language.SlangIcon
import javax.swing.Icon

class SlangColorSettingsPage: ColorSettingsPage {

    private val descriptors = arrayOf(
        AttributesDescriptor("Bad character", SlangTextAttributes.SLANG_BAD_CHARACTER_TEXT),
        AttributesDescriptor("Braces and operators//Braces", SlangTextAttributes.SLANG_BRACE_TEXT),
        AttributesDescriptor("Braces and operators//Brackets", SlangTextAttributes.SLANG_BRACKET_TEXT),
        AttributesDescriptor("Braces and operators//Comma", SlangTextAttributes.SLANG_COMMA_TEXT),
        AttributesDescriptor("Braces and operators//Dot", SlangTextAttributes.SLANG_DOT_TEXT),
        AttributesDescriptor("Braces and operators//Parentheses", SlangTextAttributes.SLANG_PAREN_TEXT),
        AttributesDescriptor("Braces and operators//Semicolon", SlangTextAttributes.SLANG_SEMICOLON_TEXT),
        AttributesDescriptor("Comments//Line comment", SlangTextAttributes.SLANG_LINE_COMMENT_TEXT),
        AttributesDescriptor("Comments//Block comment", SlangTextAttributes.SLANG_MULTILINE_COMMENT_TEXT),
        AttributesDescriptor("Class", SlangTextAttributes.SLANG_CLASS_NAME_TEXT),
        AttributesDescriptor("Enum constant", SlangTextAttributes.SLANG_ENUM_MEMBER_TEXT),
//        AttributesDescriptor("Functions//Call", SlangTextAttributes.SLANG_FUNCTION_CALL_TEXT),
        AttributesDescriptor("Functions//Declaration", SlangTextAttributes.SLANG_FUNCTION_DECL_TEXT),
        AttributesDescriptor("Keywords", SlangTextAttributes.SLANG_KEYWORDS_TEXT),
        AttributesDescriptor("Macro", SlangTextAttributes.SLANG_MACRO_TEXT),
        AttributesDescriptor("Number", SlangTextAttributes.SLANG_NUMERIC_LITERALS_TEXT),
        AttributesDescriptor("Parameters//Parameter", SlangTextAttributes.SLANG_PARAMETER_TEXT),
        AttributesDescriptor("Struct", SlangTextAttributes.SLANG_STRUCT_NAME_TEXT),
        AttributesDescriptor("String", SlangTextAttributes.SLANG_STRING_LITERAL_TEXT),
        AttributesDescriptor("Type", SlangTextAttributes.SLANG_TYPE_TEXT),
    )

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = descriptors
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getDisplayName(): String = "Slang"
    override fun getIcon(): Icon = SlangIcon.Icon
    override fun getHighlighter(): SyntaxHighlighter = SlangSyntaxHighlighter()

    override fun getDemoText(): String {
        return """
            // Comment

            /*
             * Block comment
             */

            #include "Platform.h"
            #define vkLocation(index) [[vk::location(index)]]

            struct IaToVs
            {
                vkLocation(0) float3 m_position: POSITION0;
                vkLocation(0) float4 m_color: COLOR0;
            };

            struct VsToPs
            {
                vkLocation(0) float4 m_color: COLOR0;
                float4 m_position: SV_POSITION;
            };

            struct PsToOm
            {
                vkLocation(0) float4 m_color: SV_TARGET0;
            };

            func myFun()
            {}

            [shader("vertex")]
            VsToPs MainVS(IaToVs _input)
            {
                VsToPs output;

                output.m_color = _input.m_color;
                output.m_position = float4(_input.m_position, 1);

                return output;
            }

            [shader("fragment")]
            PsToOm MainPS(VsToPs _input)
            {
                PsToOm output;

                output.m_color = _input.m_color;

                return output;
            }
        """.trimIndent()
    }

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? {
        return null
    }
}