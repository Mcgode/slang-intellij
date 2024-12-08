package slang.plugin.psi

import com.intellij.psi.tree.IElementType
import slang.plugin.language.SlangLanguage

class SlangElementType public constructor(debugName: String): IElementType(debugName, SlangLanguage.INSTANCE) {

    override fun toString(): String {
        return "SlangElementType.${super.toString()}"
    }

}