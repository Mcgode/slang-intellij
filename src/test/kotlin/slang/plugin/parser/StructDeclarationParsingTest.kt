package slang.plugin.parser

import com.intellij.testFramework.ParsingTestCase

import slang.plugin.language.SlangParserDefinition

class StructDeclarationParsingTest: ParsingTestCase(
    "parser/struct-declaration",
    "expr",
    SlangParserDefinition()
)
{
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testBasic() {
        doTest(true);
    }

    fun testQualifier() {
        doTest(true);
    }

    fun testSemantic() {
        doTest(true);
    }

    fun testMultiMember() {
        doTest(true);
    }
}