package slang.plugin.psi

import com.intellij.psi.tree.TokenSet;
import slang.plugin.psi.types.SlangTypes

interface SlangTokenSets {

    companion object {

        val COMMENTS: TokenSet
            get() = TokenSet.create(SlangTypes.LINE_COMMENT, SlangTypes.MULTILINE_COMMENT)

        val BRACES: TokenSet
            get() = TokenSet.create(SlangTypes.LEFT_BRACE, SlangTypes.RIGHT_BRACE)

        val PARENTHESIS: TokenSet
            get() = TokenSet.create(SlangTypes.LEFT_PAREN, SlangTypes.RIGHT_PAREN)

        val BRACKETS: TokenSet
            get() = TokenSet.create(SlangTypes.LEFT_BRACKET, SlangTypes.RIGHT_BRACKET)

        val VARIABLES: TokenSet
            get() = TokenSet.create(SlangTypes.VARIABLE_NAME)

        val NUMERIC_LITERALS: TokenSet
            get() = TokenSet.create(SlangTypes.INTEGER_LITERAL, SlangTypes.FLOAT_LITERAL)

    }

}