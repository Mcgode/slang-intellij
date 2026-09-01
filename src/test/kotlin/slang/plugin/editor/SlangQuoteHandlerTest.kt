package slang.plugin.editor

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import slang.plugin.highlight.SlangSyntaxHighlighter

/**
 * Drives [SlangQuoteHandler] directly against a Slang-lexer highlighter — `BasePlatformTestCase`
 * does not load this plugin's `plugin.xml`, so the `lang.quoteHandler` extension point and the
 * typed-quote action are not wired up in the test IDE. See [SlangLineIndentTest].
 */
class SlangQuoteHandlerTest : BasePlatformTestCase() {

    private fun editorFor(text: String): EditorEx {
        myFixture.configureByText("a.txt", text)
        val editor = myFixture.editor as EditorEx
        editor.highlighter = LexerEditorHighlighter(SlangSyntaxHighlighter(), EditorColorsManager.getInstance().globalScheme)
            .also { it.setText(editor.document.immutableCharSequence) }
        return editor
    }

    private val handler = SlangQuoteHandler()

    fun testOpeningQuoteAtStartOfString() {
        val text = """let name = "material";"""
        val editor = editorFor(text)
        val openQuote = text.indexOf('"')
        val iter = editor.highlighter.createIterator(openQuote)
        assertTrue(handler.isOpeningQuote(iter, openQuote))
    }

    fun testClosingQuoteAtEndOfString() {
        val text = """let s = "hi";"""
        val editor = editorFor(text)
        val closeQuote = text.lastIndexOf('"')
        val iter = editor.highlighter.createIterator(closeQuote)
        assertTrue(handler.isClosingQuote(iter, closeQuote))
        assertFalse(handler.isOpeningQuote(iter, closeQuote))
    }

    fun testUnterminatedStringIsNonClosed() {
        val editor = editorFor("let s = \"oops\n")
        val iter = editor.highlighter.createIterator(8) // the opening quote of "oops
        assertTrue(handler.hasNonClosedLiteral(editor, iter, 9))
    }

    fun testClosedStringIsNotNonClosed() {
        val editor = editorFor("""let s = "ok";""")
        val iter = editor.highlighter.createIterator(8)
        assertFalse(handler.hasNonClosedLiteral(editor, iter, 9))
    }

    fun testNotAQuoteOutsideStrings() {
        val editor = editorFor("int x = 1;")
        val iter = editor.highlighter.createIterator(4)
        assertFalse(handler.isOpeningQuote(iter, 4))
        assertFalse(handler.isInsideLiteral(iter))
    }
}
