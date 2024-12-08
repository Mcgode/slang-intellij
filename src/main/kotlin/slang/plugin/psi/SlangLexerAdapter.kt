package slang.plugin.psi

import com.intellij.lexer.FlexAdapter
import slang.plugin.psi.SlangLexer

class SlangLexerAdapter: FlexAdapter(SlangLexer(null)) {
}