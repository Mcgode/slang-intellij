package slang.plugin.lang

import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import slang.plugin.language.HlslLanguage

/**
 * Same lexer-only definition as [SlangParserDefinition], but producing an [HlslFile] so an `.hlsl`
 * document reports the HLSL file type rather than Slang.
 */
class HlslParserDefinition : SlangParserDefinition() {

    override fun getFileNodeType(): IFileElementType = FILE

    override fun createFile(viewProvider: FileViewProvider): PsiFile = HlslFile(viewProvider)

    companion object {
        val FILE = IFileElementType(HlslLanguage)
    }
}
