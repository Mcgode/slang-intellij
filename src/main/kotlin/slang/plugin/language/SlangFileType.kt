package slang.plugin.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class SlangFileType private constructor(): LanguageFileType(SlangLanguage.INSTANCE) {

    companion object {
        val INSTANCE = SlangFileType()
    }

    override fun getName(): String {
        return "Slang file"
    }

    override fun getDescription(): String {
        return "Slang language file"
    }

    override fun getDefaultExtension(): String {
        return "slang"
    }

    override fun getIcon(): Icon {
        return SlangIcon.Icon;
    }
}