package slang.plugin.language

import com.intellij.lang.Language

object SlangLanguage : Language("Slang") {
    private fun readResolve(): Any = SlangLanguage
    override fun getDisplayName(): String = "Slang"
}
