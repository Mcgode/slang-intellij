package slang.plugin.editor

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import slang.plugin.highlight.SlangSyntaxHighlighter
import slang.plugin.language.SlangLanguage

/**
 * Exercises [SlangLineIndentProvider] directly against a Slang-lexer editor highlighter.
 *
 * It cannot go through the `lineIndentProvider` extension point or `EnterHandler`: `BasePlatformTestCase`
 * with the current build setup does not load this plugin's `plugin.xml`, so `.slang` resolves to plain
 * text and none of the language extensions are registered. See the "actually run it" follow-up.
 */
class SlangLineIndentTest : BasePlatformTestCase() {

    /** Indent string the provider computes for the line that begins at the `<caret>` marker. */
    private fun indentAtCaret(textWithCaret: String): String? {
        val caret = textWithCaret.indexOf("<caret>")
        val text = textWithCaret.replace("<caret>", "")
        myFixture.configureByText("a.txt", text)
        val editor = myFixture.editor as EditorEx
        editor.highlighter = LexerEditorHighlighter(SlangSyntaxHighlighter(), EditorColorsManager.getInstance().globalScheme)
            .also { it.setText(editor.document.immutableCharSequence) }
        return SlangLineIndentProvider().getLineIndent(project, editor, SlangLanguage, caret)
    }

    fun testIndentsOneLevelAfterOpeningBrace() {
        assertEquals("    ", indentAtCaret("void f()\n{\n<caret>\n}\n"))
    }

    fun testKeepsIndentForNextStatementInBlock() {
        assertEquals("    ", indentAtCaret("void f()\n{\n    int a = 1;\n<caret>\n}\n"))
    }

    fun testDedentsToBlockLevelBeforeClosingBrace() {
        assertEquals("", indentAtCaret("void f()\n{\n    int a = 1;\n<caret>}\n"))
    }

    fun testNestedBlocksStackIndent() {
        assertEquals("        ", indentAtCaret("void f()\n{\n    if (x)\n    {\n<caret>\n    }\n}\n"))
    }
}
