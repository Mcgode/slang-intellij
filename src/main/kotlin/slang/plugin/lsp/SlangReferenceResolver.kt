package slang.plugin.lsp

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.platform.lsp.api.LspClient
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.platform.lsp.api.LspServerState
import org.eclipse.lsp4j.DefinitionParams
import org.eclipse.lsp4j.Position
import slang.plugin.editor.SlangOccurrences
import java.net.URI
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Turns "occurrences of the identifier under the caret" into "occurrences that resolve to the same
 * declaration", using slangd's `textDocument/definition` (slangd has no `documentHighlight`).
 *
 * Each candidate occurrence is resolved once and cached per (file, document version); repeat
 * highlights of the same name are then free. Anything that can't be resolved cleanly makes the
 * caller fall back to a plain textual match; if a request times out, the resolver stays on textual
 * matching for that client instance.
 */
@Service(Service.Level.PROJECT)
class SlangReferenceResolver(private val project: Project) {

    /** A declaration location, normalised for equality. */
    internal data class DefKey(val path: String, val line: Int, val character: Int)

    private class FileCache(val stamp: Long) {
        val byRange = ConcurrentHashMap<TextRange, DefKey>()
    }

    private val cache = ConcurrentHashMap<VirtualFile, FileCache>()

    /** The `LspClient` a `definition` request timed out against — stay on textual matching for it. */
    @Volatile
    private var degradedClient: LspClient? = null

    /**
     * Ranges of the identifier at [caretOffset] that resolve to the same declaration as the caret,
     * or `null` if that could not be determined.
     */
    fun sameDeclarationOccurrences(file: VirtualFile, document: Document, caretOffset: Int): List<TextRange>? {
        val text = document.immutableCharSequence
        val caret = SlangOccurrences.identifierAt(text, caretOffset) ?: return null
        val name = text.subSequence(caret.startOffset, caret.endOffset)
        val candidates = SlangOccurrences.occurrencesOf(text, name)
        if (candidates.size < 2) return candidates
        if (candidates.size > MAX_CANDIDATES) return null

        val client = runningClient() ?: return null
        if (client === degradedClient) return null
        val fileCache = cache.compute(file) { _, existing ->
            if (existing != null && existing.stamp == document.modificationStamp) existing
            else FileCache(document.modificationStamp)
        }!!
        evictIfNeeded(keep = file)

        val deadline = System.nanoTime() + BUDGET_NANOS
        val positionOf: (TextRange) -> Pair<Int, Int> = { r ->
            val l = document.getLineNumber(r.startOffset)
            l to (r.startOffset - document.getLineStartOffset(l))
        }
        val resolve: (TextRange) -> DefKey? = fn@{ range ->
            fileCache.byRange[range]?.let { return@fn it }
            if (client === degradedClient || System.nanoTime() > deadline) return@fn null
            val key = queryDefinition(client, file, document, range) ?: return@fn null
            fileCache.byRange[range] = key
            key
        }

        val caretKey = resolve(caret) ?: return null
        val kept = filterSameDeclaration(caret, caretKey, candidates, positionOf, resolve)
        return if (client === degradedClient) null else kept
    }

    private fun runningClient(): LspClient? =
        LspClientManager.getInstance(project)
            .getClients(SlangLspIntegrationProvider::class.java)
            .firstOrNull { it.state == LspServerState.Running }

    private fun queryDefinition(client: LspClient, file: VirtualFile, document: Document, range: TextRange): DefKey? {
        val line = document.getLineNumber(range.startOffset)
        val character = range.startOffset - document.getLineStartOffset(line)
        val params = DefinitionParams(client.getDocumentIdentifier(file), Position(line, character))

        val startNanos = System.nanoTime()
        val result = try {
            client.sendRequestSync(TIMEOUT_MS) { server -> server.textDocumentService.definition(params) }
        } catch (e: ProcessCanceledException) {
            throw e
        } catch (e: Throwable) {
            LOG.debug("textDocument/definition failed", e)
            degradedClient = client
            return null
        }
        // sendRequestSync returns null both for a proper empty result and for a timeout; a timeout
        // takes the full budget. Slow null => the server didn't answer => stop querying it.
        if (result == null && (System.nanoTime() - startNanos) >= TIMEOUT_NANOS - SLACK_NANOS) {
            LOG.info("slangd did not answer textDocument/definition; occurrence highlighting falls back to textual matching")
            degradedClient = client
            return null
        }

        val target: Pair<String, Position>? = when {
            result == null -> null
            result.isLeft -> result.left.firstOrNull()?.let { it.uri to it.range.start }
            result.isRight -> result.right.firstOrNull()?.let {
                it.targetUri to (it.targetSelectionRange ?: it.targetRange).start
            }
            else -> null
        }
        // An empty result means the token *is* a declaration; key it to its own position so a use
        // and its declaration compare equal.
        val (uri, pos) = target ?: (client.getDocumentIdentifier(file).uri to Position(line, character))
        return DefKey(pathOf(uri, file), pos.line, pos.character)
    }

    /** URI → the same [VirtualFile.path] we use for [file], so same-file matches compare equal. */
    private fun pathOf(uri: String, file: VirtualFile): String {
        val vf = runCatching { VirtualFileManager.getInstance().findFileByUrl(uri) }.getOrNull()
        return when {
            vf == file -> file.path
            vf != null -> vf.path
            else -> normalizePath(uri)
        }
    }

    private fun evictIfNeeded(keep: VirtualFile) {
        if (cache.size <= MAX_FILES) return
        cache.keys.asSequence().filter { it != keep }.take(cache.size - MAX_FILES).forEach { cache.remove(it) }
    }

    internal companion object {
        const val MAX_CANDIDATES = 40
        const val TIMEOUT_MS = 250
        private const val MAX_FILES = 8
        private val TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(TIMEOUT_MS.toLong())
        private val SLACK_NANOS = TimeUnit.MILLISECONDS.toNanos(60)
        private val BUDGET_NANOS = TimeUnit.MILLISECONDS.toNanos(1500)
        private val LOG = logger<SlangReferenceResolver>()

        /**
         * Keep candidates that resolve to the same declaration as the caret ([caret] always kept).
         * A candidate sitting at [caretKey]'s position *is* that declaration and is kept without a
         * [resolve] call.
         */
        internal fun filterSameDeclaration(
            caret: TextRange,
            caretKey: DefKey,
            candidates: List<TextRange>,
            positionOf: (TextRange) -> Pair<Int, Int>,
            resolve: (TextRange) -> DefKey?,
        ): List<TextRange> = candidates.filter { c ->
            if (c == caret) return@filter true
            val (line, character) = positionOf(c)
            if (line == caretKey.line && character == caretKey.character) return@filter true
            resolve(c)?.let { it == caretKey || (it.line == caretKey.line && it.character == caretKey.character) } == true
        }

        /** `file:` URI → a comparable path (decoded, `.`/`..` collapsed). Falls back to the raw string. */
        internal fun normalizePath(uri: String): String = try {
            Paths.get(URI(uri)).normalize().toString()
        } catch (e: Exception) {
            uri
        }
    }
}
