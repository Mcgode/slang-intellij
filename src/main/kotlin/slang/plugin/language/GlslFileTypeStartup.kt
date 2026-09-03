package slang.plugin.language

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/** Applies the GLSL extension associations once the IDE is up (idempotent across projects). */
class GlslFileTypeStartup : ProjectActivity {
    override suspend fun execute(project: Project) {
        GlslFileAssociations.sync()
    }
}
