package slang.plugin.lsp

import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.HoverParams
import org.eclipse.lsp4j.InlayHint
import org.eclipse.lsp4j.InlayHintParams
import org.eclipse.lsp4j.MarkupContent
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import org.eclipse.lsp4j.services.WorkspaceService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import java.util.concurrent.CompletableFuture

class SlangHoverSanitizerTest {

    @Test
    fun `null result is passed through`() {
        assertNull(sanitizeHover(null))
    }

    @Test
    fun `hover with null contents becomes null even when a range is set`() {
        val hover = Hover().apply { range = Range() }
        assertNull(sanitizeHover(hover))
    }

    @Test
    fun `a real hover is left untouched`() {
        val hover = Hover(MarkupContent("markdown", "float3 x"))
        assertSame(hover, sanitizeHover(hover))
    }

    @Test
    fun `wrapped server sanitizes hover but forwards everything else`() {
        val realHint = InlayHint()
        val backing = object : TextDocumentService {
            override fun hover(params: HoverParams) = CompletableFuture.completedFuture(Hover().apply { range = Range() })
            override fun inlayHint(params: InlayHintParams) = CompletableFuture.completedFuture(listOf(realHint))
            override fun didOpen(params: org.eclipse.lsp4j.DidOpenTextDocumentParams) = Unit
            override fun didChange(params: org.eclipse.lsp4j.DidChangeTextDocumentParams) = Unit
            override fun didClose(params: org.eclipse.lsp4j.DidCloseTextDocumentParams) = Unit
            override fun didSave(params: org.eclipse.lsp4j.DidSaveTextDocumentParams) = Unit
        }
        val server = object : LanguageServer {
            override fun initialize(params: org.eclipse.lsp4j.InitializeParams) = CompletableFuture.completedFuture(org.eclipse.lsp4j.InitializeResult())
            override fun shutdown() = CompletableFuture.completedFuture<Any>(null)
            override fun exit() = Unit
            override fun getTextDocumentService() = backing
            override fun getWorkspaceService(): WorkspaceService = throw UnsupportedOperationException()
        }

        val wrapped = sanitizingServer(server)

        // hover: invalid response normalised to null
        assertNull(wrapped.textDocumentService.hover(HoverParams()).get())
        // inlayHint (a Java `default` method on TextDocumentService) is forwarded, not left to throw
        assertEquals(listOf(realHint), wrapped.textDocumentService.inlayHint(InlayHintParams()).get())
    }
}
