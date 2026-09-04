package slang.plugin.language

import com.intellij.openapi.vfs.VirtualFile

/** The file types this plugin drives `slangd` for: Slang itself and, when enabled, GLSL and HLSL. */
object SlangFileTypes {

    fun isHandled(file: VirtualFile): Boolean = when (file.fileType) {
        SlangFileType.INSTANCE, GlslFileType.INSTANCE, HlslFileType.INSTANCE -> true
        else -> false
    }
}
