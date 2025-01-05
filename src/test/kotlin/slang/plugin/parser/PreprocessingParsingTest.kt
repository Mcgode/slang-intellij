package slang.plugin.parser

import com.intellij.testFramework.ParsingTestCase
import slang.plugin.language.SlangParserDefinition

class PreprocessingParsingTest: ParsingTestCase(
    "parser/preprocessing",
    "slang",
    SlangParserDefinition()
) {
    override fun getTestDataPath(): String = "src/test/testData"

    fun testBasicDefine() = doTest(true)
    fun testBasicInclude() = doTest(true)
}