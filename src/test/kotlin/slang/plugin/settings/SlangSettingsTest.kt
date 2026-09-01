package slang.plugin.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class SlangSettingsTest {

    @Test
    fun `slangdSource round-trips through its persisted string`() {
        val state = SlangSettings.SlangState()
        assertEquals(SlangdSource.PLUGIN, state.slangdSource)

        state.slangdSource = SlangdSource.SYSTEM
        assertEquals("SYSTEM", state.slangdSourceName)
        assertEquals(SlangdSource.SYSTEM, state.slangdSource)
    }

    @Test
    fun `an unrecognised persisted value falls back to PLUGIN`() {
        val state = SlangSettings.SlangState()

        state.slangdSourceName = "not-a-source"
        assertEquals(SlangdSource.PLUGIN, state.slangdSource)

        state.slangdSourceName = null
        assertEquals(SlangdSource.PLUGIN, state.slangdSource)
    }
}
