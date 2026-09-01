package slang.plugin.editor

import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import slang.plugin.lexer.SlangLexer
import slang.plugin.lexer.SlangTokenTypes as T

/**
 * Folding driven purely by the lexer: balanced `{ }` blocks, multi-line block comments, and
 * `#if / #endif` preprocessor regions. No PSI structure required.
 */
class SlangFoldingBuilder : FoldingBuilderEx(), DumbAware {

    override fun isCollapsedByDefault(node: com.intellij.lang.ASTNode): Boolean = false

    override fun getPlaceholderText(node: com.intellij.lang.ASTNode): String = "..."

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val text = root.containingFile?.text ?: return FoldingDescriptor.EMPTY_ARRAY
        val node = root.node ?: return FoldingDescriptor.EMPTY_ARRAY
        val descriptors = ArrayList<FoldingDescriptor>()

        val lexer = SlangLexer()
        lexer.start(text)
        val braceStack = ArrayDeque<Int>()
        val ppStack = ArrayDeque<Int>()

        while (lexer.tokenType != null) {
            when (lexer.tokenType) {
                T.LBRACE -> braceStack.addLast(lexer.tokenStart)
                T.RBRACE -> {
                    val open = braceStack.removeLastOrNull()
                    if (open != null) addMultiline(descriptors, node, text, open, lexer.tokenEnd, "{...}")
                }
                T.BLOCK_COMMENT ->
                    addMultiline(descriptors, node, text, lexer.tokenStart, lexer.tokenEnd, "/*...*/")
                T.PREPROCESSOR_DIRECTIVE -> {
                    val directive = text.substring(lexer.tokenStart, lexer.tokenEnd).trimStart('#', ' ', '\t')
                    when {
                        directive.startsWith("if") -> ppStack.addLast(lexer.tokenStart)
                        directive.startsWith("endif") -> {
                            val open = ppStack.removeLastOrNull()
                            if (open != null) addMultiline(descriptors, node, text, open, lexer.tokenEnd, "#if...#endif")
                        }
                    }
                }
            }
            lexer.advance()
        }
        return descriptors.toTypedArray()
    }

    private fun addMultiline(
        out: MutableList<FoldingDescriptor>,
        node: com.intellij.lang.ASTNode,
        text: CharSequence,
        start: Int,
        end: Int,
        placeholder: String,
    ) {
        if (end <= start) return
        if (text.subSequence(start, end).indexOf('\n') < 0) return
        out.add(FoldingDescriptor(node, TextRange(start, end), null, placeholder))
    }
}
