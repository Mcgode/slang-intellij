package slang.plugin.psi.utils

import org.intellij.markdown.lexer.push
import slang.plugin.language.parser.data.MacroExpansion
import slang.plugin.language.parser.data.TokenData

data class ExpandedMacro(
    val dynamicTokens: ArrayList<TokenData> = ArrayList()
)
{
    constructor(macroExpansion: MacroExpansion, parameters: Iterable<Iterable<TokenData>>?) : this() {
        // TODO: support macro arguments

        for (element in macroExpansion.content)
            dynamicTokens.push(element)
    }
}