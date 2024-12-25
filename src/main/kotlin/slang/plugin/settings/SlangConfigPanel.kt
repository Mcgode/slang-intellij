package slang.plugin.settings

import com.intellij.openapi.observable.util.whenItemSelected
import com.intellij.openapi.project.Project
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JPanel

class SlangConfigPanel(project: Project) {
    lateinit var configPanel: JPanel

    private lateinit var lspEnabled: JCheckBox
    private lateinit var lspType: JComboBox<String>

    companion object {
        private const val INTELLIJ_TEXT = "Native IntelliJ support"
        private const val LSP4IJ_TEXT = "LSP4IJ plugin"
    }

    init {
        val configService = SlangConfigService.getInstance(project)

        lspEnabled.isSelected = configService.enableLsp
        lspEnabled.addActionListener { updateLspType() }

        lspType.removeAllItems()
        lspType.addItem(
            if (SlangConfigService.intellijLspClientSupported.value) INTELLIJ_TEXT
            else "$INTELLIJ_TEXT (unavailable on this IDE)"
        )
        lspType.addItem(
            if (SlangConfigService.lsp4ijSupported.value) LSP4IJ_TEXT
            else "$LSP4IJ_TEXT (unavailable, plugin not found)"
        )
        if (configService.useLsp4ij)
            lspType.selectedIndex = 1
        updateLspType()
    }

    private fun updateLspType() {
        lspType.isEnabled = lspEnabled.isSelected
    }

    val enableLsp: Boolean
        get() = lspEnabled.isSelected

    val useIntellijLspClient: Boolean
        get() = lspType.selectedItem == INTELLIJ_TEXT

    val useLsp4ij: Boolean
        get() = lspType.selectedItem == LSP4IJ_TEXT
}