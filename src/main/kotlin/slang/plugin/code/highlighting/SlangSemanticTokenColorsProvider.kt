package slang.plugin.code.highlighting

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors.*
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.psi.PsiFile
import com.redhat.devtools.lsp4ij.features.semanticTokens.SemanticTokensColorsProvider
import com.redhat.devtools.lsp4ij.features.semanticTokens.SemanticTokensHighlightingColors.MACRO

class SlangSemanticTokenColorsProvider: SemanticTokensColorsProvider {
    override fun getTextAttributesKey(
        tokenType: String,
        modifiers: MutableList<String>,
        file: PsiFile): TextAttributesKey?
    {
        return when (tokenType) {
            "type" -> SlangTextAttributes.SLANG_TYPE_TEXT
            "enumMember" -> createTextAttributesKey("Slang.LSP.enumMember", SlangTextAttributes.SLANG_ENUM_MEMBER_TEXT)
            "variable" -> createTextAttributesKey("Slang.LSP.variable", SlangTextAttributes.SLANG_VARIABLE_TEXT)
            "parameter" -> createTextAttributesKey("Slang.LSP.parameter", SlangTextAttributes.SLANG_PARAMETER_TEXT)
            "function" -> createTextAttributesKey("Slang.LSP.function", SlangTextAttributes.SLANG_FUNCTION_DECL_TEXT)
            "property" -> createTextAttributesKey("Slang.LSP.property", SlangTextAttributes.SLANG_INSTANCE_VARIABLE)
            "namespace" -> createTextAttributesKey("Slang.LSP.namespace", SlangTextAttributes.SLANG_NAMESPACE_TEXT)
            "keyword" -> createTextAttributesKey("Slang.LSP.keyword", SlangTextAttributes.SLANG_KEYWORDS_TEXT)
            "macro" -> createTextAttributesKey("Slang.LSP.macro", SlangTextAttributes.SLANG_MACRO_TEXT)
            "string" -> createTextAttributesKey("Slang.LSP.string", SlangTextAttributes.SLANG_STRING_LITERAL_TEXT)
            else -> null
        }
    }
}