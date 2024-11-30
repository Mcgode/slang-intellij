package slang.plugin.psi

import com.intellij.lang.PsiBuilder
import slang.plugin.psi.SlangTypes.*

import slang.plugin.language.parser.SlangParserUtil

object SlangPsiUtil: SlangParserUtil() {

    /**
     *  This method differentiates between a type and a variable. If we have 2 identifiers
     *  one after the other we know the first one must be a type and the second one a variable.
     *  Normally this method is not doing much since the lexer is doing this job already, but
     *  the lexer will fail if an undeclared user type is used and would make the whole parser crash.
     *  In such cases this method will secure correct parsing.
     */
    @JvmStatic
    fun primaryExprVariable(builder: PsiBuilder, level: Int): Boolean {
//        if (!recursion_guard_(builder, level, "primary_expr_variable")) return false
        val isCurrentTokenIdentifier = builder.tokenType == IDENTIFIER
        val isNextTokenIdentifier = builder.lookAhead(1) == IDENTIFIER
        if (isCurrentTokenIdentifier && !isNextTokenIdentifier) {
            builder.advanceLexer()
            return true
        }
        return false
    }

}