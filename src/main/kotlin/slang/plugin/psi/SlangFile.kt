package slang.plugin.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import slang.plugin.language.SlangFileType
import slang.plugin.language.SlangLanguage

class SlangFile constructor(viewProvider: FileViewProvider): PsiFileBase(viewProvider, SlangLanguage.INSTANCE) {

    override fun getFileType(): FileType {
        return SlangFileType.INSTANCE
    }

    override fun toString(): String {
        return "Slang File"
    }

}