package slang.plugin.lang

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import slang.plugin.language.SlangFileType
import slang.plugin.language.SlangLanguage

class SlangFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, SlangLanguage) {
    override fun getFileType(): FileType = SlangFileType.INSTANCE
    override fun toString(): String = "Slang File"
}
