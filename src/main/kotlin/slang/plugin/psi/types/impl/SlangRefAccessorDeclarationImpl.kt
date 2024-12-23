package slang.plugin.psi.types.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import slang.plugin.psi.types.SlangRefAccessorDeclaration

class SlangRefAccessorDeclarationImpl(node: ASTNode): ASTWrapperPsiElement(node), SlangRefAccessorDeclaration {
}
