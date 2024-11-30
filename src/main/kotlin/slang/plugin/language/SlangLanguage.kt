package slang.plugin.language

import com.intellij.lang.Language

class SlangLanguage private constructor() : Language("Slang")
{
    companion object {
        val INSTANCE = SlangLanguage()
    }
}