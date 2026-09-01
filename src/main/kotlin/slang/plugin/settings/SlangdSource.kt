package slang.plugin.settings

/** Which `slangd` the plugin should launch. */
enum class SlangdSource {
    /** The `slangd` from the configured path, or failing that from `PATH`. */
    SYSTEM,

    /** The `slangd` the plugin downloads and manages itself. */
    PLUGIN,
}
