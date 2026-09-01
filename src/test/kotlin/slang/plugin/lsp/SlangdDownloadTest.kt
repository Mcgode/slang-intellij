package slang.plugin.lsp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SlangdDownloadTest {

    @Test
    fun `pinned version looks like a slang release tag`() {
        assertTrue(
            "unexpected version '${SlangdDownload.VERSION}'",
            SlangdDownload.VERSION.matches(Regex("""\d{4}\.\d+(\.\d+)?""")),
        )
    }

    @Test
    fun `release url matches the github asset layout`() {
        val v = SlangdDownload.VERSION
        assertEquals(
            "https://github.com/shader-slang/slang/releases/download/v$v/slang-$v-macos-aarch64.zip",
            SlangdDownload.releaseUrl("macos-aarch64"),
        )
        assertEquals(
            "https://github.com/shader-slang/slang/releases/download/v$v/slang-$v-windows-x86_64.zip",
            SlangdDownload.releaseUrl("windows-x86_64"),
        )
    }
}
