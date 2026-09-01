package slang.plugin.lsp

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.Lsp4jServerWrapper
import com.intellij.platform.lsp.api.LspClientManager
import com.intellij.platform.lsp.api.LspServer
import org.eclipse.lsp4j.Hover
import org.eclipse.lsp4j.services.LanguageServer
import org.eclipse.lsp4j.services.TextDocumentService
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
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
            sanitizingServer(lsp4jServer)
        } else {
            lsp4jServer
        }
}

/** A [Hover] with `null` contents is an invalid response; treat it as "no hover". */
internal fun sanitizeHover(hover: Hover?): Hover? =
    if (hover != null && hover.contents == null) null else hover

/**
 * Wraps [delegate] so that `textDocument/hover` responses go through [sanitizeHover]. Everything
 * else is forwarded untouched.
 *
 * lsp4j's [LanguageServer] / [TextDocumentService] have ~50 `default` methods between them, and
 * Kotlin interface delegation (`by`) does not generate forwarders for Java `default` methods, so a
 * hand-written wrapper would silently break every feature it forgot to list. A reflective proxy
 * forwards the whole surface and stays correct as lsp4j grows.
 */
internal fun sanitizingServer(delegate: LanguageServer): LanguageServer {
    val textDocumentService = forwardingProxy(TextDocumentService::class.java, delegate.textDocumentService) { method, args ->
        if (method.name == "hover") {
            @Suppress("UNCHECKED_CAST")
            (method.invoke(delegate.textDocumentService, *args) as CompletableFuture<Hover?>).thenApply(::sanitizeHover)
        } else {
            NOT_HANDLED
        }
    }
    return forwardingProxy(LanguageServer::class.java, delegate) { method, _ ->
        if (method.name == "getTextDocumentService") textDocumentService else NOT_HANDLED
    }
}

private val NOT_HANDLED = Any()

/**
 * A [Proxy] implementing [iface] that forwards every call to [delegate], except where [intercept]
 * returns something other than [NOT_HANDLED].
 */
@Suppress("UNCHECKED_CAST")
private fun <T> forwardingProxy(iface: Class<T>, delegate: Any, intercept: (Method, Array<Any?>) -> Any?): T =
    Proxy.newProxyInstance(iface.classLoader, arrayOf(iface), object : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<Any?>?): Any? {
            val safeArgs = args ?: EMPTY_ARGS
            val intercepted = intercept(method, safeArgs)
            if (intercepted !== NOT_HANDLED) return intercepted
            return try {
                method.invoke(delegate, *safeArgs)
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        }
    }) as T

private val EMPTY_ARGS = arrayOfNulls<Any?>(0)
