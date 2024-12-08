package slang.plugin.parser

import com.intellij.testFramework.ParsingTestCase

import slang.plugin.language.SlangParserDefinition

class StructDeclarationParsingTest: ParsingTestCase(
    "parser/struct-declaration",
    "slang",
    SlangParserDefinition()
)
{
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testBasicEmpty() {
        doTest(true)
    }

    fun testNamedEmpty() {
        doTest(true)
    }

    fun testInheritance() {
        doTest(true)
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