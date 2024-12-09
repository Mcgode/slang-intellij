package slang.plugin.parser

import com.intellij.testFramework.ParsingTestCase
import slang.plugin.language.SlangParserDefinition

class ClassDeclarationParsingTest: ParsingTestCase(
    "parser/class-declaration",
    "slang",
    SlangParserDefinition()
) {

    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testBasic() = doTest(true)
    fun testMultiVariableMembers() = doTest(true)
}