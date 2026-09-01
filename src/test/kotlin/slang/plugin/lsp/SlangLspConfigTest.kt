package slang.plugin.lsp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import slang.plugin.settings.SlangSettings

class SlangLspConfigTest {

    private fun state() = SlangSettings.SlangState()

    @Test
    fun `the managed slang keys are all present`() {
        assertTrue(
            SlangLspConfig.all(state()).keys.containsAll(
                listOf(
                    "slang.predefinedMacros",
                    "slang.additionalSearchPaths",
                    "slang.searchInAllWorkspaceDirectories",
                    "slang.inlayHints.deducedTypes",
                    "slang.inlayHints.parameterNames",
                ),
            ),
        )
    }

    @Test
    fun `configurationFor reflects the settings state`() {
        val s = state()
        s.predefinedMacros.add("SHADOWS=1")
        assertEquals(listOf("SHADOWS=1"), SlangLspConfig.configurationFor("slang.predefinedMacros", s))

        s.inlayHintsParameterNames = false
        assertEquals(false, SlangLspConfig.configurationFor("slang.inlayHints.parameterNames", s))
    }

    @Test
    fun `configurationFor accepts the bare key without the slang prefix`() {
        assertEquals(true, SlangLspConfig.configurationFor("searchInAllWorkspaceDirectories", state()))
    }

    @Test
    fun `configurationFor returns null for keys we do not manage`() {
        assertNull(SlangLspConfig.configurationFor("editor.fontSize", state()))
        assertNull(SlangLspConfig.configurationFor(null, state()))
    }
}
