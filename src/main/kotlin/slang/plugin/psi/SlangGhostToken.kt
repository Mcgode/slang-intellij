package slang.plugin.psi

import com.intellij.psi.tree.IElementType
import slang.plugin.language.SlangLanguage

class SlangGhostToken(private val originalToken: IElementType, val text: String): IElementType(
    "SlangGhostToken(${originalToken.debugName})",
    SlangLanguage.INSTANCE)
{
    override fun toString(): String {
        return originalToken.toString()
    }
}

