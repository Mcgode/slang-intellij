package slang.plugin.psi

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType

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

}