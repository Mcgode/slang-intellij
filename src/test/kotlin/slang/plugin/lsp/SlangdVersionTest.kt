package slang.plugin.lsp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SlangdVersionTest {

    @Test
    fun `parses the version out of slangc -v output`() {
        assertEquals("2026.16.1", SlangdVersion.parse("2026.16.1\n"))
        assertEquals("2026.16.1", SlangdVersion.parse("Slang version 2026.16.1 (build ...)"))
        assertEquals("2026.13", SlangdVersion.parse("2026.13"))
        assertEquals("2026.14.0-rc.2", SlangdVersion.parse("2026.14.0-rc.2"))
        assertNull(SlangdVersion.parse("no version here"))
    }

    @Test
    fun `compares numeric-dotted versions`() {
        assertTrue(SlangdVersion.compare("2026.10.0", "2026.16.1") < 0)
        assertTrue(SlangdVersion.compare("2026.16.1", "2026.16.1") == 0)
        assertTrue(SlangdVersion.compare("2026.16.2", "2026.16.1") > 0)
        assertTrue(SlangdVersion.compare("2026.9", "2026.16.1") < 0)   // 9 < 16, not string order
        assertTrue(SlangdVersion.compare("2027.1", "2026.16.1") > 0)
    }

    @Test
    fun `isOlderThan does not nag on unknown or newer`() {
        assertFalse(SlangdVersion.isOlderThan(null, "2026.16.1"))
        assertFalse(SlangdVersion.isOlderThan("2026.16.1", "2026.16.1"))
        assertFalse(SlangdVersion.isOlderThan("2027.0.0", "2026.16.1"))
        assertTrue(SlangdVersion.isOlderThan("2026.10.0", "2026.16.1"))
    }
}
