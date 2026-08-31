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

    private fun all(): Map<String, Any> {
        val s = SlangSettings.getInstance().state
        return mapOf(
            "slang.predefinedMacros" to s.predefinedMacros.toList(),
            "slang.additionalSearchPaths" to s.additionalSearchPaths.toList(),
            "slang.searchInAllWorkspaceDirectories" to s.searchInAllWorkspaceDirectories,
            "slang.inlayHints.deducedTypes" to s.inlayHintsDeducedTypes,
            "slang.inlayHints.parameterNames" to s.inlayHintsParameterNames,
        )
    }

    fun initializationOptions(): Any = all()

    /** Value for a single `workspace/configuration` section, or null if we don't manage that key. */
    fun configurationFor(section: String?): Any? {
        if (section == null) return null
        all()[section]?.let { return it }
        // slangd also asks for nested keys with the leading "slang." stripped in some versions.
        return all()["slang.$section"]
    }
}
