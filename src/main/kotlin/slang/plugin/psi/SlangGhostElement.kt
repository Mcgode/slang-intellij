package slang.plugin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.LeafPsiElement

class SlangGhostElement(node: ASTNode): LeafPsiElement(node.elementType, "SlangGhostElement") {
    override fun toString(): String = "SlangGhostElement(${elementType})"
}