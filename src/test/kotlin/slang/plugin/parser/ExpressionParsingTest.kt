package slang.plugin.parser

import com.intellij.testFramework.ParsingTestCase
import slang.plugin.language.SlangParserDefinition

class ExpressionParsingTest: ParsingTestCase(
    "parser/expression",
    "slang",
    SlangParserDefinition()) {

    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testLiterals() = doTest(true)
    fun testPostfixExpression() = doTest(true)

}