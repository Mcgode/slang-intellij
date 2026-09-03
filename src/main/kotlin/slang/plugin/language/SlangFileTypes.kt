package slang.plugin.language

import com.intellij.openapi.vfs.VirtualFile

/** The file types this plugin drives `slangd` for: Slang itself and, when enabled, GLSL. */
object SlangFileTypes {

    fun isHandled(file: VirtualFile): Boolean = when (file.fileType) {
        SlangFileType.INSTANCE, GlslFileType.INSTANCE -> true
        else -> false
    }
}
