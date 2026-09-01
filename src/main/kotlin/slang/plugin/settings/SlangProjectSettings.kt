package slang.plugin.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * Project-specific Slang settings that are forwarded to slangd. Stored in the project's shared
 * `.idea/slang.xml`, so a team can commit include paths / macros with the project.
 */
@State(name = "SlangProjectSettings", storages = [Storage("slang.xml")])
class SlangProjectSettings :
    SimplePersistentStateComponent<SlangProjectSettings.State>(State()) {

    class State : BaseState() {
        /** Add every workspace directory that contains a `.slang`/`.hlsl` file to the import path. */
        var searchInAllWorkspaceDirectories by property(true)

        /** Extra `-I` directories, in addition to the ones slangd discovers. */
        var additionalSearchPaths by list<String>()

        /** Predefined preprocessor macros, each `NAME` or `NAME=VALUE`. */
        var predefinedMacros by list<String>()
    }

    companion object {
        fun getInstance(project: Project): SlangProjectSettings =
            project.getService(SlangProjectSettings::class.java)
    }
}
