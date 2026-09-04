package slang.plugin.editor

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.Tokenizer

/**
 * Spell-checks comment prose only. Identifiers, keywords, numbers, operators and string literals
 * (mostly `#include` paths and attribute arguments in a shader) are left alone.
 *
 * The base strategy already strips the comment markers via the language's [SlangCommenter] and
 * skips suppression comments.
 */
class SlangSpellcheckingStrategy : SpellcheckingStrategy() {
    override fun getTokenizer(element: PsiElement): Tokenizer<*> =
        if (element is PsiComment) super.getTokenizer(element) else EMPTY_TOKENIZER
}
