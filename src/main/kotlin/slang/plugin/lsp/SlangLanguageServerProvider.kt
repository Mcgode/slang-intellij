package slang.plugin.lsp

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerManager
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path

@Service(Service.Level.PROJECT)
class SlangLanguageServerProvider {

    companion object {

        fun getInstance(project: Project): SlangLanguageServerProvider {
            return project.getService(SlangLanguageServerProvider::class.java)
        }

    }

    private val slangdVersion = "2024.17"
    private val downloader = SlangLanguageServerDownloader();

    fun getLanguageServerDirectoryPath(): Path {
        val path = Path.of(PathManager.getPluginsPath(), "slang-intellij", slangdVersion)
        if (!Files.exists(path)) {
            path.toFile().mkdirs()
        }
        return path
    }

    fun getSlangDExecutablePath(): Path {
        return getLanguageServerDirectoryPath().toFile().listFiles()?.find {
            isSlangDExecutableFileName(it.toPath().fileName.toString())
        }!!.toPath()
    }

    fun isSlangDExecutableFileName(filename: String): Boolean {
        return filename.contains(Regex("^slangd(.exe)?$"))
    }

    fun isSlangDynamicLibraryFileName(filename: String): Boolean {
        return filename.contains(Regex("^libslang\\.[a-zA-Z]+$")) || filename.contains(Regex("^slang\\.dll$"))
    }

    fun checkValidLanguageServerFiles(): Boolean {
        val dir = getLanguageServerDirectoryPath()

        val files = dir.toFile().listFiles() ?: return false
        return files.any { isSlangDExecutableFileName(it.toPath().fileName.toString()) }
                && files.any { isSlangDynamicLibraryFileName(it.toPath().fileName.toString()) }
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

    fun uri(): URI {
        return URI("https://github.com/shader-slang/slang/releases/download/v%s/slang-%s-%s-%s.zip".format(
            slangdVersion,
            slangdVersion,
            getOsCode(),
            getArchCode()
        ))
    }

    fun tryAutoDownload(project: Project) {
        if (downloader.status == SlangLanguageServerDownloader.Status.Idle) {
            downloader.launchDownload(project, this)
            downloader.addStatusChangeListener {
                if (it == SlangLanguageServerDownloader.Status.Downloaded) {
                    LanguageServerManager.getInstance(project).start("slangLanguageServer")
                }
            }
        }
    }
}