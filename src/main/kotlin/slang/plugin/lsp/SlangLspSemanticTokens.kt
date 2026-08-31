package slang.plugin.lsp

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.platform.lsp.api.customization.LspSemanticTokensSupport
import com.intellij.psi.PsiFile
import com.jetbrains.rd.util.string.printToString
import slang.plugin.highlight.SlangColors

/**
 * Maps the semantic tokens reported by slangd onto the plugin's own colour keys, so the
 * "Slang" colour-settings page controls the language-server highlighting too.
 *
 * slangd's legend (see slang-language-server-semantic-tokens.h) is 10 flat token types with no
 * modifiers, so [tokenModifiers] is always empty and there is no modifier-based styling to do.
 */
class SlangLspSemanticTokens : LspSemanticTokensSupport() {

    override fun shouldAskServerForSemanticTokens(psiFile: PsiFile): Boolean {
        return true
    }

    override fun getTextAttributesKey(tokenType: String, modifiers: List<String>): TextAttributesKey? {
        print(tokenType)
        print(modifiers.printToString())
        return when (tokenType) {
            "type" -> SlangColors.TYPE
            "function" -> SlangColors.FUNCTION
            "parameter" -> SlangColors.PARAMETER
            "property" -> SlangColors.PROPERTY
            "variable" -> SlangColors.VARIABLE
            "namespace" -> SlangColors.NAMESPACE
            "enumMember" -> SlangColors.ENUM_MEMBER
            "macro" -> SlangColors.MACRO
            "keyword" -> SlangColors.KEYWORD
            "string" -> SlangColors.STRING
            else -> null
        }
    }
}
