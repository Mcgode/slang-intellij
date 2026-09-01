package slang.plugin.settings

import com.intellij.configurationStore.deserializeInto
import com.intellij.configurationStore.serialize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the actual persistence path: [SlangProjectSettings] is a [com.intellij.openapi.components.SimplePersistentStateComponent],
 * so the platform saves it by serialising [SlangProjectSettings.State] to `.idea/slang.xml`. The list
 * properties only make it into that XML if they are declared `var` (a getter-only `by list()` property
 * is silently dropped by the bean serialiser), so restarting the IDE would lose configured search paths.
 */
class SlangProjectSettingsPersistenceTest {

    @Test
    fun `search paths and macros survive a serialize round-trip`() {
        val state = SlangProjectSettings.State()
        state.additionalSearchPaths.add("libs/common")
        state.additionalSearchPaths.add("libs/pbr")
        state.predefinedMacros.add("SLANG_DEBUG=1")
        state.searchInAllWorkspaceDirectories = false

        assertTrue("mutations must bump the modification count so the platform saves", state.modificationCount > 0)

        val element = serialize(state)
        assertNotNull("state with non-default values must serialize to a non-empty element", element)

        val restored = SlangProjectSettings.State()
        element!!.deserializeInto(restored)

        assertEquals(listOf("libs/common", "libs/pbr"), restored.additionalSearchPaths.toList())
        assertEquals(listOf("SLANG_DEBUG=1"), restored.predefinedMacros.toList())
        assertEquals(false, restored.searchInAllWorkspaceDirectories)
    }

    @Test
    fun `an all-defaults state serializes to nothing`() {
        assertEquals(null, serialize(SlangProjectSettings.State()))
    }
}
