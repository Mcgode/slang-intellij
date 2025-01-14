package slang.plugin.psi

import com.intellij.psi.tree.IElementType
import slang.plugin.language.SlangLanguage

class SlangMacroArgumentToken(val argumentIndex: Int)
    : IElementType("MacroArg$argumentIndex", SlangLanguage.INSTANCE)