package slang.plugin.psi

import com.intellij.psi.tree.IElementType
import slang.plugin.language.SlangLanguage

class SlangMacroArgumentToken(val index: Int): IElementType("MacroArg$index", SlangLanguage.INSTANCE)