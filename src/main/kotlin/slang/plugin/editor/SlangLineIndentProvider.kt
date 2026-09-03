package slang.plugin.editor

import com.intellij.lang.Language
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.codeStyle.SemanticEditorPosition.SyntaxElement
import com.intellij.psi.impl.source.codeStyle.lineIndent.JavaLikeLangLineIndentProvider
import com.intellij.psi.impl.source.codeStyle.lineIndent.JavaLikeLangLineIndentProvider.JavaLikeElement
import com.intellij.psi.tree.IElementType
import slang.plugin.language.SlangLanguage
import slang.plugin.lexer.SlangTokenTypes

/**
 * Enter-key auto-indent for Slang. `slangd` only reformats on `}` / `;`, never on a newline, so
 * without this, pressing Enter inside a `{ … }` block leaves the caret at column 0.
 *
 * [JavaLikeLangLineIndentProvider] already implements brace/paren/bracket-aware indentation for
 * C-like languages; all it needs is a lexer-token → abstract syntax-element mapping. It works off
 * the editor highlighter (our [slang.plugin.lexer.SlangLexer]) rather than PSI, which suits the
 * lexer-only setup.
 */
class SlangLineIndentProvider : JavaLikeLangLineIndentProvider() {

    override fun mapType(tokenType: IElementType): SyntaxElement? = SYNTAX_MAP[tokenType]

    override fun isSuitableForLanguage(language: Language): Boolean = language.isKindOf(SlangLanguage)
}

private val SYNTAX_MAP: Map<IElementType, SyntaxElement> = mapOf(
    TokenType.WHITE_SPACE to JavaLikeElement.Whitespace,
    SlangTokenTypes.LBRACE to JavaLikeElement.BlockOpeningBrace,
    SlangTokenTypes.RBRACE to JavaLikeElement.BlockClosingBrace,
    SlangTokenTypes.LPAREN to JavaLikeElement.LeftParenthesis,
    SlangTokenTypes.RPAREN to JavaLikeElement.RightParenthesis,
    SlangTokenTypes.LBRACKET to JavaLikeElement.ArrayOpeningBracket,
    SlangTokenTypes.RBRACKET to JavaLikeElement.ArrayClosingBracket,
    SlangTokenTypes.SEMICOLON to JavaLikeElement.Semicolon,
    SlangTokenTypes.COMMA to JavaLikeElement.Comma,
    SlangTokenTypes.LINE_COMMENT to JavaLikeElement.LineComment,
    SlangTokenTypes.BLOCK_COMMENT to JavaLikeElement.BlockComment,
)
