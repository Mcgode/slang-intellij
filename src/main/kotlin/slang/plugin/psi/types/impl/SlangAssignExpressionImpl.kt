package slang.plugin.psi.types.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import slang.plugin.psi.types.SlangAssignExpression

class SlangAssignExpressionImpl(node: ASTNode) : ASTWrapperPsiElement(node), SlangAssignExpression {
}