package slang.plugin.language

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/** Applies the GLSL / HLSL extension associations once the IDE is up (idempotent across projects). */
class ShaderFileTypeStartup : ProjectActivity {
    override suspend fun execute(project: Project) {
        ShaderFileAssociations.sync()
    }
}
