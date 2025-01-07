package slang.plugin.psi.utils

import com.intellij.lang.PsiBuilder

data class RollbackableMarker(
    val underlyingMarker: PsiBuilder.Marker,
    val currentExpandedMacro: ExpandedMacro?,
    val currentExpandedIndex: Int
)
