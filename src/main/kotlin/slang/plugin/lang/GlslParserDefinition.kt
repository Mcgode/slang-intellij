package slang.plugin.lang

import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import slang.plugin.language.GlslLanguage

/**
 * Same lexer-only definition as [SlangParserDefinition], but producing a [GlslFile] so a `.glsl`
 * document reports the GLSL file type rather than Slang.
 */
class GlslParserDefinition : SlangParserDefinition() {

    override fun getFileNodeType(): IFileElementType = FILE

    override fun createFile(viewProvider: FileViewProvider): PsiFile = GlslFile(viewProvider)

    companion object {
        val FILE = IFileElementType(GlslLanguage)
    }
}
