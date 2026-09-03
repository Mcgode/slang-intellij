package slang.plugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.annotations.Transient

/**
 * Machine-wide Slang settings (the same regardless of which project is open).
 * Project-specific settings — include search paths, predefined macros — live in
 * [SlangProjectSettings].
 */
@State(name = "SlangSettings", storages = [Storage("slang.xml")])
class SlangSettings : SimplePersistentStateComponent<SlangSettings.SlangState>(SlangState()) {

    class SlangState : BaseState() {
        /** Persisted form of [slangdSource]. */
        var slangdSourceName by string(SlangdSource.PLUGIN.name)

        /** Path to a system `slangd` (a file or its `bin` directory). Used when [slangdSource] is SYSTEM. */
        var slangdPath by string("")

        /** Download the plugin-managed slangd automatically instead of only offering a button. */
        var autoDownload by property(true)

        var inlayHintsDeducedTypes by property(true)
        var inlayHintsParameterNames by property(true)

        /**
         * Associate GLSL file extensions (.glsl, .vert, .frag, …) with the GLSL file type and let
         * slangd analyze them. Off by default — it overlaps with the standalone GLSL plugin.
         */
        var glslSupport by property(false)

        /**
         * Associate HLSL and HLSL-adjacent extensions (.hlsl, .hlsli, .fx, Unreal's .usf / .ush, …)
         * with the HLSL file type. On by default: Slang parses HLSL as a superset, and the
         * association only takes an extension no other plugin (e.g. Rider's built-in support) owns.
         */
        var hlslSupport by property(true)

        @get:Transient
        var slangdSource: SlangdSource
            get() = runCatching { SlangdSource.valueOf(slangdSourceName ?: "") }.getOrDefault(SlangdSource.PLUGIN)
            set(value) {
                slangdSourceName = value.name
            }
    }

    companion object {
        fun getInstance(): SlangSettings = ApplicationManager.getApplication().getService(SlangSettings::class.java)
    }
}
