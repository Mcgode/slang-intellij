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
            "enumMember" -> SlangTextAttributes.SLANG_ENUM_MEMBER_TEXT
            "variable" -> SlangTextAttributes.SLANG_VARIABLE_TEXT
            "parameter" -> SlangTextAttributes.SLANG_PARAMETER_TEXT
            "function" -> SlangTextAttributes.SLANG_FUNCTION_DECL_TEXT
            "property" -> SlangTextAttributes.SLANG_INSTANCE_VARIABLE
            "namespace" -> SlangTextAttributes.SLANG_NAMESPACE_TEXT
            "keyword" -> SlangTextAttributes.SLANG_KEYWORDS_TEXT
            "macro" -> SlangTextAttributes.SLANG_MACRO_TEXT
            "string" -> SlangTextAttributes.SLANG_STRING_LITERAL_TEXT
            else -> null
        }
    }
}