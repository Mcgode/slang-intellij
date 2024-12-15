package slang.plugin.psi.types.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import slang.plugin.psi.types.SlangIntrinsicAsmStatement

class SlangIntrinsicAsmStatementImpl(node: ASTNode) : ASTWrapperPsiElement(node), SlangIntrinsicAsmStatement {
}