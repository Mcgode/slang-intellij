package slang.plugin.lang

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import slang.plugin.language.GlslFileType
import slang.plugin.language.GlslLanguage

class GlslFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, GlslLanguage) {
    override fun getFileType(): FileType = GlslFileType.INSTANCE
    override fun toString(): String = "GLSL File"
}
