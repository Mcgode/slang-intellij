package slang.plugin.psi.types.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import slang.plugin.psi.types.SlangIntrinsicTypeModifier

class SlangIntrinsicTypeModifierImpl(node: ASTNode): ASTWrapperPsiElement(node), SlangIntrinsicTypeModifier {
}