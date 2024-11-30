package slang.plugin.psi

import com.intellij.psi.tree.TokenSet;
import slang.plugin.psi.SlangTypes

interface SlangTokenSets {

    companion object {
        val COMMENTS: TokenSet
            get() = TokenSet.create(SlangTypes.LINE_COMMENT, SlangTypes.MULTILINE_COMMENT)
        val IDENTIFIERS: TokenSet
            get() = TokenSet.create(SlangTypes.IDENTIFIER)
    }

}