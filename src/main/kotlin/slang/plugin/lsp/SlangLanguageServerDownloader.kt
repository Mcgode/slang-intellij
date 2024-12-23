package slang.plugin.lsp

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.URI
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SlangLanguageServerDownloader(
    private val project: Project,
    private val outputPath: Path,
    private val uri: URI) {

    enum class Status {
        Scheduled,
        Downloading,
        Downloaded,
        Cancelled
    }

    private var status: Status = Status.Scheduled
    private val LOG = logger<SlangLanguageServerDownloader>()

    fun isDone(): Boolean {
        return status == Status.Downloaded || status == Status.Cancelled
    }

    private var listeners = HashSet<(Status) -> Unit>()

    fun addListener(listener: (Status) -> Unit) = listeners.add(listener)
    fun removeListener(listener: (Status) -> Unit) = listeners.remove(listener)

    private suspend fun runCancellableBackgroundTaskSuspending(
        title: String,
        project: Project,
        action: (ProgressIndicator) -> Unit)
    {
        suspendCancellableCoroutine<Unit> { continuation ->
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, title, true) {
                override fun run(indicator: ProgressIndicator) {
                    try {
                        action(indicator)
                        continuation.resume(Unit)
                    }
                    catch (e: Throwable) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }

    private suspend fun download() =
        runCancellableBackgroundTaskSuspending("Downloading slangd", project) { indicator ->

            LOG.info("Downloading SlangD from $uri")

            indicator.text = "Download Slang language server"
            val zipInputStream = ZipInputStream(uri.toURL().openStream())

            var entry: ZipEntry?

            val entryCount = 2.0
            val fractionSizePerEntry = 1.0 / entryCount
            var fractionOffset = 0.0

            while (zipInputStream.nextEntry.also { entry = it } != null) {

                val path = if (entry!!.name.contains("slangd"))
                    outputPath
                else if (entry!!.name.contains("slang.dll") || entry!!.name.contains("libslang.")) {
                    val fileName = Path.of(entry!!.name).fileName.toString()
                    outputPath.parent.resolve(fileName)
                } else
                    null

                path?.let {
                    indicator.text2 = path.fileName.toString()
                    indicator.fraction = 0.0

                    path.toFile().outputStream().use { outputStream ->
                        val readBuffer = ByteArray(4096)
                        var readLength: Int
                        var totalRead: Long = 0
                        while (zipInputStream.read(readBuffer).also { readLength = it } != -1) {
                            indicator.checkCanceled()
                            totalRead += readLength
                            outputStream.write(readBuffer, 0, readLength)
                            indicator.fraction =
                                fractionOffset + (totalRead.toDouble() / entry!!.size.toDouble()) * fractionSizePerEntry
                        }
                        outputStream.flush()
                        fractionOffset += fractionSizePerEntry
                        path.toFile().setExecutable(entry!!.name.contains("slangd"))
                    }
                }
            }
        }

    init {
        ApplicationManager.getApplication().executeOnPooledThread {
            runBlocking {
                status = Status.Downloading
                try {
                    download()
                    status = Status.Downloaded
                } catch (_: CancellationException) {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Slang")
                        .createNotification("Download has been cancelled", NotificationType.WARNING)
                    status = Status.Cancelled
                }
            }
        }
    }

}