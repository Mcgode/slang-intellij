package slang.plugin.highlight

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import slang.plugin.language.SlangIcons
import javax.swing.Icon

class SlangColorSettingsPage : ColorSettingsPage {

    override fun getDisplayName(): String = "Slang"

    override fun getIcon(): Icon = SlangIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = SlangSyntaxHighlighter()

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null

    override fun getDemoText(): String = """
        #include "common.slang"
        #define SQUARE(x) ((x) * (x))

        // Vertex shader for a lit mesh
        import gfx;

        struct Vertex {
            float3 position;
            float3 normal;
        }

        [shader("vertex")]
        float4 vertexMain(Vertex v, uniform float4x4 mvp) : SV_Position {
            let world = mul(mvp, float4(v.position, 1.0));
            return world;
        }
    """.trimIndent()

    private companion object {
        val DESCRIPTORS = arrayOf(
            AttributesDescriptor("Keyword", SlangColors.KEYWORD),
            AttributesDescriptor("Built-in type", SlangColors.BUILTIN_TYPE),
            AttributesDescriptor("Identifier", SlangColors.IDENTIFIER),
            AttributesDescriptor("Number", SlangColors.NUMBER),
            AttributesDescriptor("String", SlangColors.STRING),
            AttributesDescriptor("Preprocessor directive", SlangColors.PREPROCESSOR),
            AttributesDescriptor("Comments//Line comment", SlangColors.LINE_COMMENT),
            AttributesDescriptor("Comments//Block comment", SlangColors.BLOCK_COMMENT),
            AttributesDescriptor("Braces and operators//Braces", SlangColors.BRACES),
            AttributesDescriptor("Braces and operators//Brackets", SlangColors.BRACKETS),
            AttributesDescriptor("Braces and operators//Parentheses", SlangColors.PARENTHESES),
            AttributesDescriptor("Braces and operators//Semicolon", SlangColors.SEMICOLON),
            AttributesDescriptor("Braces and operators//Comma", SlangColors.COMMA),
            AttributesDescriptor("Braces and operators//Dot", SlangColors.DOT),
            AttributesDescriptor("Braces and operators//Operator sign", SlangColors.OPERATOR),
            AttributesDescriptor("Bad character", SlangColors.BAD_CHARACTER),

            AttributesDescriptor("Language server//Type", SlangColors.TYPE),
            AttributesDescriptor("Language server//Function", SlangColors.FUNCTION),
            AttributesDescriptor("Language server//Parameter", SlangColors.PARAMETER),
            AttributesDescriptor("Language server//Property", SlangColors.PROPERTY),
            AttributesDescriptor("Language server//Variable", SlangColors.VARIABLE),
            AttributesDescriptor("Language server//Namespace", SlangColors.NAMESPACE),
            AttributesDescriptor("Language server//Enum member", SlangColors.ENUM_MEMBER),
            AttributesDescriptor("Language server//Macro", SlangColors.MACRO),
        )
    }
}
