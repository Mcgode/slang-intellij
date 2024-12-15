package slang.plugin.language.parser.data

import com.intellij.psi.tree.IElementType

class Scope(val type: IElementType) {
    var syntaxDeclarations = HashMap<String, SyntaxDeclaration>()
    var parent: Scope? = null
    var nextSibling: Scope? = null
}
