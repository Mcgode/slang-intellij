package slang.plugin.editor

import com.intellij.lang.LanguageCommenters
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import slang.plugin.lang.SlangParserDefinition
import slang.plugin.language.SlangFileType
import slang.plugin.language.SlangLanguage

/**
 * Runs the real Comment-with-Line-Comment / Block-Comment actions on a Slang file.
 *
 * `BasePlatformTestCase` does not load this plugin's `plugin.xml`, so the language extensions are
 * registered here for the duration of the test.
 */
class SlangCommenterTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        LanguageParserDefinitions.INSTANCE.addExplicitExtension(SlangLanguage, SlangParserDefinition(), testRootDisposable)
        LanguageCommenters.INSTANCE.addExplicitExtension(SlangLanguage, SlangCommenter(), testRootDisposable)
    }

    private fun afterAction(action: String, before: String): String {
        myFixture.configureByText(SlangFileType.INSTANCE, before)
        myFixture.performEditorAction(action)
        return myFixture.editor.document.text
    }

    fun testLineCommentSingleLine() {
        assertEquals(
            "//let x = 1;",
            afterAction(IdeActions.ACTION_COMMENT_LINE, "let x =<caret> 1;"),
        )
    }

    fun testLineCommentTogglesBack() {
        val once = afterAction(IdeActions.ACTION_COMMENT_LINE, "<caret>let x = 1;")
        myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        assertEquals("let x = 1;", myFixture.editor.document.text)
        assertEquals("//let x = 1;", once)
    }

    fun testLineCommentSelection() {
        val before = "<selection>let a = 1;\nlet b = 2;\n</selection>"
        assertEquals("//let a = 1;\n//let b = 2;\n", afterAction(IdeActions.ACTION_COMMENT_LINE, before))
    }

    fun testBlockComment() {
        assertEquals(
            "let x = /*1*/;",
            afterAction(IdeActions.ACTION_COMMENT_BLOCK, "let x = <selection>1</selection>;"),
        )
    }

    fun testLineCommentGoesAtTheIndent() {
        // `//` at the code's indentation, not column 0 (SlangCommenter is an IndentedCommenter).
        assertEquals(
            "void f() {\n    //let x = 1;\n}\n",
            afterAction(IdeActions.ACTION_COMMENT_LINE, "void f() {\n    let x =<caret> 1;\n}\n"),
        )
    }

    fun testUncommentBlock() {
        assertEquals(
            "let x = 1;",
            afterAction(IdeActions.ACTION_COMMENT_BLOCK, "let x = <selection>/*1*/</selection>;"),
        )
    }
}
