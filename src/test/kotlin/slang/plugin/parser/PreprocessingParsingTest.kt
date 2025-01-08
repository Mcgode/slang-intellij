package slang.plugin.parser

import com.intellij.testFramework.ParsingTestCase
import slang.plugin.language.SlangParserDefinition

class PreprocessingParsingTest: ParsingTestCase(
    "parser/preprocessing",
    "slang",
    SlangParserDefinition()
) {
    override fun getTestDataPath(): String = "src/test/testData"

    fun testBasicInclude() = doTest(true)
    fun testBasicDefine() = doTest(true)
    fun testBasicUndef() = doTest(true)
    fun testUpleveledDefine() = doTest(true)
    fun testRecursiveExpand() = doTest(true)
}