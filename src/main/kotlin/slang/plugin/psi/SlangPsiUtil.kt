package slang.plugin.psi

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import slang.plugin.language.parser.data.Scope

import slang.plugin.psi.types.SlangTypes

object SlangPsiUtil: GeneratedParserUtilBase() {

    @JvmStatic fun skipToMatchingToken(builder: PsiBuilder, tokenType: IElementType): IElementType? {
        while (true) {
            if (builder.eof())
                return null
            else if (nextTokenIs(builder, tokenType))
            {
                builder.advanceLexer()
                return tokenType
            }
            skipBalancedToken(builder)
        }
    }

    @JvmStatic fun skipBalancedToken(builder: PsiBuilder): IElementType? {
        builder.advanceLexer()
        var tokenType = builder.tokenType
        when (tokenType) {
            SlangTypes.LEFT_PAREN -> tokenType = skipToMatchingToken(builder, SlangTypes.RIGHT_PAREN)
            SlangTypes.LEFT_BRACKET -> tokenType = skipToMatchingToken(builder, SlangTypes.RIGHT_BRACKET)
            SlangTypes.LEFT_BRACE -> tokenType = skipToMatchingToken(builder, SlangTypes.RIGHT_BRACE)
        }

        return tokenType
    }

    @JvmStatic fun isFirstNonWhitespaceTokenOnNewLine(builder: PsiBuilder): Boolean {
        var currentOffset = -1
        var isNewLine = false
        while (true) {
            val tokenType = builder.rawLookup(currentOffset)

            // If we rolled back to the first lexeme of the file, consider it as a new line, since it's the first line
            if (tokenType == null || tokenType == SlangTypes.NEW_LINE) {
                isNewLine = true
                break
            }
            else if (tokenType != TokenType.WHITE_SPACE)
                break
            currentOffset--
        }

        return isNewLine
    }

    @JvmStatic fun nextTokenIs(builder: PsiBuilder, names: Iterable<String>): Boolean {
        for (name in names)
            if (nextTokenIs(builder, name))
                return true
        return false
    }

    @JvmStatic fun findNamespaceScope(name: String, scopes: Iterable<Scope>): Scope? {
        return scopes.find { it.type == SlangTypes.NAMESPACE_DECLARATION && it.namespaceName == name }
    }

    @JvmStatic fun peekModernStyleVarDeclaration(builder: PsiBuilder): Boolean {
        return if (!nextTokenIs(builder, SlangTypes.IDENTIFIER))
            false
        else {
            when (builder.lookAhead(1)) {
                SlangTypes.COLON,
                SlangTypes.COMMA,
                SlangTypes.RIGHT_PAREN,
                SlangTypes.RIGHT_BRACE,
                SlangTypes.RIGHT_BRACKET,
                SlangTypes.LEFT_BRACE -> true
                else -> false
            }
        }
    }

}