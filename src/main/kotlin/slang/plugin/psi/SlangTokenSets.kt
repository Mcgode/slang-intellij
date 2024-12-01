package slang.plugin.psi

import com.intellij.psi.tree.TokenSet;
import slang.plugin.psi.SlangTypes.*

interface SlangTokenSets {

    companion object {

        val COMMENTS: TokenSet
            get() = TokenSet.create(LINE_COMMENT, MULTILINE_COMMENT)

        val IDENTIFIERS: TokenSet
            get() = TokenSet.create(IDENTIFIER)

        val VARIABLES: TokenSet
            get() = TokenSet.create(VARIABLE_IDENTIFIER)

        val KEYWORDS: TokenSet
            get() = TokenSet.create(
                NAMESPACE, ENUM, ENUM_CLASS, STRUCT, CLASS, INTERFACE,
                CONST, IN, OUT,
                )

        val BUILTINS: TokenSet
            get() = TokenSet.create(
                VOID,
                )

        val NUMERIC_LITERALS: TokenSet
            get() = TokenSet.create(UINT_LITERAL, INT_LITERAL, FLOAT_LITERAL, DOUBLE_LITERAL)

    }

}