package slang.plugin.editor

import com.intellij.lexer.Lexer
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.search.IndexPatternBuilder
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import slang.plugin.language.SlangLanguage
import slang.plugin.lexer.SlangLexer
import slang.plugin.lexer.SlangTokenTypes

/**
 * Feeds Slang / GLSL / HLSL comments to the platform's TODO machinery, so TODO / FIXME markers and
 * any custom index patterns are highlighted and listed in the TODO tool window.
 */
class SlangIndexPatternBuilder : IndexPatternBuilder {

    private fun handles(file: PsiFile) = file.language.isKindOf(SlangLanguage)

    override fun getIndexingLexer(file: PsiFile): Lexer? = if (handles(file)) SlangLexer() else null

    override fun getCommentTokenSet(file: PsiFile): TokenSet? =
        if (handles(file)) SlangTokenTypes.COMMENTS else null

    // Skip the two-character comment opener so the pattern offset lands on the comment text.
    override fun getCommentStartDelta(tokenType: IElementType): Int =
        if (tokenType == SlangTokenTypes.LINE_COMMENT || tokenType == SlangTokenTypes.BLOCK_COMMENT) 2 else 0

    // Skip the two-character block-comment closer.
    override fun getCommentEndDelta(tokenType: IElementType): Int =
        if (tokenType == SlangTokenTypes.BLOCK_COMMENT) 2 else 0
}
