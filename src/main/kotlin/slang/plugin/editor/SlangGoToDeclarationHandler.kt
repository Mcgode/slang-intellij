package slang.plugin.editor

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.platform.lsp.api.LspServerState
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.eclipse.lsp4j.DefinitionParams
import org.eclipse.lsp4j.Location
import org.eclipse.lsp4j.Position
import slang.plugin.language.SlangLanguage
import slang.plugin.lsp.SlangLspIntegrationProvider

/**
 * Manual `textDocument/definition` lookup for Cmd+B / Cmd+Click.
 *
 * The platform's built-in LSP navigation resolves through an implicit PSI reference rather than a
 * [GotoDeclarationHandler]; in CLion (Nova) and Rider that path is never invoked from the
 * keyboard/mouse action (JetBrains bug CPP-51642 — only the context menu's "Declaration or usages"
 * sends the request). Registering a handler here drives `textDocument/definition` ourselves, using
 * the same code path `GotoDeclarationAction` already resolves successfully everywhere else.
 */
class SlangGoToDeclarationHandler : GotoDeclarationHandler {

    override fun getGotoDeclarationTargets(
        sourceElement: PsiElement?,
        offset: Int,
        editor: Editor?
    ): Array<PsiElement>? {
        if (sourceElement == null || !sourceElement.language.isKindOf(SlangLanguage)) return null
        val file = sourceElement.containingFile ?: return null
        val project = sourceElement.project

        val client = runningClient(project) ?: return null
        val locations = queryDefinition(client, file, offset) ?: return null
        val targets = locations.mapNotNull { resolveTarget(project, it) }
        return targets.ifEmpty { null }?.toTypedArray()
    }

    private fun runningClient(project: Project): LspClient? =
        LspClientManager.getInstance(project)
            .getClients(SlangLspIntegrationProvider::class.java)
            .firstOrNull { it.state == LspServerState.Running }

    private fun queryDefinition(client: LspClient, file: PsiFile, offset: Int): List<Location>? {
        val document = file.fileDocument
        val line = document.getLineNumber(offset)
        val column = offset - document.getLineStartOffset(line)
        val params = DefinitionParams(client.getDocumentIdentifier(file.virtualFile), Position(line, column))

        val result = try {
            client.sendRequestSync(TIMEOUT_MS) { server -> server.textDocumentService.definition(params) }
        } catch (e: ProcessCanceledException) {
            throw e
        } catch (e: Throwable) {
            LOG.debug("textDocument/definition failed", e)
            return null
        } ?: return null

        return when {
            result.isLeft -> result.left
            result.isRight -> result.right.map { Location(it.targetUri, it.targetSelectionRange ?: it.targetRange) }
            else -> null
        }
    }

    /** The lexer token at [location] in its target file — the closest thing to a "declaration" PSI has here. */
    private fun resolveTarget(project: Project, location: Location): PsiElement? {
        val virtualFile = VirtualFileManager.getInstance().findFileByUrl(location.uri) ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return null
        val document = psiFile.fileDocument
        val pos = location.range.start
        if (pos.line !in 0 until document.lineCount) return null

        val lineStart = document.getLineStartOffset(pos.line)
        val lineEnd = document.getLineEndOffset(pos.line)
        val targetOffset = (lineStart + pos.character).coerceIn(lineStart, lineEnd)
        return psiFile.findElementAt(targetOffset)
    }

    private companion object {
        val LOG = logger<SlangGoToDeclarationHandler>()

        /** More generous than the occurrence highlighter's budget: this fires once per explicit user action. */
        const val TIMEOUT_MS = 1500
    }
}
