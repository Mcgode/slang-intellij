package slang.plugin.psi

import com.intellij.psi.tree.IElementType
import slang.plugin.language.SlangLanguage

class SlangTokenType public constructor(debugName: String): IElementType(debugName, SlangLanguage.INSTANCE) {

    override fun toString(): String {
        return "SlangTokenType.${super.toString()}"
    }

}