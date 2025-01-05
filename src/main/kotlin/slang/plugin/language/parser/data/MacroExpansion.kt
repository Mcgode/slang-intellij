package slang.plugin.language.parser.data

import com.intellij.psi.tree.IElementType

data class MacroExpansion(var content: ArrayList<IElementType> = arrayListOf())
