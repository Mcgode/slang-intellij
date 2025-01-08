package slang.plugin.psi

import com.intellij.lang.PsiBuilder
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.util.alsoIfNull
import slang.plugin.language.parser.SlangParser
import slang.plugin.language.parser.data.Scope
import slang.plugin.language.parser.data.TokenData
import slang.plugin.psi.types.SlangTypes
import slang.plugin.psi.utils.ExpandedMacro
import slang.plugin.psi.utils.RollbackableMarker

class SlangPsiUtil {

    private var currentExpandedMacro: ExpandedMacro? = null
    private var currentExpansionIndex: Int = -1
    private var processingPreprocessorDirective = false

    private fun getParser(builder: PsiBuilder): SlangParser =
        (builder as GeneratedParserUtilBase.Builder).parser as SlangParser

    private fun skipToMatchingToken(builder: PsiBuilder, tokenType: IElementType): IElementType? {
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

    fun skipBalancedToken(builder: PsiBuilder): IElementType? {
        builder.advanceLexer()
        var tokenType = builder.tokenType
        when (tokenType) {
            SlangTypes.LEFT_PAREN -> tokenType = skipToMatchingToken(builder, SlangTypes.RIGHT_PAREN)
            SlangTypes.LEFT_BRACKET -> tokenType = skipToMatchingToken(builder, SlangTypes.RIGHT_BRACKET)
            SlangTypes.LEFT_BRACE -> tokenType = skipToMatchingToken(builder, SlangTypes.RIGHT_BRACE)
        }

        return tokenType
    }

    fun isFirstNonWhitespaceTokenOnNewLine(builder: PsiBuilder): Boolean {
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

    private fun consumePreprocessorDirectives(builder: PsiBuilder) {
        while (builder.tokenType == SlangTypes.PREPROCESSOR_DIRECTIVE) {
            processingPreprocessorDirective = true
            val level = GeneratedParserUtilBase.ErrorState.get(builder).currentFrame.level
            getParser(builder).parsePreprocessorDirective(builder, level)
            processingPreprocessorDirective = false
        }
    }

    private fun parseMacroCallInternal(
        builder: PsiBuilder,
        expandedMacro: ExpandedMacro,
        startIndex: Int,
        level: Int)
    : Pair<ExpandedMacro, Int>?
    {
        if (level >= 100) {
            builder.error("Too many recursions")
            return null
        }

        var currentIndex = startIndex

        val getText: () -> String = {
            if (level == 0)
                builder.tokenText!!
            else
                expandedMacro.dynamicTokens[startIndex].string
        }

        val typeIs: (IElementType) -> Boolean = { token ->
            if (level == 0) {
                GeneratedParserUtilBase.nextTokenIs(builder, token)
            }
            else {
                virtualNextTokenIs(builder, token, expandedMacro, currentIndex)
            }
        }

        val remapToken: (IElementType) -> Unit = {
            if (level == 0)
                builder.remapCurrentToken(it)
            else
                expandedMacro.dynamicTokens[currentIndex].token = it
        }

        val advance: () -> Unit = {
            if (level == 0)
                builder.advanceLexer()
            else {
                createGhostToken(builder, expandedMacro.dynamicTokens[currentIndex])
                expandedMacro.dynamicTokens.removeAt(currentIndex)
            }
        }

        val parser = getParser(builder)
        val macroExpansion = parser.getMacroExpansion(getText())!!

        val marker = GeneratedParserUtilBase.enter_section_(builder)
        remapToken(SlangTypes.DEFINE_NAME)
        advance()

        var validMacro = true
        if (typeIs(SlangTypes.LEFT_PAREN))
            TODO("Handle macro arguments")

        val innerExpandedMacro = ExpandedMacro(macroExpansion, arrayListOf())

        // Recursively expand macro
        var index = 0
        while (index < innerExpandedMacro.dynamicTokens.size) {
            val entry = innerExpandedMacro.dynamicTokens[index]

            if (entry.token == SlangTypes.IDENTIFIER) {
                parser.getMacroExpansion(entry.string)?.let {
                    index = parseMacroCallInternal(builder, innerExpandedMacro, index, level + 1)?.second
                        ?: innerExpandedMacro.dynamicTokens.size
                }?.alsoIfNull { index++ }
            }
            else
                index++
        }
        expandedMacro.dynamicTokens.addAll(startIndex, innerExpandedMacro.dynamicTokens)

        GeneratedParserUtilBase.exit_section_(builder, marker, SlangTypes.MACRO_CALL, true)

        return if (validMacro)
            Pair(expandedMacro, currentIndex)
        else
            null
    }

    private fun parseMacroCall(builder: PsiBuilder): ExpandedMacro?
    {
        if (currentExpandedMacro != null || builder.tokenType != SlangTypes.IDENTIFIER || processingPreprocessorDirective)
            return null

        return parseMacroCallInternal(builder, ExpandedMacro(), 0, 0)?.first
    }

    private fun handlePreprocessing(builder: PsiBuilder) {
        consumePreprocessorDirectives(builder)
        if (currentExpandedMacro == null && builder.tokenType == SlangTypes.IDENTIFIER && !processingPreprocessorDirective) {
            getParser(builder).getMacroExpansion(builder.tokenText!!)?.let {
                parseMacroCall(builder)?.let {
                    currentExpandedMacro = it
                    currentExpansionIndex = 0
                }
            }
        }
    }

    private fun createGhostToken(builder: PsiBuilder, tokenData: TokenData) {
        val marker = builder.mark()
        marker.done(SlangGhostToken(tokenData.token, tokenData.string))
    }

    fun getTokenType(builder: PsiBuilder): IElementType? {
        return if (currentExpandedMacro == null)
            builder.tokenType
        else
            currentExpandedMacro?.dynamicTokens?.get(currentExpansionIndex)?.token
    }

    fun getTokenText(builder: PsiBuilder): String? {
        return if (currentExpandedMacro == null)
            builder.tokenText
        else
            currentExpandedMacro?.dynamicTokens?.get(currentExpansionIndex)?.string
    }

    fun advanceLexer(builder: PsiBuilder) {
        if (currentExpandedMacro != null) {
            createGhostToken(builder, currentExpandedMacro!!.dynamicTokens[currentExpansionIndex])
            currentExpansionIndex++

            if (currentExpansionIndex >= currentExpandedMacro!!.dynamicTokens.size) {
                currentExpandedMacro = null
                currentExpansionIndex = -1
            }
        }
        else {
            builder.advanceLexer()
        }
        handlePreprocessing(builder)
    }

    private fun virtualNextTokenIs(
        builder: PsiBuilder,
        tokenType: IElementType,
        expandedMacro: ExpandedMacro,
        currentIndex: Int): Boolean
    {
        GeneratedParserUtilBase.addVariant(builder, tokenType.toString())
        return if (expandedMacro.dynamicTokens.size > currentIndex)
            expandedMacro.dynamicTokens[currentIndex].token == tokenType
        else
            false
    }

    fun nextTokenIs(builder: PsiBuilder, tokenType: IElementType): Boolean {
        handlePreprocessing(builder)
        return if (currentExpandedMacro == null)
            GeneratedParserUtilBase.nextTokenIs(builder, tokenType)
        else
            virtualNextTokenIs(builder, tokenType, currentExpandedMacro!!, currentExpansionIndex)
    }

    fun nextTokenIs(builder: PsiBuilder, vararg tokenTypes: IElementType): Boolean {
        for (tokenType in tokenTypes)
            if (nextTokenIs(builder, tokenType))
                return true
        return false
    }

    fun nextTokenIs(builder: PsiBuilder, name: String): Boolean {
        handlePreprocessing(builder)
        if (currentExpandedMacro == null)
            return GeneratedParserUtilBase.nextTokenIs(builder, name)
        else {
            GeneratedParserUtilBase.addVariant(builder, name)
            return currentExpandedMacro!!.dynamicTokens[currentExpansionIndex].string == name
        }
    }

    fun nextTokenIs(builder: PsiBuilder, names: Iterable<String>): Boolean {
        for (name in names)
            if (nextTokenIs(builder, name))
                return true
        return false
    }

    fun consumeToken(builder: PsiBuilder, tokenType: IElementType): Boolean {
        if (nextTokenIs(builder, tokenType)) {
            advanceLexer(builder)
            return true
        }
        return false
    }

    fun consumeToken(builder: PsiBuilder, name: String): Boolean {
        if (nextTokenIs(builder, name)) {
            advanceLexer(builder)
            return true
        }
        return false
    }

    fun remapCurrentToken(builder: PsiBuilder, tokenType: IElementType) {
        if (currentExpandedMacro == null)
            builder.remapCurrentToken(tokenType)
        else
            currentExpandedMacro!!.dynamicTokens[currentExpansionIndex].token = tokenType
    }

    fun mark(builder: PsiBuilder): RollbackableMarker {
        return RollbackableMarker(builder.mark(), currentExpandedMacro, currentExpansionIndex)
    }

    fun rollbackTo(marker: RollbackableMarker) {
        marker.underlyingMarker.rollbackTo()
        currentExpandedMacro = marker.currentExpandedMacro
        currentExpansionIndex = marker.currentExpandedIndex
    }

    private fun moveSteps(builder: PsiBuilder, steps: Int) {
        for (i in 0 until steps) {
            if (eof(builder))
                return
            advanceLexer(builder)
        }
    }

    fun lookAhead(builder: PsiBuilder, steps: Int): IElementType? {
        if (steps <= 0)
            throw RuntimeException("Not looking ahead")
        if (currentExpandedMacro != null && currentExpandedMacro!!.dynamicTokens.size > steps + currentExpansionIndex) {
            return currentExpandedMacro!!.dynamicTokens[steps + currentExpansionIndex].token
        }
        val marker = mark(builder)
        moveSteps(builder, steps)
        val result = if (eof(builder))
            null
        else
            getTokenType(builder)
        rollbackTo(marker)
        return result
    }

    fun lookAheadText(builder: PsiBuilder, steps: Int): String? {
        if (steps <= 0)
            throw RuntimeException("Not looking ahead")
        if (currentExpandedMacro != null && currentExpandedMacro!!.dynamicTokens.size > steps + currentExpansionIndex) {
            return currentExpandedMacro!!.dynamicTokens[steps + currentExpansionIndex].string
        }
        val marker = mark(builder)
        moveSteps(builder, steps)
        val result = if (eof(builder))
            null
        else
            getTokenText(builder)
        rollbackTo(marker)
        return result
    }

    fun eof(builder: PsiBuilder): Boolean = currentExpandedMacro == null && builder.eof()

    fun findNamespaceScope(name: String, scopes: Iterable<Scope>): Scope? {
        return scopes.find { it.type == SlangTypes.NAMESPACE_DECLARATION && it.namespaceName == name }
    }

    fun peekModernStyleVarDeclaration(builder: PsiBuilder): Boolean {
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