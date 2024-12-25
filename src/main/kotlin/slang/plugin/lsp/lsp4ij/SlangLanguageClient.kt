package slang.plugin.lsp.lsp4ij

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import java.nio.file.Path

class SlangLanguageClient(project: Project): LanguageClientImpl(project) {

    override fun createSettings(): Any? {
        val path = Path.of(project.basePath ?: return null, "slang-config.json")
        if (!path.toFile().exists())
            return null
        val json = JsonParser.parseString(path.toFile().readText(Charsets.UTF_8))

        (((json as? JsonObject)?.get("slang") as? JsonObject)?.get("additionalSearchPaths") as? JsonArray)?.let { array ->
            for (i in 0 until array.size()) {
                val entry = array[i]!!
                if (!entry.isJsonPrimitive || !entry.asJsonPrimitive.isString)
                    continue

                val entryPath = Path.of(entry.asJsonPrimitive.asString)

                if (!entryPath.isAbsolute) {
                    array.set(i, JsonPrimitive(Path.of(project.basePath!!, entryPath.toString()).toString()))
                }
            }
        }
        return json
    }

}