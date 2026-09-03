package slang.plugin.lang

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import slang.plugin.language.HlslFileType
import slang.plugin.language.HlslLanguage

class HlslFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, HlslLanguage) {
    override fun getFileType(): FileType = HlslFileType.INSTANCE
    override fun toString(): String = "HLSL File"
}
