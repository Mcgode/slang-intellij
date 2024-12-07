package slang.plugin.psi

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import slang.plugin.psi.SlangOldTypes.*

import slang.plugin.language.parser.SlangParserUtil

object SlangPsiUtil: SlangParserUtil() {

    @JvmStatic fun nextToken(builder: PsiBuilder): IElementType? {
        return builder.lookAhead(1)
    }

    @JvmStatic fun skipToMatchingToken(builder: PsiBuilder, tokenType: IElementType): IElementType? {
        while (true) {
            if (builder.eof())
                return null
            else if (GeneratedParserUtilBase.nextTokenIs(builder, tokenType))
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
            LEFT_PAREN -> tokenType = skipToMatchingToken(builder, RIGHT_PAREN)
            LEFT_BRACKET -> tokenType = skipToMatchingToken(builder, RIGHT_BRACKET)
            LEFT_BRACE -> tokenType = skipToMatchingToken(builder, RIGHT_BRACE)
        }

        return tokenType
    }

}