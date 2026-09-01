package slang.plugin.lsp

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspClientManager

/**
 * Stops and (re)starts the Slang language client. Call this after anything that changes which
 * `slangd` runs or what it is initialised with — the settings UI on Apply, or [SlangdDownload]
 * once a managed `slangd` has been installed.
 */
object SlangLspRestart {

    fun restart(project: Project) {
        val manager = LspClientManager.getInstance(project)
        manager.stopClients(SlangLspIntegrationProvider::class.java)
        manager.startClientsIfNeeded(SlangLspIntegrationProvider::class.java)
    }
}
