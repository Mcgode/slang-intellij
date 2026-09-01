package slang.plugin.lsp

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.Lsp4jServerWrapper
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.platform.lsp.api.LspServer
import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.HoverParams
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Project service that installs a wrapper around slangd's lsp4j proxy to paper over
 * protocol violations that would otherwise crash the IDE's LSP support.
 *
 * Currently it fixes one thing: slangd sometimes answers `textDocument/hover` with a
 * `Hover` object whose `contents` is `null` (e.g. hovering over parts of `BRDF.slangh`).
 * The LSP spec says the *whole result* must be `null` when there is nothing to show, and
 * `com.intellij.platform.lsp.impl.LspRequestExecutor.getHoverCaching` trusts that: it copies
 * the hover via `Hover.setContents(other.getContents())`, and lsp4j's setter throws
 * `NullPointerException: Property must not be null: contents`. We normalise such a response
 * back to `null` before the platform ever sees it.
 */
@Service(Service.Level.PROJECT)
class SlangLspServerCustomization(private val project: Project) : Disposable {

    private val installed = AtomicBoolean(false)

    /** Register the wrapper once, before the first slangd client is started. */
    fun ensureInstalled() {
        if (installed.compareAndSet(false, true)) {
            LspClientManager.getInstance(project).addLsp4jServerWrapper(SlangHoverSanitizingWrapper, this)
        }
    }

    override fun dispose() = Unit
}

private object SlangHoverSanitizingWrapper : Lsp4jServerWrapper {
    override fun wrapLsp4jServer(lspServer: LspServer, lsp4jServer: LanguageServer): LanguageServer =
        if (lspServer.providerClass == SlangLspIntegrationProvider::class.java) {
            SanitizingLanguageServer(lsp4jServer)
        } else {
            lsp4jServer
        }
}

private class SanitizingLanguageServer(
    private val delegate: LanguageServer,
) : LanguageServer by delegate {

    private val textDocumentService = SanitizingTextDocumentService(delegate.textDocumentService)

    override fun getTextDocumentService(): TextDocumentService = textDocumentService
}

private class SanitizingTextDocumentService(
    private val delegate: TextDocumentService,
) : TextDocumentService by delegate {

    override fun hover(params: HoverParams): CompletableFuture<Hover> =
        delegate.hover(params).thenApply(::sanitizeHover)
}

/** A [Hover] with `null` contents is an invalid response; treat it as "no hover". */
internal fun sanitizeHover(hover: Hover?): Hover? =
    if (hover != null && hover.contents == null) null else hover
