package slang.plugin.psi.types.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import slang.plugin.psi.types.SlangAttributeTargetModifier

class SlangAttributeTargetModifierImpl(node: ASTNode) : ASTWrapperPsiElement(node), SlangAttributeTargetModifier {
}