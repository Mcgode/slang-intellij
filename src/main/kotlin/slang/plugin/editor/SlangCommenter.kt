package slang.plugin.editor

import com.intellij.codeInsight.generation.IndentedCommenter

class SlangCommenter : IndentedCommenter {
    override fun getLineCommentPrefix(): String = "//"
    override fun getBlockCommentPrefix(): String = "/*"
    override fun getBlockCommentSuffix(): String = "*/"
    override fun getCommentedBlockCommentPrefix(): String? = null
    override fun getCommentedBlockCommentSuffix(): String? = null

    /** Put `//` at the line's indentation, the way C / Java / Kotlin do — not at column 0. */
    override fun forceIndentedLineComment(): Boolean = true
}
