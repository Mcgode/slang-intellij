package slang.plugin.lsp

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.util.io.Decompressor
import com.intellij.util.io.HttpRequests
import com.intellij.util.system.CpuArch
import slang.plugin.SlangBundle
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Downloads a matching `slangd` from the Slang GitHub releases into the IDE system directory and
 * (re)starts the language client once it is in place. Used only when no `slangd` is found from
 * settings or `PATH`.
 */
object SlangdDownload {

    /** Slang release used for the plugin-managed download. Bump alongside plugin releases. */
    const val VERSION: String = "2026.16.1"

    private val LOG = logger<SlangdDownload>()
    private val inProgress = AtomicBoolean(false)

    val isDownloading: Boolean get() = inProgress.get()

    private val exeName: String get() = if (SystemInfo.isWindows) "slangd.exe" else "slangd"

    private fun installRoot(): Path = PathManager.getSystemDir().resolve("slang-lsp")
    private fun versionDir(): Path = installRoot().resolve(VERSION)

    /** The plugin-managed slangd's version, or null if it is not installed. Always [VERSION] when present. */
    fun installedVersion(): String? = if (installedBinary() != null) VERSION else null

    /** Downloaded version directories other than the current [VERSION] (from an earlier plugin release). */
    fun staleVersions(): List<String> {
        val root = installRoot()
        if (!Files.isDirectory(root)) return emptyList()
        Files.list(root).use { s ->
            return s.filter { Files.isDirectory(it) && it.fileName.toString() != VERSION }
                .map { it.fileName.toString() }
                .sorted()
                .toList()
        }
    }

    private fun cleanupStaleVersions() {
        val root = installRoot()
        if (!Files.isDirectory(root)) return
        Files.list(root).use { s ->
            s.filter { Files.isDirectory(it) && it.fileName.toString() != VERSION }.forEach { dir ->
                runCatching { dir.toFile().deleteRecursively() }
            }
        }
    }

    /** Release assets are named `slang-<version>-<os>-<arch>.zip`. */
    private fun platformSlug(): String? {
        val os = when {
            SystemInfo.isWindows -> "windows"
            SystemInfo.isMac -> "macos"
            SystemInfo.isLinux -> "linux"
            else -> return null
        }
        val arch = when {
            CpuArch.isArm64() -> "aarch64"
            CpuArch.isIntel64() -> "x86_64"
            else -> return null
        }
        return "$os-$arch"
    }

    fun releaseUrl(slug: String): String =
        "https://github.com/shader-slang/slang/releases/download/v$VERSION/slang-$VERSION-$slug.zip"

    /** The downloaded `slangd`, or null if it has not been installed (or the install is incomplete). */
    fun installedBinary(): Path? {
        val dir = versionDir()
        if (!Files.isDirectory(dir)) return null
        Files.walk(dir).use { stream ->
            return stream
                .filter { it.fileName?.toString() == exeName && Files.isRegularFile(it) }
                .findFirst()
                .orElse(null)
        }
    }

    fun startDownload(project: Project, onFinished: () -> Unit = {}) {
        if (installedBinary() != null) {
            SlangLspRestart.restart(project)
            onFinished()
            return
        }
        if (!inProgress.compareAndSet(false, true)) {
            onFinished()
            return
        }

        val slug = platformSlug()
        if (slug == null) {
            inProgress.set(false)
            notify(project, SlangBundle.message("notification.slangd.noPrebuilt"), NotificationType.WARNING)
            onFinished()
            return
        }

        object : Task.Backgroundable(project, SlangBundle.message("notification.slangd.downloading"), true) {
            override fun run(indicator: ProgressIndicator) {
                val url = releaseUrl(slug)
                val zip = Files.createTempFile("slangd-$VERSION-", ".zip")
                try {
                    indicator.text = url
                    HttpRequests.request(url).saveToFile(zip.toFile(), indicator)

                    indicator.text = SlangBundle.message("notification.slangd.extracting")
                    indicator.isIndeterminate = true
                    val dest = versionDir()
                    Files.createDirectories(dest)
                    Decompressor.Zip(zip)
                        // libslang-llvm (~100 MB) is only needed for host codegen, not the language server.
                        .filter { entry -> !entry.contains("libslang-llvm") }
                        .extract(dest)

                    if (!SystemInfo.isWindows) {
                        installedBinary()?.toFile()?.setExecutable(true, false)
                    }
                } finally {
                    runCatching { Files.deleteIfExists(zip) }
                }
            }

            override fun onSuccess() {
                inProgress.set(false)
                if (installedBinary() != null) {
                    cleanupStaleVersions()
                    notify(
                        project,
                        SlangBundle.message("notification.slangd.installed", VERSION),
                        NotificationType.INFORMATION,
                    )
                    SlangLspRestart.restart(project)
                } else {
                    notify(project, SlangBundle.message("notification.slangd.missingInArchive"), NotificationType.ERROR)
                }
                onFinished()
            }

            override fun onThrowable(error: Throwable) {
                inProgress.set(false)
                LOG.warn("slangd download failed", error)
                notify(
                    project,
                    SlangBundle.message("notification.slangd.downloadFailed", error.message ?: error.javaClass.simpleName),
                    NotificationType.ERROR,
                )
                onFinished()
            }
        }.queue()
    }

    private fun notify(project: Project, content: String, type: NotificationType) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Slang")
            .createNotification(SlangBundle.message("notification.slangd.notFound.title"), content, type)
            .notify(project)
    }
}
