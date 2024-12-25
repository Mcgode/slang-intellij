package slang.plugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.util.xmlb.XmlSerializerUtil
import com.redhat.devtools.lsp4ij.LanguageServerManager

@Service(Service.Level.PROJECT)
@State(name = "SlangConfigService", storages = [Storage("slang.xml")])
class SlangConfigService: PersistentStateComponent<SlangConfigService> {
    var enableLsp: Boolean = true
    var useIntellijLspClient = intellijLspClientSupported.value
    var useLsp4ij = !useIntellijLspClient

    override fun getState(): SlangConfigService {
        return this
    }

    override fun loadState(config: SlangConfigService) {
        XmlSerializerUtil.copyBean(config, this)
    }

    companion object {
        fun getInstance(project: Project): SlangConfigService = project.getService(SlangConfigService::class.java)

        val intellijLspClientSupported = lazy {
            try {
                @Suppress("UnstableApiUsage")
                LspServerSupportProvider
                true
            }
            catch (e: NoClassDefFoundError) {
                false
            }
        }

        val lsp4ijSupported = lazy {
            try {
                LanguageServerManager.StartOptions.DEFAULT
                true
            }
            catch (e: NoClassDefFoundError) {
                false
            }
        }
    }
}