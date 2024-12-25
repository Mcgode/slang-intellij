package slang.plugin.lsp

import com.google.gson.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import java.nio.file.Path

@Service(Service.Level.PROJECT)
class SlangJsonConfigService: BulkFileListener {

    private var configPath: Path? = null
    var json: JsonElement? = null
        private set
    private var configUpdateListener: (() -> Unit)? = null

    fun setUpdateListener(updateListener: () -> Unit) {
        configUpdateListener = updateListener
    }

    companion object {
        fun getInstance(project: Project): SlangJsonConfigService = project.getService(SlangJsonConfigService::class.java)
    }

    override fun after(events: MutableList<out VFileEvent>) {
        if (configPath == null)
            return

        for (event in events) {
            if (event.path == configPath.toString()) {
                fetchAndApplyChanges()
                configUpdateListener?.invoke()
            }
        }
    }

    private fun fetchAndApplyChanges() {
        if (configPath == null)
            return

        if (!configPath!!.toFile().exists())
            return
        json = JsonParser.parseString(configPath!!.toFile().readText(Charsets.UTF_8))

        (json as? JsonObject)?.let { rootObject ->
            (rootObject.get("slang") as? JsonObject)?.let { slangObject ->

                // Convert relative paths in 'additionalSearchPaths' to absolute paths
                (slangObject.get("additionalSearchPaths") as? JsonArray)?.let { array ->
                    for (i in 0 until array.size()) {
                        val entry = array[i]!!
                        if (!entry.isJsonPrimitive || !entry.asJsonPrimitive.isString)
                            continue

                        val entryPath = Path.of(entry.asJsonPrimitive.asString)

                        if (!entryPath.isAbsolute) {
                            array.set(i, JsonPrimitive(configPath!!.parent.resolve(entryPath.toString()).toString()))
                        }
                    }
                }
            }
        }
    }

    fun prepareJsonConfig(project: Project) {
        configPath = Path.of(project.basePath!!, "slangdconfig.json")

        if (json == null)
            fetchAndApplyChanges()
    }

    inline fun <reified T> getEntry(currentJson: JsonElement, names: List<String>): T? {
        var current: JsonElement? = currentJson
        for (name in names) {
            current = (current as? JsonObject)?.get(name)
            if (current == null)
                break
        }

        return if (current == null)
            null
        else
            Gson().fromJson(current, T::class.java)
    }

    inline fun <reified T> getEntry(name: String): T? {
        return getEntry(json ?: return null, name.split("."))
    }
}