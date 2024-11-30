package slang.plugin.psi

import com.intellij.lexer.FlexAdapter
import slang.plugin.language.psi.SlangLexer

class SlangLexerAdapter: FlexAdapter(SlangLexer(null)) {
}