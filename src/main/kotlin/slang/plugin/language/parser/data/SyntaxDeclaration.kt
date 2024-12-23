package slang.plugin.language.parser.data

import com.intellij.lang.PsiBuilder
import slang.plugin.psi.SlangElementType

data class SyntaxDeclaration(val name: String, val type: Type) {

    enum class Type {
        Modifier,
        Declaration,
        Expression
    }

    var parseCallback: ((PsiBuilder, Int) -> Boolean)? = null
    var elementSimpleCast: SlangElementType? = null
}
