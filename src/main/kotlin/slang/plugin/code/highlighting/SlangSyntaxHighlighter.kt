package slang.plugin.code.highlighting

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.elementType
import slang.plugin.psi.SlangLexerAdapter
import slang.plugin.psi.SlangTokenSets
import slang.plugin.psi.types.SlangTypes

class SlangSyntaxHighlighter: SyntaxHighlighterBase(), Annotator {

    override fun getHighlightingLexer(): Lexer {
        return SlangLexerAdapter()
    }

    private fun mapTokenToTextAttribute(tokenType: IElementType): TextAttributesKey? {
        return when (tokenType) {
            in (SlangTokenSets.NUMERIC_LITERALS) -> SlangTextAttributes.SLANG_NUMERIC_LITERALS_TEXT
            in (SlangTokenSets.BRACES) -> SlangTextAttributes.SLANG_BRACE_TEXT
            in (SlangTokenSets.PARENTHESIS) -> SlangTextAttributes.SLANG_PAREN_TEXT
            in (SlangTokenSets.BRACKETS) -> SlangTextAttributes.SLANG_BRACKET_TEXT
            SlangTypes.STRING_LITERAL -> SlangTextAttributes.SLANG_STRING_LITERAL_TEXT
            SlangTypes.LINE_COMMENT -> SlangTextAttributes.SLANG_LINE_COMMENT_TEXT
            SlangTypes.MULTILINE_COMMENT -> SlangTextAttributes.SLANG_MULTILINE_COMMENT_TEXT
            SlangTypes.COMMA -> SlangTextAttributes.SLANG_COMMA_TEXT
            SlangTypes.DOT -> SlangTextAttributes.SLANG_DOT_TEXT
            SlangTypes.SEMICOLON -> SlangTextAttributes.SLANG_SEMICOLON_TEXT
            SlangTypes.PREPROCESSOR_DIRECTIVE -> SlangTextAttributes.SLANG_MACRO_TEXT
            TokenType.BAD_CHARACTER -> SlangTextAttributes.SLANG_BAD_CHARACTER_TEXT
            else -> null
        }
    }

    override fun getTokenHighlights(element: IElementType): Array<TextAttributesKey> {
        return pack(mapTokenToTextAttribute(element))
    }

    private fun mapPsiElementToTextAttribute(element: PsiElement): TextAttributesKey? {
        when (element.elementType) {
            SlangTypes.FUNCTION_NAME -> return SlangTextAttributes.SLANG_FUNCTION_DECL_TEXT
            SlangTypes.PARAMETER_NAME -> return SlangTextAttributes.SLANG_PARAMETER_TEXT
            SlangTypes.STRUCT_NAME -> return SlangTextAttributes.SLANG_STRUCT_NAME_TEXT
            SlangTypes.CLASS_NAME -> return SlangTextAttributes.SLANG_CLASS_NAME_TEXT
            SlangTypes.DEFINE_NAME -> return SlangTextAttributes.SLANG_MACRO_TEXT
        }
        return null
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        mapPsiElementToTextAttribute(element)?.let {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .textAttributes(it)
                .create()
        }
    }

}