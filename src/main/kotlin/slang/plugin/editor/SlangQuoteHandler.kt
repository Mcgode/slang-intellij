package slang.plugin.editor

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler
import slang.plugin.lexer.SlangTokenTypes

/**
 * Auto-closes `"` inside Slang string literals, skips over the closing quote when you type one, and
 * removes the pair on backspace. This is a client-side editor action, so it just needs to recognise
 * the lexer's string token — [SlangTokenTypes.STRING].
 */
class SlangQuoteHandler : SimpleTokenSetQuoteHandler(SlangTokenTypes.STRINGS)
