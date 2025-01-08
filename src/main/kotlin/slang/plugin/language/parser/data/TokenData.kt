package slang.plugin.language.parser.data

import com.intellij.psi.tree.IElementType

data class TokenData(var token: IElementType, val string: String)
