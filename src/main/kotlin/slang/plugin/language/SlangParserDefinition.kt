package slang.plugin.language

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import slang.plugin.language.parser.SlangParser
import slang.plugin.psi.*
import slang.plugin.psi.types.SlangTypes

class SlangParserDefinition: ParserDefinition {

    override fun createLexer(project: Project?): Lexer {
        return SlangLexerAdapter()
    }

    override fun createParser(project: Project?): PsiParser {
        return SlangParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return SlangIFileElementType();
    }

    override fun getWhitespaceTokens(): TokenSet {
        return TokenSet.create(TokenType.WHITE_SPACE, SlangTypes.NEW_LINE)
    }

    override fun getCommentTokens(): TokenSet {
        return SlangTokenSets.COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.create(SlangTypes.STRING_LITERAL)
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return SlangTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return SlangFile(viewProvider)
    }

}