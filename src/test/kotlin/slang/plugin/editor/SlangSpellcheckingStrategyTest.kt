package slang.plugin.editor

import com.intellij.lang.LanguageParserDefinitions
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import slang.plugin.lang.SlangParserDefinition
import slang.plugin.language.SlangFileType
import slang.plugin.language.SlangLanguage

/**
 * `BasePlatformTestCase` does not load this plugin's `plugin.xml`; the parser definition is
 * registered here so the fixture builds Slang PSI.
 */
class SlangSpellcheckingStrategyTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        LanguageParserDefinitions.INSTANCE.addExplicitExtension(SlangLanguage, SlangParserDefinition(), testRootDisposable)
    }

    private val strategy = SlangSpellcheckingStrategy()

    private fun tokenizerAt(source: String, at: String): Tokenizer<*> {
        val file = myFixture.configureByText(SlangFileType.INSTANCE, source)
        val leaf = file.findElementAt(source.indexOf(at)) ?: error("no element at '$at'")
        return strategy.getTokenizer(leaf)
    }

    fun testCommentsAreSpellChecked() {
        assertNotSame(SpellcheckingStrategy.EMPTY_TOKENIZER, tokenizerAt("// spellcheck this line\n", "spellcheck"))
        assertNotSame(SpellcheckingStrategy.EMPTY_TOKENIZER, tokenizerAt("/* and this block */", "block"))
    }

    fun testIdentifiersAreNotSpellChecked() {
        assertSame(SpellcheckingStrategy.EMPTY_TOKENIZER, tokenizerAt("let gBufferHandle = 1;", "gBufferHandle"))
    }

    fun testStringLiteralsAreNotSpellChecked() {
        assertSame(SpellcheckingStrategy.EMPTY_TOKENIZER, tokenizerAt("""#include "common/pbrLut";""", "pbrLut"))
    }
}
