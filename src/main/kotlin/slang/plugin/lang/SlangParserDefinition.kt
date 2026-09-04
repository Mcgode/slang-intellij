package slang.plugin.lang

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import slang.plugin.language.SlangLanguage
import slang.plugin.lexer.SlangLexer
import slang.plugin.lexer.SlangTokenTypes

/**
 * Lexer-only parser definition: it produces a flat PSI file whose children are the lexer tokens.
 * There is no real grammar here by design (see the LSP-first architecture). It exists so the editor
 * has a PSI file, which brace matching, the commenter, word selection and folding build on.
 */
open class SlangParserDefinition : ParserDefinition {

    override fun createLexer(project: Project?): Lexer = SlangLexer()

    override fun getCommentTokens(): TokenSet = SlangTokenTypes.COMMENTS

    override fun getStringLiteralElements(): TokenSet = SlangTokenTypes.STRINGS

    override fun createParser(project: Project?): PsiParser = PsiParser { root, builder ->
        val mark = builder.mark()
        while (!builder.eof()) {
            builder.advanceLexer()
        }
        mark.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE

    override fun createFile(viewProvider: FileViewProvider): PsiFile = SlangFile(viewProvider)

    override fun createElement(node: ASTNode): PsiElement =
        throw AssertionError("Slang has no composite elements: ${node.elementType}")

    companion object {
        val FILE = IFileElementType(SlangLanguage)
    }
}
