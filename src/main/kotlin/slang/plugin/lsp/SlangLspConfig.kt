package slang.plugin.lsp

import com.intellij.openapi.project.Project
import slang.plugin.settings.SlangProjectSettings
import slang.plugin.settings.SlangSettings

/**
 * Builds the `slang.*` configuration that slangd reads. slangd asks for it two ways:
 *  - once at start-up via LSP `initializationOptions` ([initializationOptions]),
 *  - per key via `workspace/configuration` requests ([configurationFor]).
 *
 * The values come from [SlangSettings] (machine-wide) and [SlangProjectSettings] (project-shared).
 *
 * Recognised keys (from the slangd source): `slang.predefinedMacros`, `slang.additionalSearchPaths`,
 * `slang.searchInAllWorkspaceDirectories`, `slang.inlayHints.deducedTypes`,
 * `slang.inlayHints.parameterNames`, `slang.enableCommitCharactersInAutoCompletion`,
 * `slang.format.*`.
 */
object SlangLspConfig {

    /** The inputs [all] needs, resolved from services (overridable in tests). */
    data class Inputs(
        val searchPaths: List<String>,
        val predefinedMacros: List<String>,
        val searchInAllWorkspaceDirectories: Boolean,
        val inlayHintsDeducedTypes: Boolean,
        val inlayHintsParameterNames: Boolean,
    )

    private fun inputsFor(project: Project): Inputs {
        val app = SlangSettings.getInstance().state
        val proj = SlangProjectSettings.getInstance(project).state
        return Inputs(
            searchPaths = proj.additionalSearchPaths.toList(),
            predefinedMacros = proj.predefinedMacros.toList(),
            searchInAllWorkspaceDirectories = proj.searchInAllWorkspaceDirectories,
            inlayHintsDeducedTypes = app.inlayHintsDeducedTypes,
            inlayHintsParameterNames = app.inlayHintsParameterNames,
        )
    }

    internal fun all(inputs: Inputs): Map<String, Any> = mapOf(
        "slang.predefinedMacros" to inputs.predefinedMacros,
        "slang.additionalSearchPaths" to inputs.searchPaths,
        "slang.searchInAllWorkspaceDirectories" to inputs.searchInAllWorkspaceDirectories,
        "slang.inlayHints.deducedTypes" to inputs.inlayHintsDeducedTypes,
        "slang.inlayHints.parameterNames" to inputs.inlayHintsParameterNames,
    )

    fun initializationOptions(project: Project): Any = all(inputsFor(project))

    /**
     * Everything that slangd only reads at start-up (via [initializationOptions] /
     * `workspace/configuration`). The settings UI compares this before and after an Apply to decide
     * whether the language server needs restarting.
     */
    fun restartRelevantConfig(project: Project): Map<String, Any> = all(inputsFor(project))

    /** Value for a single `workspace/configuration` section, or null if we don't manage that key. */
    fun configurationFor(section: String?, project: Project): Any? =
        configurationFor(section, all(inputsFor(project)))

    internal fun configurationFor(section: String?, entries: Map<String, Any>): Any? {
        if (section == null) return null
        entries[section]?.let { return it }
        // slangd also asks for nested keys with the leading "slang." stripped in some versions.
        return entries["slang.$section"]
    }
}
