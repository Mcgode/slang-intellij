package slang.plugin.parser

import com.intellij.testFramework.ParsingTestCase
import slang.plugin.language.SlangParserDefinition

class FunctionParsingTest: ParsingTestCase(
    "parser/function",
    "slang",
    SlangParserDefinition()
)
{
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testBasic() = doTest(true)
    fun testDeclarationAndDefinition() = doTest(true)
}