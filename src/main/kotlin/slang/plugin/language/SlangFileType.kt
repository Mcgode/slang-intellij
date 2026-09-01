package slang.plugin.language

import com.intellij.openapi.fileTypes.LanguageFileType
import slang.plugin.SlangBundle
import javax.swing.Icon

class SlangFileType private constructor() : LanguageFileType(SlangLanguage) {

    override fun getName(): String = "Slang"

    override fun getDescription(): String = SlangBundle.message("name") + " shader language file"

    override fun getDefaultExtension(): String = "slang"

    override fun getIcon(): Icon = SlangIcons.FILE

    companion object {
        @JvmField
        val INSTANCE = SlangFileType()
    }
}
