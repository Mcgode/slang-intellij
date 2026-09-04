package slang.plugin.editor

import com.intellij.lang.LanguageParserDefinitions
import com.intellij.psi.impl.search.IndexPatternBuilder
import com.intellij.psi.search.PsiTodoSearchHelper
import com.intellij.testFramework.ExtensionTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import slang.plugin.lang.SlangParserDefinition
import slang.plugin.language.SlangFileType
import slang.plugin.language.SlangLanguage

/**
 * `BasePlatformTestCase` does not load this plugin's `plugin.xml`, so the parser definition and the
 * `indexPatternBuilder` extension are registered here for the test.
 */
class SlangIndexPatternBuilderTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        LanguageParserDefinitions.INSTANCE.addExplicitExtension(SlangLanguage, SlangParserDefinition(), testRootDisposable)
        ExtensionTestUtil.maskExtensions(IndexPatternBuilder.EP_NAME, listOf(SlangIndexPatternBuilder()), testRootDisposable)
    }

    private fun todoTexts(source: String): List<String> {
        val file = myFixture.configureByText(SlangFileType.INSTANCE, source)
        return PsiTodoSearchHelper.getInstance(project).findTodoItems(file)
            .map { it.textRange.substring(file.text).trim() }
    }

    fun testLineCommentTodo() {
        assertEquals(listOf("TODO: wire up the light buffer"), todoTexts(
            """
            // TODO: wire up the light buffer
            let x = 1;
            """.trimIndent(),
        ))
    }

    fun testBlockCommentFixme() {
        assertEquals(listOf("FIXME later"), todoTexts("let y = 2; /* FIXME later */"))
    }

    fun testPlainCommentIsNotATodo() {
        assertEmpty(todoTexts("// just a note\nlet z = 3;"))
    }
}
