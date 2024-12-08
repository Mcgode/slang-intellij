package slang.plugin.psi.types.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import slang.plugin.psi.types.SlangExpression

class SlangExpressionImpl(node: ASTNode) : ASTWrapperPsiElement(node), SlangExpression {
}