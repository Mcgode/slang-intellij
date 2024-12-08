package slang.plugin.psi

import com.intellij.psi.tree.IFileElementType
import slang.plugin.language.SlangLanguage

class SlangIFileElementType : IFileElementType(SlangLanguage.INSTANCE) {
}