package slang.plugin.psi.types.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import slang.plugin.psi.types.SlangInitializerList

class SlangInitializerListImpl(node: ASTNode) : ASTWrapperPsiElement(node), SlangInitializerList {
}