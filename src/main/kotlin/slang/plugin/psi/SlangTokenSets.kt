package slang.plugin.psi

import com.intellij.psi.tree.TokenSet;
import slang.plugin.language.psi.SlangTypes

interface SlangTokenSets {

    companion object {
        val COMMENTS: TokenSet
            get() = TokenSet.create(SlangTypes.PREDEFINED_MACROS)
        val IDENTIFIERS: TokenSet
            get() = TokenSet.create(SlangTypes.KEY)
    }

}