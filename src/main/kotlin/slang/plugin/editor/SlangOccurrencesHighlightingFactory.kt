package slang.plugin.editor

import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerBase
import com.intellij.codeInsight.highlighting.HighlightUsagesHandlerFactory
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.Consumer
import slang.plugin.language.SlangFileType
import slang.plugin.lsp.SlangReferenceResolver

/**
 * Highlights other occurrences of the identifier under the caret.
 *
 * Occurrences that resolve (via slangd's `textDocument/definition`) to the same declaration as the
 * caret are highlighted — so a local `p` no longer lights up an unrelated field `p`. If slangd
 * can't be consulted (not running, too slow, indexing, LSP module absent) this falls back to a
 * plain lexer-level textual match on the identifier name.
 */
class SlangOccurrencesHighlightingFactory : HighlightUsagesHandlerFactory, DumbAware {

    override fun createHighlightUsagesHandler(editor: Editor, psiFile: PsiFile): HighlightUsagesHandlerBase<PsiElement>? {
        if (psiFile.fileType != SlangFileType.INSTANCE || editor.isOneLineMode) return null
        if (SlangOccurrences.identifierAt(editor.document.immutableCharSequence, editor.caretModel.offset) == null) return null
        return Handler(editor, psiFile)
    }

    private class Handler(editor: Editor, file: PsiFile) :
        HighlightUsagesHandlerBase<PsiElement>(editor, file), DumbAware {

        override fun getTargets(): List<PsiElement> = listOf(myFile)

        override fun selectTargets(targets: List<PsiElement>, selectionConsumer: Consumer<in List<PsiElement>>) {
            selectionConsumer.consume(targets)
        }

        override fun computeUsages(targets: List<PsiElement>) {
            val text = myEditor.document.immutableCharSequence
            val caret = SlangOccurrences.identifierAt(text, myEditor.caretModel.offset) ?: return
            val name = text.subSequence(caret.startOffset, caret.endOffset)

            val ranges = resolveScoped() ?: SlangOccurrences.occurrencesOf(text, name)
            ranges.forEach(myReadUsages::add)
        }

        /** Same-declaration occurrences from slangd, or null to fall back to a textual match. */
        private fun resolveScoped(): List<TextRange>? {
            val vFile = myFile.virtualFile ?: return null
            if (DumbService.isDumb(myFile.project)) return null
            return try {
                myFile.project.service<SlangReferenceResolver>()
                    .sameDeclarationOccurrences(vFile, myEditor.document, myEditor.caretModel.offset)
            } catch (e: ProcessCanceledException) {
                throw e
            } catch (e: Throwable) {
                null
            }
        }
    }
}
