package slang.plugin.psi.utils

import com.intellij.util.alsoIfNull
import org.intellij.markdown.lexer.push
import slang.plugin.language.parser.data.MacroExpansion
import slang.plugin.language.parser.data.TokenData
import slang.plugin.psi.SlangMacroArgumentToken
import slang.plugin.psi.types.SlangTypes

data class ExpandedMacro(
    val dynamicTokens: ArrayList<TokenData> = ArrayList()
)
{
    constructor(macroExpansion: MacroExpansion, parameters: List<List<TokenData>>) : this() {
        for (element in macroExpansion.content) {
            (element.token as? SlangMacroArgumentToken)?.let {
                val index = it.argumentIndex
                val macroArgument = macroExpansion.arguments[index]

                // If variadic, unroll all variadic arguments
                if (macroArgument.isVariadic && parameters.size > index) {
                    for (tokenData in parameters[index])
                        dynamicTokens.add(tokenData)

                    for (i in index + 1 until parameters.size) {
                        dynamicTokens.add(TokenData(SlangTypes.COMMA, ","))
                        for (tokenData in parameters[i])
                            dynamicTokens.add(tokenData)
                    }
                }

                // Unroll non-variadic argument
                else if (!macroArgument.isVariadic) {
                    for (tokenData in parameters[index])
                        dynamicTokens.add(tokenData)
                }
            }
                .alsoIfNull { dynamicTokens.push(element) }
        }
    }
}