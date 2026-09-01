package slang.plugin.lsp

import slang.plugin.settings.SlangSettings

/**
 * Builds the `slang.*` configuration that slangd reads. slangd asks for it two ways:
 *  - once at start-up via LSP `initializationOptions` ([initializationOptions]),
 *  - per key via `workspace/configuration` requests ([configurationFor]).
 *
 * Recognised keys (from the slangd source): `slang.predefinedMacros`, `slang.additionalSearchPaths`,
 * `slang.searchInAllWorkspaceDirectories`, `slang.inlayHints.deducedTypes`,
 * `slang.inlayHints.parameterNames`, `slang.enableCommitCharactersInAutoCompletion`,
 * `slang.format.*`.
 */
object SlangLspConfig {

    private fun currentState() = SlangSettings.getInstance().state

    internal fun all(state: SlangSettings.SlangState = currentState()): Map<String, Any> = mapOf(
        "slang.predefinedMacros" to state.predefinedMacros.toList(),
        "slang.additionalSearchPaths" to state.additionalSearchPaths.toList(),
        "slang.searchInAllWorkspaceDirectories" to state.searchInAllWorkspaceDirectories,
        "slang.inlayHints.deducedTypes" to state.inlayHintsDeducedTypes,
        "slang.inlayHints.parameterNames" to state.inlayHintsParameterNames,
    )

    fun initializationOptions(): Any = all()

    /** Value for a single `workspace/configuration` section, or null if we don't manage that key. */
    fun configurationFor(section: String?, state: SlangSettings.SlangState = currentState()): Any? {
        if (section == null) return null
        val entries = all(state)
        entries[section]?.let { return it }
        // slangd also asks for nested keys with the leading "slang." stripped in some versions.
        return entries["slang.$section"]
    }
}
