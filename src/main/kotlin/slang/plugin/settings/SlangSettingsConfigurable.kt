package slang.plugin.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import slang.plugin.SlangBundle

class SlangSettingsConfigurable : BoundConfigurable(SlangBundle.message("settings.title")) {

    override fun createPanel(): DialogPanel {
        val state = SlangSettings.getInstance().state
        return panel {
            group(SlangBundle.message("settings.slangd.group")) {
                row(SlangBundle.message("settings.slangd.path")) {
                    textField()
                        .bindText({ state.slangdPath ?: "" }, { state.slangdPath = it })
                        .comment(SlangBundle.message("settings.slangd.path.comment"))
                        .columns(40)
                }
                row {
                    checkBox(SlangBundle.message("settings.slangd.autoDownload"))
                        .bindSelected({ state.autoDownload }, { state.autoDownload = it })
                }
            }
            group {
                row {
                    checkBox(SlangBundle.message("settings.inlayHints.deducedTypes"))
                        .bindSelected({ state.inlayHintsDeducedTypes }, { state.inlayHintsDeducedTypes = it })
                }
                row {
                    checkBox(SlangBundle.message("settings.inlayHints.parameterNames"))
                        .bindSelected({ state.inlayHintsParameterNames }, { state.inlayHintsParameterNames = it })
                }
            }
        }
    }
}
