package slang.plugin.lsp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SlangLspConfigTest {

    private fun inputs(
        searchPaths: List<String> = emptyList(),
        macros: List<String> = emptyList(),
        searchInWorkspace: Boolean = true,
        deducedTypes: Boolean = true,
        parameterNames: Boolean = true,
    ) = SlangLspConfig.Inputs(searchPaths, macros, searchInWorkspace, deducedTypes, parameterNames)

    @Test
    fun `all the managed slang keys are present`() {
        assertTrue(
            SlangLspConfig.all(inputs()).keys.containsAll(
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
    fun `the config blob reflects the inputs`() {
        val map = SlangLspConfig.all(
            inputs(searchPaths = listOf("../LibA"), macros = listOf("SHADOWS=1"), searchInWorkspace = false),
        )
        assertEquals(listOf("../LibA"), map["slang.additionalSearchPaths"])
        assertEquals(listOf("SHADOWS=1"), map["slang.predefinedMacros"])
        assertEquals(false, map["slang.searchInAllWorkspaceDirectories"])
    }

    @Test
    fun `configurationFor resolves a known section`() {
        val map = SlangLspConfig.all(inputs(macros = listOf("A")))
        assertEquals(listOf("A"), SlangLspConfig.configurationFor("slang.predefinedMacros", map))
    }

    @Test
    fun `configurationFor accepts the bare key without the slang prefix`() {
        val map = SlangLspConfig.all(inputs(searchInWorkspace = true))
        assertEquals(true, SlangLspConfig.configurationFor("searchInAllWorkspaceDirectories", map))
    }

    @Test
    fun `configurationFor returns null for keys we do not manage`() {
        val map = SlangLspConfig.all(inputs())
        assertNull(SlangLspConfig.configurationFor("editor.fontSize", map))
        assertNull(SlangLspConfig.configurationFor(null, map))
    }
}
