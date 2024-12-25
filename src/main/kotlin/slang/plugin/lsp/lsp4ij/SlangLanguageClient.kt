package slang.plugin.lsp.lsp4ij

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import slang.plugin.lsp.SlangJsonConfigService
import java.nio.file.Path

class SlangLanguageClient(project: Project): LanguageClientImpl(project) {

    override fun createSettings(): Any? {
        val jsonConfig = SlangJsonConfigService.getInstance(project)
        jsonConfig.prepareJsonConfig(project)
        jsonConfig.setUpdateListener {
            triggerChangeConfiguration()
        }
        return jsonConfig.json
    }

}