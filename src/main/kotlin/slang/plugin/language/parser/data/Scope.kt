package slang.plugin.language.parser.data

import com.intellij.psi.tree.IElementType
import slang.plugin.psi.types.SlangTypes

class Scope(val type: IElementType, parent: Scope?, namespaceName: String? = null) {

    val namespaceName: String
    var syntaxDeclarations = HashMap<String, SyntaxDeclaration>()
    var parent: Scope? = null
    var nextSibling: Scope? = null

    init {
        assert(type == SlangTypes.NAMESPACE_DECLARATION && namespaceName != null)
        this.parent = parent
        this.namespaceName = namespaceName ?: parent?.namespaceName ?: "::"
    }
}
