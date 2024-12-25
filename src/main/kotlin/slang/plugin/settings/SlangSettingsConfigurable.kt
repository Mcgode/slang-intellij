package slang.plugin.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class SlangSettingsConfigurable(val project: Project): Configurable {

    private val configPanel = SlangConfigPanel(project)

    override fun createComponent(): JComponent? {
        reset()
        return configPanel.configPanel
    }

    override fun isModified(): Boolean {
        val configService = SlangConfigService.getInstance(project)
        return configPanel.enableLsp != configService.enableLsp
                || configPanel.useIntellijLspClient != configService.useIntellijLspClient
                || configPanel.useLsp4ij != configService.useLsp4ij
    }

    override fun apply() {
        val configService = SlangConfigService.getInstance(project)
        configService.enableLsp = configPanel.enableLsp
        configService.useIntellijLspClient = configPanel.useIntellijLspClient
        configService.useLsp4ij = configPanel.useLsp4ij
    }

    override fun getDisplayName(): String {
        return "Slang"
    }
}