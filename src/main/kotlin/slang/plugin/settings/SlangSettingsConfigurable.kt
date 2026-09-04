package slang.plugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.MutableProperty
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.rows
import com.intellij.util.concurrency.annotations.RequiresEdt
import slang.plugin.SlangBundle
import slang.plugin.language.ShaderFileAssociations
import slang.plugin.lsp.SlangLspConfig
import slang.plugin.lsp.SlangLspRestart
import slang.plugin.lsp.SlangdBinary
import slang.plugin.lsp.SlangdDownload
import slang.plugin.lsp.SlangdVersion
import javax.swing.JButton

class SlangSettingsConfigurable(private val project: Project) :
    BoundConfigurable(SlangBundle.message("settings.title")) {

    private val app get() = SlangSettings.getInstance().state
    private val proj get() = SlangProjectSettings.getInstance(project).state

    private val systemInfo = JBLabel()
    private val pluginInfo = JBLabel()
    private lateinit var downloadButton: JButton

    /**
     * The subset of settings that slangd only picks up when it (re)starts: the resolved binary
     * (source + path) and everything forwarded as `initializationOptions`. Compared across an Apply
     * so the language server is restarted only when one of these actually changed.
     */
    private data class LspRestartKey(
        val source: SlangdSource,
        val path: String,
        val config: Map<String, Any>,
    )

    private fun lspRestartKey() = LspRestartKey(
        source = app.slangdSource,
        path = app.slangdPath ?: "",
        config = SlangLspConfig.restartRelevantConfig(project),
    )

    override fun createPanel(): DialogPanel {
        val appState = app
        var lspKey = lspRestartKey()
        val dialog = panel {
            group(SlangBundle.message("settings.slangd.group")) {
                buttonsGroup(SlangBundle.message("settings.slangd.source")) {
                    row {
                        radioButton(SlangBundle.message("settings.slangd.source.system"), SlangdSource.SYSTEM)
                        radioButton(SlangBundle.message("settings.slangd.source.plugin"), SlangdSource.PLUGIN)
                    }
                }.bind(
                    object : MutableProperty<SlangdSource> {
                        override fun get() = appState.slangdSource
                        override fun set(value: SlangdSource) {
                            appState.slangdSource = value
                        }
                    },
                    SlangdSource::class.java,
                )

                row(SlangBundle.message("settings.slangd.path")) {
                    textField()
                        .bindText({ app.slangdPath ?: "" }, { app.slangdPath = it })
                        .columns(40)
                }.comment(SlangBundle.message("settings.slangd.path.comment"))

                row(SlangBundle.message("settings.slangd.systemLabel")) { cell(systemInfo) }
                row(SlangBundle.message("settings.slangd.pluginLabel")) { cell(pluginInfo) }
                row {
                    downloadButton = button(SlangBundle.message("settings.slangd.downloadButton", SlangdDownload.VERSION)) {
                        downloadButton.isEnabled = false
                        SlangdDownload.startDownload(project) {
                            ApplicationManager.getApplication().invokeLater { refresh() }
                        }
                    }.component
                }

                row {
                    checkBox(SlangBundle.message("settings.slangd.autoDownload"))
                        .bindSelected({ app.autoDownload }, { app.autoDownload = it })
                }
            }

            group(SlangBundle.message("settings.imports.group")) {
                row {
                    checkBox(SlangBundle.message("settings.searchInWorkspace"))
                        .bindSelected(
                            { proj.searchInAllWorkspaceDirectories },
                            { proj.searchInAllWorkspaceDirectories = it },
                        )
                        .comment(SlangBundle.message("settings.searchInWorkspace.comment"))
                }
                row {
                    textArea()
                        .align(AlignX.FILL)
                        .rows(4)
                        .label(SlangBundle.message("settings.searchPaths"), LabelPosition.TOP)
                        .comment(SlangBundle.message("settings.oneEntryPerLine"))
                        .bindText(
                            { proj.additionalSearchPaths.toLines() },
                            { proj.additionalSearchPaths.setLines(it) },
                        )
                }
                row {
                    textArea()
                        .align(AlignX.FILL)
                        .rows(4)
                        .label(SlangBundle.message("settings.predefinedMacros"), LabelPosition.TOP)
                        .comment(SlangBundle.message("settings.predefinedMacros.comment"))
                        .bindText(
                            { proj.predefinedMacros.toLines() },
                            { proj.predefinedMacros.setLines(it) },
                        )
                }
            }

            group(SlangBundle.message("settings.inlayHints.group")) {
                row {
                    checkBox(SlangBundle.message("settings.inlayHints.deducedTypes"))
                        .bindSelected({ app.inlayHintsDeducedTypes }, { app.inlayHintsDeducedTypes = it })
                }
                row {
                    checkBox(SlangBundle.message("settings.inlayHints.parameterNames"))
                        .bindSelected({ app.inlayHintsParameterNames }, { app.inlayHintsParameterNames = it })
                }
            }

            group(SlangBundle.message("settings.fileTypes.group")) {
                row {
                    checkBox(SlangBundle.message("settings.fileTypes.glsl"))
                        .bindSelected({ app.glslSupport }, { app.glslSupport = it })
                        .comment(SlangBundle.message("settings.fileTypes.glsl.comment"))
                }
                row {
                    checkBox(SlangBundle.message("settings.fileTypes.hlsl"))
                        .bindSelected({ app.hlslSupport }, { app.hlslSupport = it })
                        .comment(SlangBundle.message("settings.fileTypes.hlsl.comment"))
                }
            }

            // onApply/onReset run after the field bindings have written their backing state, so
            // lspRestartKey() here reflects the just-applied settings.
            var shaderTypesWere = app.glslSupport to app.hlslSupport
            onReset {
                lspKey = lspRestartKey()
                shaderTypesWere = app.glslSupport to app.hlslSupport
            }
            onApply {
                val current = lspRestartKey()
                if (current != lspKey) {
                    lspKey = current
                    // Restart slangd so the new binary / initializationOptions take effect now,
                    // rather than only after an IDE restart.
                    SlangLspRestart.restart(project)
                }
                val shaderTypesNow = app.glslSupport to app.hlslSupport
                if (shaderTypesNow != shaderTypesWere) {
                    shaderTypesWere = shaderTypesNow
                    ShaderFileAssociations.sync()
                }
                refresh()
            }
        }
        refresh()
        return dialog
    }

    /** Re-detect the system and plugin slangd, updating the info labels off the EDT. */
    @RequiresEdt
    private fun refresh() {
        systemInfo.text = SlangBundle.message("settings.slangd.checking")
        pluginInfo.text = SlangBundle.message("settings.slangd.checking")
        if (::downloadButton.isInitialized) downloadButton.isEnabled = false

        ApplicationManager.getApplication().executeOnPooledThread {
            val system = SlangdBinary.systemBinary()
            val systemText = if (system == null) {
                SlangBundle.message("settings.slangd.notFound")
            } else {
                describe(system.toString(), SlangdVersion.of(system))
            }

            val plugin = SlangdDownload.installedBinary()
            val stale = SlangdDownload.staleVersions()
            val pluginText = when {
                plugin != null -> SlangBundle.message("settings.slangd.versionOnly", SlangdDownload.VERSION)
                stale.isNotEmpty() -> SlangBundle.message("settings.slangd.pluginStale", stale.joinToString(", "))
                else -> SlangBundle.message("settings.slangd.pluginNone")
            }
            val needsDownload = plugin == null

            ApplicationManager.getApplication().invokeLater {
                systemInfo.text = systemText
                pluginInfo.text = pluginText
                if (::downloadButton.isInitialized) downloadButton.isEnabled = needsDownload && !SlangdDownload.isDownloading
            }
        }
    }

    private fun describe(path: String, version: String?): String =
        SlangBundle.message(
            "settings.slangd.foundAt",
            path,
            version ?: SlangBundle.message("settings.slangd.versionUnknown"),
        )

    private fun MutableList<String>.toLines() = joinToString("\n")

    private fun MutableList<String>.setLines(text: String) {
        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines == this.toList()) return
        clear()
        addAll(lines)
    }
}
