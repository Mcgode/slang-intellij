package slang.plugin.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class SlangLanguageServer(project: Project): OSProcessStreamConnectionProvider() {

    private val SLANGD_VERSION = "2024.17"
    private val SLANGD_EXE_NAME: String = let {
        if (System.getProperty("os.name").lowercase().contains("win"))
            "slangd.exe"
        else
            "slangd"
    }

    private var exeDownloaded = false
    private var startOnDownloaded = false

    companion object {
        private var downloader: SlangLanguageServerDownloader? = null
    }

    private fun getSlangdPath(): Path {
        val path = Path.of(PathManager.getPluginsPath(), "slang-intellij", SLANGD_VERSION, SLANGD_EXE_NAME)
        if (!Files.exists(path.parent)) {
            path.parent.toFile().mkdirs()
        }
        return path
    }

    private fun getOsCode() = System.getProperty("os.name").lowercase().run {
        when {
            "win" in this -> "windows"
            "mac" in this -> "macos"
            else -> "linux"
        }
    }

    private fun getArchCode(): String = System.getProperty("os.arch").lowercase().run {
        when {
            "arch64" in this -> "aarch64"
            else -> "x86_64"
        }
    }

    private fun onDownloadStatusChanged(status: SlangLanguageServerDownloader.Status) {
        if (status == SlangLanguageServerDownloader.Status.Downloading || status == SlangLanguageServerDownloader.Status.Scheduled)
            return

        if (startOnDownloaded) {
            super.start()
            startOnDownloaded = false
            downloader = null
        }
        exeDownloaded = true
    }

    init {
        val exePath = getSlangdPath()

        exeDownloaded = exePath.toFile().exists()

        // Check if dynamic slang lib is there as well
        if (exeDownloaded) {
            exeDownloaded = exePath.parent.toFile().listFiles()?.any {
                it.path.contains("libslang.") || it.path.contains("slang.dll")
            } == true
        }

        commandLine = GeneralCommandLine(exePath.absolutePathString(), "--stdio")

        if (!exeDownloaded) {
            if (downloader == null) {
                val url = "https://github.com/shader-slang/slang/releases/download/v%s/slang-%s-%s-%s.zip".format(
                    SLANGD_VERSION,
                    SLANGD_VERSION,
                    getOsCode(),
                    getArchCode()
                )
                downloader = SlangLanguageServerDownloader(project, exePath, URI(url))
            }

            if (!downloader!!.isDone())
                downloader!!.addListener(this::onDownloadStatusChanged)
        }
    }



    override fun start() {
        if (exeDownloaded)
            super.start()
        else
            startOnDownloaded = true
    }

    override fun isAlive(): Boolean {
        return (downloader != null && startOnDownloaded) || super.isAlive()
    }

    override fun stop() {
        if (downloader != null) {
            assert(startOnDownloaded)
            startOnDownloaded = false
        }
        else
            super.stop()
    }
}