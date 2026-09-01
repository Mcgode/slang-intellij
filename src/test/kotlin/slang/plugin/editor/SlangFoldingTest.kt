package slang.plugin.editor

import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class SlangFoldingTest : BasePlatformTestCase() {

    private fun folds(text: String): List<FoldingDescriptor> {
        val file = myFixture.configureByText("a.slang", text)
        val document = myFixture.getDocument(file)
        return SlangFoldingBuilder().buildFoldRegions(file, document, false).toList()
    }

    private fun foldedText(text: String): List<String> =
        folds(text).map { text.substring(it.range.startOffset, it.range.endOffset) }

    fun testMultiLineBraceBlockFolds() {
        val text = "struct Foo\n{\n    int a;\n}\n"
        assertSize(1, folds(text))
        assertEquals("{\n    int a;\n}", foldedText(text).single())
    }

    fun testSingleLineBraceBlockDoesNotFold() {
        assertEmpty(folds("struct Foo { int a; }\n"))
    }

    fun testNestedBracesProduceTwoRegions() {
        assertSize(2, folds("void f()\n{\n    if (x)\n    {\n        g();\n    }\n}\n"))
    }

    fun testMultiLineBlockCommentFolds() {
        assertSize(1, folds("/*\n * doc\n */\nstruct S;\n"))
    }

    fun testSingleLineBlockCommentDoesNotFold() {
        assertEmpty(folds("/* one liner */ struct S;\n"))
    }

    fun testPreprocessorIfEndifFolds() {
        val text = "#ifndef GUARD\n#define GUARD\nint x;\n#endif\n"
        val regions = folds(text)
        assertSize(1, regions)
        assertTrue(foldedText(text).single().startsWith("#ifndef GUARD"))
    }

    fun testUnbalancedBraceIsIgnored() {
        assertEmpty(folds("void f()\n{\n    g();\n"))
    }
}
