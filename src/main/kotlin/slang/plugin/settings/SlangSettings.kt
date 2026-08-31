package slang.plugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "SlangSettings", storages = [Storage("slang.xml")])
class SlangSettings : SimplePersistentStateComponent<SlangSettings.SlangState>(SlangState()) {

    class SlangState : BaseState() {
        var slangdPath by string("")
        var autoDownload by property(true)
        var searchInAllWorkspaceDirectories by property(true)
        var inlayHintsDeducedTypes by property(true)
        var inlayHintsParameterNames by property(true)
        val additionalSearchPaths by list<String>()
        val predefinedMacros by list<String>()
    }

    companion object {
        fun getInstance(): SlangSettings = ApplicationManager.getApplication().getService(SlangSettings::class.java)
    }
}
