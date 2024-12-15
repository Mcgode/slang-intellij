package slang.plugin.language.parser

import ai.grazie.utils.attributes.Attributes
import com.intellij.lang.ASTNode
import com.intellij.lang.LightPsiParser
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.lang.parser.GeneratedParserUtilBase.*
import com.intellij.psi.tree.IElementType
import slang.plugin.language.parser.data.TypeSpec
import slang.plugin.psi.types.SlangTypes
import slang.plugin.psi.SlangPsiUtil

//
// This parser is implemented based on the CPP slang parser from
// https://github.com/shader-slang/slang/blob/master/source/slang/slang-parser.cpp
//
// Current supported commit revision is 'b118451e301d734e3e783b3acdf871f3f6ea851c'
//
open class SlangParser: PsiParser, LightPsiParser {

    private val enableGlslCode = true
    private var isInVariadicGenerics = false
    private var genericDepth = 0
    private var genericShrConsumeOffset = -1
    private val identifierLookup = SlangIdentifierLookup()

    override fun parse(type: IElementType, builder: PsiBuilder): ASTNode {
        parseLight(type, builder)
        return builder.treeBuilt
    }

    override fun parseLight(type: IElementType, baseBuilder: PsiBuilder) {

        val builder = adapt_builder_(type, baseBuilder, this, null)
        identifierLookup.initDefault("")
        val marker = enter_section_(builder, 0, _COLLAPSE_, null)
        val result = parseSourceFile(builder, 1)

        exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION)

    }

    private fun nextTokenAfterModifiersIs(builder: PsiBuilder, name: String): Boolean {
        while (true) {
            if (nextTokenIs(builder, name))
                return true
            else if (identifierLookup.lookUp(builder.tokenText ?: "n/a")?.identifier == SlangIdentifierLookup.IdentifierStyle.TypeModifier) {
                builder.advanceLexer()
                continue
            }
            return false
        }
    }

    private fun nextTokenAheadIs(builder: PsiBuilder, name: String, offset: Int): Boolean {
        if (offset <= 0)
            return false

        val marker = builder.mark();
        for (i in 0 until offset)
            builder.advanceLexer();
        val result = builder.tokenText == name
        marker.rollbackTo()
        return result
    }

    private fun parseSourceFile(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseRoot"))
            return false

        while (true) {
            val cursor = current_position_(builder)
            // TODO: Implement parseGlslGlobalDecl (see slang/slang-parser.cpp:4889)
            if (!parseDecl(builder, level))
                break
            if (!empty_element_parsed_guard_(builder, "parseRoot", cursor))
                break
        }

        return true
    }

    private fun parseDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDecl"))
            return false

        val marker = enter_section_(builder)
        var result = parseModifiers(builder, level + 1)
        result = result && parseDeclWithModifiers(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.DECLARATION, result)
        return result
    }

    private fun parseModifiers(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDeclarationModifiers"))
            return false

        while (true) {
            when (builder.tokenType) {
                SlangTypes.IDENTIFIER -> {
                    if (tryParseUsingSyntaxDecl(builder, level, SyntaxType.Modifier)) {
                        val marker = enter_section_(builder)
                        builder.advanceLexer()
                        exit_section_(builder, marker, SlangTypes.TYPE_MODIFIER, true)
                        continue
                    }
                    else if (nextTokenIs(builder, "no_diff")) {
                        val marker = enter_section_(builder)
                        builder.advanceLexer()
                        exit_section_(builder, marker, SlangTypes.TYPE_MODIFIER, true)
                        continue
                    }
                    else if (enableGlslCode)
                        if (consumeToken(builder, "flat")) {
                            val marker = enter_section_(builder)
                            builder.advanceLexer()
                            exit_section_(builder, marker, SlangTypes.TYPE_MODIFIER, true)
                            continue
                        }
                    break
                }
                SlangTypes.LEFT_BRACKET -> {
                    if (!parseSquareBracketAttributes(builder, level))
                        return false
                }
                else -> break
            }
        }

        return true
    }

    private fun parseDeclWithModifiers(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDeclWithModifiers"))
            return false

        when (builder.tokenType) {
            SlangTypes.IDENTIFIER -> {
                // TODO: see slang/slang-parser.cpp:4706

                return parseDeclaratorDecl(builder, level)
            }

            // It is valid in HLSL/GLSL to have an "empty" declaration
            // that consists of just a semicolon. In particular, this
            // gets used a lot in GLSL to attach custom semantics to
            // shader input or output.
            SlangTypes.SEMICOLON -> {
                builder.advanceLexer()
            }

            SlangTypes.LEFT_BRACE, SlangTypes.LEFT_PAREN -> {
                // We shouldn't be seeing an LBrace or an LParent when expecting a decl.
                // However, recovery logic may lead us here. In this case we just
                // skip the whole `{}` block and return an empty decl.
                SlangPsiUtil.skipBalancedToken(builder)
            }

            else -> {
                return parseDeclaratorDecl(builder, level)
            }
        }

        return true
    }

    private enum class SyntaxType {
        Modifier,
        Declaration,
        Expression
    }

    private fun tryParseUsingSyntaxDecl(builder: PsiBuilder, level: Int, type: SyntaxType): Boolean {
        if (!recursion_guard_(builder, level, "parseUsingSyntaxDecl"))
            return false

        if (!nextTokenIs(builder, SlangTypes.IDENTIFIER))
            return false

        val name = builder.tokenText!!
        val result = identifierLookup.lookUp(name) ?: return false
        // TODO: operation is a lot more complex in slang/slang-parser.cpp:1154

        return when (type) {
            SyntaxType.Modifier -> result.identifier == SlangIdentifierLookup.IdentifierStyle.TypeModifier
            else -> false
        }
    }

    private fun parseSquareBracketAttributes(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseSquareBracketAttributes"))
            return false

        var result = consumeToken(builder, SlangTypes.LEFT_BRACKET)
        val doubleBracketed = consumeToken(builder, SlangTypes.LEFT_BRACKET)

        while (result) {
            // Note: When parsing we just construct an AST node for an
            // "unchecked" attribute, and defer all detailed semantic
            // checking until later.
            //
            // An alternative would be to perform lookup of an `AttributeDecl`
            // at this point, similar to what we do for `SyntaxDecl`, but it
            // seems better to not complicate the parsing process anymore.
            //

            val marker = enter_section_(builder)
            result = parseAttributeName(builder, level + 1)

            if (consumeToken(builder, SlangTypes.LEFT_PAREN)) {
                // HLSL-style `[name(arg0, ...)]` attribute
                while (result) {
                    val argMarker = enter_section_(builder)
                    result = parseArgExpr(builder, level + 2)
                    exit_section_(builder, argMarker, SlangTypes.MODIFIER_ARGUMENT, result)

                    if (consumeToken(builder, SlangTypes.RIGHT_PAREN))
                        break

                    result = result && consumeToken(builder, SlangTypes.COMMA)
                }
            }

            exit_section_(builder, marker, SlangTypes.UNCHECKED_ATTRIBUTE, result)

            if (nextTokenIs(builder, SlangTypes.RIGHT_BRACKET))
                break

            // If there is a comma consume it. It appears that the comma is optional.
            consumeToken(builder, SlangTypes.COMMA)
        }

        result = result && consumeToken(builder, SlangTypes.RIGHT_BRACKET)
        if (doubleBracketed)
            result = result && consumeToken(builder, SlangTypes.RIGHT_BRACKET)

        return result
    }

    private fun parseDeclaratorDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDeclaratorDecl"))
            return false

        val typeSpec = parseTypeSpec(builder, level)

        var result = typeSpec != null

        if (typeSpec?.decl == false)
            result = result && parseBracketTypeSuffix(builder, level)

        if (consumeToken(builder, SlangTypes.SEMICOLON)) {
            result = result && (typeSpec?.decl == true)
            if (!result)
                builder.error("Declaration does not declare anything")
            return result
        }

        // It is possible that we have a plain `struct`, `enum`,
        // or similar declaration that isn't being used to declare
        // any variable, and the user didn't put a trailing
        // semicolon on it:
        //
        //      struct Batman
        //      {
        //          int cape;
        //      }
        //
        // We want to allow this syntax (rather than give an
        // inscrutable error), but also support the less common
        // idiom where that declaration is used as part of
        // a variable declaration:
        //
        //      struct Robin
        //      {
        //          float tights;
        //      } boyWonder;
        //
        // As a bit of a hack (insofar as it means we aren't
        // *really* compatible with arbitrary HLSL code), we
        // will check if there are any more tokens on the
        // same line as the closing '}', and if not, we
        // will treat it like the end of the declaration.
        //
        if (typeSpec?.decl == true)
        {
            if (builder.eof() || SlangPsiUtil.isFirstNonWhitespaceTokenOnNewLine(builder)) {
                // The token after the `}` is at the start of its
                // own line, which means it can't be on the same line.
                //
                // This means the programmer probably wants to
                // just treat this as a declaration.
                return result
            }
        }

        val marker = enter_section_(builder)
        val initDeclaratorState = if (result) parseInitDeclarator(builder, level + 1) else null
        result = initDeclaratorState != null

        // Rather than parse function declarators properly for now,
        // we'll just do a quick disambiguation here. This won't
        // matter unless we actually decide to support function-type parameters,
        // using C syntax.
        //
        if ((nextTokenIs(builder, null, SlangTypes.LEFT_PAREN, SlangTypes.LESS_OP)

                // Only parse as a function if we didn't already see mutually-exclusive
                // constructs when parsing the declarator.
                && initDeclaratorState?.initializer == false) && !initDeclaratorState.semantics) {

            result = parseTraditionalFuncDecl(builder, level + 1)
            exit_section_(builder, marker, SlangTypes.FUNCTION_DECLARATION, result)
        }

        // Otherwise we are looking at a variable declaration, which could be one in a sequence...
        else {
            exit_section_(builder, marker, SlangTypes.VARIABLE_DECL, result)

            while (result) {
                when (builder.tokenType) {
                    SlangTypes.COMMA -> {
                        val markerB = enter_section_(builder)
                        result = parseInitDeclarator(builder, level + 1) != null
                        exit_section_(builder, markerB, SlangTypes.VARIABLE_DECL, result)
                    }

                    else -> break
                }
            }

            result = result && consumeToken(builder, SlangTypes.SEMICOLON)
        }
        return result
    }

    private fun parseTypeSpec(builder: PsiBuilder, level: Int): TypeSpec? {
        if (!recursion_guard_(builder, level, "parseTypeSpec"))
            return null

        val marker = enter_section_(builder)
        val typeSpec = parseSimpleTypeSpec(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.TYPE_SPEC, typeSpec != null)
        return typeSpec
    }

    private fun parseBracketTypeSuffix(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseBracketTypeSuffix"))
            return false

        while (true) {
            when (builder.tokenType) {
                SlangTypes.LEFT_BRACKET -> {
                    val marker = enter_section_(builder)
                    var result = consumeToken(builder, SlangTypes.LEFT_BRACKET)

                    if (!nextTokenIs(builder, SlangTypes.RIGHT_BRACKET)) {
                        result = result && parseExpression(builder, level + 1)
                    }
                    result = result && consumeToken(builder, SlangTypes.RIGHT_BRACKET)

                    exit_section_(builder, marker, SlangTypes.ARRAY_SPECIFIER, result)

                    if (!result)
                        return false
                }
                else -> break
            }
        }

        return true
    }

    private enum class Precedence {
        Invalid,
        Comma,
        Assignment,
        TernaryConditional,
        LogicalOr,
        LogicalAnd,
        BitOr,
        BitXor,
        BitAnd,
        EqualityComparison,
        RelationalComparison,
        BitShift,
        Additive,
        Multiplicative,
    }
    private fun parseExpression(builder: PsiBuilder, level: Int, precedence: Precedence = Precedence.Comma): Boolean {
        if (!recursion_guard_(builder, level, "parseExpression"))
            return false

        val marker = enter_section_(builder)
        var result = parseLeafExpression(builder, level + 1)
        result = result && parseInfixExprWithPrecedence(builder, level + 1, precedence)
        exit_section_(builder, marker, SlangTypes.EXPRESSION, result)
        return result
    }

    private data class InitDeclaratorState(var initializer: Boolean = false, var semantics: Boolean = false)

    private fun parseInitDeclarator(builder: PsiBuilder, level: Int, allowEmpty: Boolean = false): InitDeclaratorState? {
        if (!recursion_guard_(builder, level, "parseInitDeclarator"))
            return null

        val marker = enter_section_(builder)
        val state = InitDeclaratorState()
        var result = parseSemanticDeclarator(builder, level + 1, allowEmpty, state)
        if (consumeToken(builder, SlangTypes.ASSIGN_OP)) {
            state.initializer = true
            result = result && parseInitExpr(builder, level + 1)
        }
        exit_section_(builder, marker, SlangTypes.INIT_DECLARATOR, result)
        return if (result) state else null
    }

    private fun parseSemanticDeclarator(builder: PsiBuilder, level: Int, allowEmpty: Boolean, state: InitDeclaratorState): Boolean {
        if (!recursion_guard_(builder, level, "parseSemanticDeclarator"))
            return false

        var result = parseDeclarator(builder, level, allowEmpty)
        result = result && parseOptSemantics(builder, level, state)
        return result
    }

    private fun parseDeclarator(builder: PsiBuilder, level: Int, allowEmpty: Boolean): Boolean {
        if (!recursion_guard_(builder, level, "parseDeclarator"))
            return false

        if (consumeToken(builder, SlangTypes.MUL_OP)) {
            val marker = enter_section_(builder)
            val result = parseDeclarator(builder, level + 1, allowEmpty)
            exit_section_(builder, marker, SlangTypes.POINTER_DECLARATOR, result)
            return result
        }

        val result = parseDirectAbstractDeclarator(builder, level, allowEmpty)
        return result
    }

    private fun parseOptSemantics(builder: PsiBuilder, level: Int, state: InitDeclaratorState = InitDeclaratorState()): Boolean {
        if (!recursion_guard_(builder, level, "parseOptSemantics"))
            return false

        if (!consumeToken(builder, SlangTypes.COLON))
            return true

        state.semantics = true

        var result = true
        while (result) {
            result = parseSemantic(builder, level)

            // If we see a '<', ignore the remaining.
            if (nextTokenIs(builder, SlangTypes.LESS_OP))
            {
                builder.advanceLexer()
                while (true) {
                    if (builder.eof()) {
                        break
                    } else if (nextTokenIs(builder, SlangTypes.GREATER_OP)) {
                        builder.advanceLexer()
                        break
                    } else {
                        builder.advanceLexer()
                    }
                }
            }

            // If we see another `:`, then that means there
            // is yet another semantic to be processed.
            // Otherwise we assume we are at the end of the list.
            //
            // TODO: This could produce sub-optimal diagnostics
            // when the user *meant* to apply multiple semantics
            // to a single declaration:
            //
            //     Foo foo : register(t0)   register(s0);
            //                            ^
            //         missing ':' here   |
            //
            // However, that is an uncommon occurrence, and trying
            // to continue parsing semantics here even if we didn't
            // see a colon forces us to be careful about
            // avoiding an infinite loop here.
            if (!consumeToken(builder, SlangTypes.COLON)) {
                return result
            }
        }
        return false
    }

    private fun parseDirectAbstractDeclarator(builder: PsiBuilder, level: Int, allowEmpty: Boolean): Boolean {
        if (!recursion_guard_(builder, level, "parseDirectAbstractDeclarator"))
            return false

        var result: Boolean

        when (builder.tokenType) {
            SlangTypes.IDENTIFIER -> {
                val marker = enter_section_(builder)
                result = consumeToken(builder, SlangTypes.IDENTIFIER)
                exit_section_(builder, marker, SlangTypes.NAME_DECLARATOR, result)
            }
            SlangTypes.LEFT_PAREN -> {
                // Note(tfoley): This is a point where disambiguation is required.
                // We could be looking at an abstract declarator for a function-type
                // parameter:
                //
                //     void F( int(int) );
                //
                // Or we could be looking at the use of parentheses in an ordinary
                // declarator:
                //
                //     void (*f)(int);
                //
                // The difference really doesn't matter right now, but we err in
                // the direction of assuming the second case.
                //
                // TODO: We should consider just not supporting this case at all,
                // since it can't come up in current Slang (no pointer or function-type
                // support), and we might be able to introduce alternative syntax
                // to get around these issues when those features come online.
                //
                result = consumeToken(builder, SlangTypes.LEFT_PAREN)
                result = result && parseDeclarator(builder, level, allowEmpty)
                result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
            }
            else -> return allowEmpty
        }

        // Postfix additions
        while (result) {
            when (builder.tokenType) {
                SlangTypes.LEFT_BRACKET -> {
                    val marker = enter_section_(builder, level, _LEFT_, SlangTypes.ARRAY_DECLARATOR, null)
                    result = consumeToken(builder, SlangTypes.LEFT_BRACKET)
                    if (!nextTokenIs(builder, SlangTypes.RIGHT_BRACKET))
                        result = result && parseExpression(builder, level + 1)
                    result = result && consumeToken(builder, SlangTypes.RIGHT_BRACKET)
                    exit_section_(builder, level, marker, result, false, null)
                    break
                }
                SlangTypes.LEFT_PAREN -> break
                SlangTypes.LESS_OP -> break // TODO: see slang/slang-parser.cpp:2011
                else -> break
            }
        }

        return result
    }

    private fun parseInitExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseInitExpr"))
            return false

        return parseExpression(builder, level, Precedence.Assignment)
    }

    private fun parseSimpleTypeSpec(builder: PsiBuilder, level: Int): TypeSpec? {
        if (!recursion_guard_(builder, level, "parseSimpleTypeSpec"))
            return null

        val typeSpec = TypeSpec()

        if (nextTokenIs(builder, "struct")) {
            typeSpec.decl = true
            typeSpec.expr = true
            return if (parseStruct(builder, level)) typeSpec else null
        }
        if (nextTokenIs(builder, "class")) {
            typeSpec.decl = true
            typeSpec.expr = true
            return if (parseClass(builder, level)) typeSpec else null
        }
        if (nextTokenIs(builder, "enum")) {
            typeSpec.decl = true
            typeSpec.expr = true
            return if (parseEnumDecl(builder, level)) typeSpec else null
        }
        if (nextTokenIs(builder, "expand") || nextTokenIs(builder, "each")) {
            typeSpec.expr = true
            return if (parsePrefixExpr(builder, level)) typeSpec else null
        }
        if (consumeToken(builder, "functype")) {
            typeSpec.expr = true
            return if (parseFuncTypeExpr(builder, level)) typeSpec else null
        }

        // Declaration identifier can begin with '::' to mark global scope
        consumeToken(builder, SlangTypes.SCOPE)

        var result = consumeToken(builder, SlangTypes.IDENTIFIER)

        typeSpec.expr = true

        while (result) {
            when (builder.tokenType) {
                SlangTypes.LESS_OP -> {
                    result = parseGenericApp(builder, level)
                }
                SlangTypes.SCOPE -> {
                    result = consumeToken(builder, SlangTypes.SCOPE)
                    result = result && parseStaticMemberType(builder, level)
                }
                SlangTypes.DOT -> {
                    result = consumeToken(builder, SlangTypes.DOT)
                    result = result && parseMemberType(builder, level)
                }
                else -> break
            }
        }

        return if (result) typeSpec else null
    }

    private fun parseStruct(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseStruct"))
            return false

        val marker = enter_section_(builder, level, _NONE_)
        var result = consumeToken(builder, "struct")

        if (nextTokenIs(builder, SlangTypes.LEFT_BRACKET)) {
            result = result && parseSquareBracketAttributes(builder, level + 1)
        }

        consumeToken(builder, SlangTypes.COMPLETION_REQUEST)

        if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            val nameMarker = enter_section_(builder, level + 1, _NONE_)
            result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
            exit_section_(builder, level + 1, nameMarker, SlangTypes.STRUCT_NAME, result, false, null)
        }

        val callback: (PsiBuilder, Int) -> Boolean = { b, l ->
            if (!recursion_guard_(b, l, "parseStructCallback"))
                false
            else {
                var callbackResult = parseOptionalInheritanceClause(b, l)
                if (consumeToken(builder, SlangTypes.ASSIGN_OP)) {
                    callbackResult = callbackResult && parseTypeExp(b, l)
                    callbackResult = callbackResult && consumeToken(builder, SlangTypes.SEMICOLON)
                    callbackResult
                }
                else if (consumeToken(builder, SlangTypes.SEMICOLON)) {
                    callbackResult
                }
                else {
                    callbackResult = callbackResult && maybeParseGenericConstraints(b, l)
                    callbackResult = callbackResult && parseDeclBody(b, l)
                    callbackResult
                }
            }
        }
        result = result && parseOptGenericDecl(builder, level, callback)

        exit_section_(builder, level, marker, SlangTypes.STRUCT_DECLARATION, result, false, null)

        return result
    }

    private fun parseClass(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseClass"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "class")

        consumeToken(builder, SlangTypes.COMPLETION_REQUEST)

        if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            val nameMarker = enter_section_(builder)
            result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
            exit_section_(builder, nameMarker, SlangTypes.CLASS_NAME, result)
        }

        result = result && parseOptionalInheritanceClause(builder, level + 1)
        result = result && parseDeclBody(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.CLASS_DECLARATION, result)
        return result
    }

    private fun parseEnumDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseEnumDecl"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "enum")

        // Consume 'class' if is enum class
        consumeToken(builder, "class")

        consumeToken(builder, SlangTypes.COMPLETION_REQUEST)

        if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            val nameMarker = enter_section_(builder)
            result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
            exit_section_(builder, nameMarker, SlangTypes.ENUM_NAME, result)
        }

        val parseInner: (PsiBuilder, Int) -> Boolean = { b, l ->
            var innerResult = parseOptionalInheritanceClause(b, l)
            innerResult = innerResult && maybeParseGenericConstraints(b, l)
            innerResult = innerResult && consumeToken(builder, SlangTypes.LEFT_BRACE)

            while (innerResult) {
                if (consumeToken(builder, SlangTypes.RIGHT_BRACE))
                    break
                innerResult = parseEnumCaseDecl(b, l)
                if (innerResult && consumeToken(builder, SlangTypes.RIGHT_BRACE))
                    break
                else
                    innerResult = innerResult && consumeToken(builder, SlangTypes.COMMA)
            }

            innerResult
        }
        result = result && parseOptGenericDecl(builder, level + 1, parseInner)

        exit_section_(builder, marker, SlangTypes.ENUM_DECLARATION, result)
        return result
    }

    private fun parsePrefixExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parsePrefixExpr"))
            return false

        if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            if (consumeToken(builder, "new")) {
                val marker = enter_section_(builder)
                val result = parsePostfixExpr(builder, level + 1)
                exit_section_(builder, marker, SlangTypes.NEW_EXPRESSION, result)
                return result
            }
            else if (consumeToken(builder, "spirv_asm")) {
                return parseSpirVAsmExpr(builder, level)
            }
            else if (isInVariadicGenerics)
            {
                // If we are inside a variadic generic, we also need to recognize
                // the new `expand` and `each` keyword for dealing with variadic packs.
                if (consumeToken(builder, "expand"))
                    return parseExpandExpr(builder, level)
                else if (consumeToken(builder, "each"))
                    return parseEachExpr(builder, level)
            }
            return parsePostfixExpr(builder, level)
        }
        else if (nextTokenIs(builder, null,
                SlangTypes.NOT_OP,
                SlangTypes.INC_OP,
                SlangTypes.DEC_OP,
                SlangTypes.MUL_OP,
                SlangTypes.BIT_AND_OP,
                SlangTypes.BIT_NOT_OP,
                SlangTypes.ADD_OP,
                SlangTypes.SUB_OP)) {
            val marker = enter_section_(builder)
            var result = parseOperator(builder, level + 1)
            result = result && parsePrefixExpr(builder, level + 1)
            exit_section_(builder, marker, SlangTypes.PREFIX_EXPRESSION, result)
            return result
        }

        return parsePostfixExpr(builder, level)
    }

    private fun parseFuncTypeExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseFuncTypeExpr"))
            return false

        val marker = enter_section_(builder)

        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        while (result) {
            if (nextTokenIs(builder, SlangTypes.RIGHT_PAREN))
                break
            result = parseTypeExp(builder, level + 1)
            if (result && nextTokenIs(builder, SlangTypes.RIGHT_PAREN))
                break
            result = result && consumeToken(builder, SlangTypes.COMMA)
        }
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        result = result && parseTypeExp(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.FUNCTYPE_EXPRESSION, result)
        return result
    }

    private fun parseGenericApp(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGenericApp"))
            return false

        val marker = enter_section_(builder, level, _LEFT_)

        var result = consumeToken(builder, SlangTypes.LESS_OP)

        genericDepth++

        // For now assume all generics have at least one argument
        result = result && parseGenericArg(builder, level + 1)
        while (result && consumeToken(builder, SlangTypes.COMMA))
            result = parseGenericArg(builder, level + 1)

        if (result && nextTokenIs(builder, SlangTypes.SHR_OP)) {
            when (genericShrConsumeOffset) {
                -1 -> {
                    if (genericDepth > 1)
                        genericShrConsumeOffset = builder.currentOffset
                    else
                        result = false
                }
                builder.currentOffset -> {
                    builder.advanceLexer()
                    genericShrConsumeOffset = -1
                }
                else -> {
                    genericShrConsumeOffset = -1
                }
            }
        }
        else if (result)
            result = consumeToken(builder, SlangTypes.GREATER_OP)

        genericDepth--

        exit_section_(builder, level, marker, SlangTypes.GENERIC_APP_EXPRESSION, result, false, null)
        return result
    }

    private fun parseGenericArg(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGenericArg"))
            return false

        // The grammar for generic arguments needs to be a super-set of the
        // grammar for types and for expressions, because we do not know
        // which to expect at each argument position during parsing.
        //
        // For the most part the expression grammar is more permissive than
        // the type grammar, but types support modifiers that are not
        // (currently) allowed in pure expression contexts.
        //
        // We could in theory allow modifiers to appear in expression contexts
        // and deal with the cases where this should not be allowed downstream,
        // but doing so runs a high risk of changing the meaning of existing code
        // (notably in cases where a user might have used a variable name that
        // overlaps with a language modifier keyword).
        //
        // Instead, we will simply detect the case where modifiers appear on
        // a generic argument here, as a special case.
        //
        val marker = enter_section_(builder)

        val beforeModifiersOffset = builder.currentOffset
        var result = parseModifiers(builder, level + 1)

        if (builder.currentOffset > beforeModifiersOffset) {
            // If there are any modifiers, then we know that we are actually
            // in the type case.
            //
            result = result && (parseSimpleTypeSpec(builder, level + 1) != null)
            result = result && parsePostFixTypeExprSuffix(builder, level + 1)
            result = result && parseInfixTypeExprSuffix(builder, level + 1)
        }
        else
            result = result && parseArgExpr(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.GENERIC_APP_ARGUMENT, result)
        return result
    }

    private fun parseStaticMemberType(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:2344
    }

    private fun parseMemberType(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:2333
    }

    private fun parseOptGenericDecl(builder: PsiBuilder, level: Int, parseInner: (PsiBuilder, Int) -> Boolean): Boolean {
        if (nextTokenIs(builder, SlangTypes.LESS_OP)) {
            val result = parseGenericDeclImpl(builder, level)
            return result && parseInner(builder, level)
        }
        return parseInner(builder, level)
    }

    private fun parseOptionalInheritanceClause(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseOptionalInheritanceClause"))
            return false

        if (!nextTokenIs(builder, SlangTypes.COLON))
            return true

        builder.advanceLexer()

        var result: Boolean
        do {
            val marker = enter_section_(builder)
            result = parseTypeExp(builder, level + 1)
            exit_section_(builder, marker, SlangTypes.INHERITANCE_DECLARATION, result)
        } while (result && consumeToken(builder, SlangTypes.COMMA))

        return result
    }

    private fun parseTypeExp(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseTypeExp"))
            return false

        val marker = enter_section_(builder)
        val result = parseType(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.TYPE_EXPRESSION, result)
        return result
    }

    private fun maybeParseGenericConstraints(builder: PsiBuilder, level: Int): Boolean {
        return true // TODO: see slang/slang-parser.cpp:1654
    }

    private fun parseDeclBody(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDeclBody"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, SlangTypes.LEFT_BRACE)
        while (result)
        {
            when (builder.tokenType) {
                SlangTypes.RIGHT_BRACE -> {
                    result = consumeToken(builder, SlangTypes.RIGHT_BRACE)
                    break
                }
                else -> result = parseDecl(builder, level + 1)
            }
        }

        exit_section_(builder, marker, SlangTypes.BODY_DECL, result)
        return result
    }

    private fun parseSemantic(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseSemantic"))
            return false

        if (nextTokenIs(builder, "register")) {
            return parseHlslRegisterSemantic(builder, level)
        } else if (nextTokenIs(builder, "packoffset")) {
            return parseHlslPackOffsetSemantic(builder, level)
        } else if (nextTokenIs(builder, "read") && builder.lookAhead(1) == SlangTypes.LEFT_PAREN) {
            return parseRayPayloadAccessSemantic(builder, level, false)
        } else if (nextTokenIs(builder, "write") && builder.lookAhead(1) == SlangTypes.LEFT_PAREN) {
            return parseRayPayloadAccessSemantic(builder, level, true)
        } else if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            val marker = enter_section_(builder)
            val result = consumeToken(builder, SlangTypes.IDENTIFIER)
            exit_section_(builder, marker, SlangTypes.HLSL_SIMPLE_SEMANTIC, result)
            return result
        } else if (nextTokenIs(builder, SlangTypes.INTEGER_LITERAL)) {
            val marker = enter_section_(builder)
            val result = consumeToken(builder, SlangTypes.INTEGER_LITERAL)
            exit_section_(builder, marker, SlangTypes.BITFIELD_MODIFIER, result)
            return result
        } else if (nextTokenIs(builder, SlangTypes.COMPLETION_REQUEST)) {
            val marker = enter_section_(builder)
            val result = consumeToken(builder, SlangTypes.COMPLETION_REQUEST)
            exit_section_(builder, marker, SlangTypes.HLSL_SIMPLE_SEMANTIC, result)
            return result
        }
        // expect an identifier, just to produce an error message
        return consumeToken(builder, SlangTypes.IDENTIFIER)
    }

    private fun parseHlslRegisterSemantic(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:3024
    }

    private fun parseHlslPackOffsetSemantic(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:3024
    }

    private fun parseRayPayloadAccessSemantic(builder: PsiBuilder, level: Int, write: Boolean): Boolean {
        return false // TODO: see slang/slang-parser.cpp:3093
    }

    private fun parseLeafExpression(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseLeafExpression"))
            return false

        return parsePrefixExpr(builder, level)
    }

    private fun getOpLevel(token: IElementType?, text: String?): Precedence {
        when (token) {
            SlangTypes.QUESTION_MARK -> return Precedence.TernaryConditional
            SlangTypes.COMMA -> return Precedence.Comma
            SlangTypes.ASSIGN_OP,
            SlangTypes.ADD_ASSIGN_OP,
            SlangTypes.SUB_ASSIGN_OP,
            SlangTypes.MUL_ASSIGN_OP,
            SlangTypes.DIV_ASSIGN_OP,
            SlangTypes.MOD_ASSIGN_OP,
            SlangTypes.SHL_ASSIGN_OP,
            SlangTypes.SHR_ASSIGN_OP,
            SlangTypes.OR_ASSIGN_OP,
            SlangTypes.AND_ASSIGN_OP,
            SlangTypes.XOR_ASSIGN_OP
                 -> return Precedence.Assignment
            SlangTypes.OR_OP -> return Precedence.LogicalOr
            SlangTypes.AND_OP -> return Precedence.LogicalAnd
            SlangTypes.BIT_OR_OP -> return Precedence.BitOr
            SlangTypes.BIT_XOR_OP -> return Precedence.BitXor
            SlangTypes.BIT_AND_OP -> return Precedence.BitAnd
            SlangTypes.EQL_OP, SlangTypes.NEQ_OP -> return Precedence.EqualityComparison
            SlangTypes.GREATER_OP, SlangTypes.GEQ_OP -> {
                // Don't allow these ops inside a generic argument
                if (genericDepth > 0)
                    return Precedence.Invalid
                return Precedence.RelationalComparison
            }
            SlangTypes.LESS_OP, SlangTypes.LEQ_OP -> return Precedence.RelationalComparison
            SlangTypes.SHR_OP -> {
                // Don't allow this op inside a generic argument
                if (genericDepth > 0)
                    return Precedence.Invalid
                return Precedence.BitShift
            }
            SlangTypes.SHL_OP -> return Precedence.BitShift
            SlangTypes.ADD_OP, SlangTypes.SUB_OP -> return Precedence.Additive
            SlangTypes.MUL_OP, SlangTypes.DIV_OP, SlangTypes.MOD_OP -> return Precedence.Multiplicative
        }
        if (text == "is" || text == "as")
            return Precedence.RelationalComparison
        return Precedence.Invalid
    }

    private enum class Associativity { Right, Left }
    private fun getAssociativityFromLevel(precedence: Precedence): Associativity {
        return if (precedence == Precedence.Assignment)
            Associativity.Right
        else
            Associativity.Left
    }

    private fun parseInfixExprWithPrecedence(builder: PsiBuilder, level: Int, precedence: Precedence): Boolean {
        if (!recursion_guard_(builder, level, "parseInfixExpression"))
            return false

        var result = true
        while (result) {
            val opToken = builder.tokenType
            val opPrecedence = getOpLevel(opToken, builder.tokenText)
            if (opPrecedence < precedence)
                break

            // Special case the "is" and "as" operators.
            if (nextTokenIs(builder, "is")) {
                builder.advanceLexer()
                val marker = enter_section_(builder)
                result = parseTypeExp(builder, level + 1)
                exit_section_(builder, marker, SlangTypes.IS_TYPE_EXPRESSION, result)
                continue
            }
            else if (nextTokenIs(builder, "as")) {
                builder.advanceLexer()
                val marker = enter_section_(builder)
                result = parseTypeExp(builder, level + 1)
                exit_section_(builder, marker, SlangTypes.AS_TYPE_EXPRESSION, result)
                continue
            }

            result = parseOperator(builder, level)

            // Special case the `?:` operator since it is the
            // one non-binary case we need to deal with.
            if (opToken == SlangTypes.QUESTION_MARK) {
                val marker = enter_section_(builder)
                result = result && parseExpression(builder, level + 1, opPrecedence)
                result = result && consumeToken(builder, SlangTypes.COLON)
                result = result && parseExpression(builder, level + 1, opPrecedence)
                exit_section_(builder, marker, SlangTypes.SELECT_EXPRESSION, result)
                continue
            }

            // Right expr
            val marker = enter_section_(builder)
            result = parseLeafExpression(builder, level + 1)

            while (result) {
                val nextOpPrecedence = getOpLevel(builder.tokenType, builder.tokenText)

                if (if (getAssociativityFromLevel(nextOpPrecedence) == Associativity.Right)
                    nextOpPrecedence < opPrecedence
                    else nextOpPrecedence <= opPrecedence) break

                result = parseInfixExprWithPrecedence(builder, level + 1, nextOpPrecedence)
            }

            if (opToken == SlangTypes.ASSIGN_OP)
                exit_section_(builder, marker, SlangTypes.ASSIGN_EXPRESSION, result)
            else
                exit_section_(builder, marker, SlangTypes.INFIX_EXPRESSION, result)
        }

        return result
    }

    private fun parseSpirVAsmExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseSpirVAsmExpression"))
            return false

        val marker = enter_section_(builder)

        var result = consumeToken(builder, SlangTypes.LEFT_BRACE)
        while (result) {
            if (nextTokenIs(builder, SlangTypes.RIGHT_BRACE))
                break

            result = parseSpirVAsmInst(builder, level + 1)
            // TODO: handle recovery l7748

            if (result && nextTokenIs(builder, SlangTypes.RIGHT_BRACE))
                break
            result = result && consumeToken(builder, SlangTypes.SEMICOLON)
        }
        result = result && consumeToken(builder, SlangTypes.RIGHT_BRACE)

        exit_section_(builder, marker, SlangTypes.SPIRV_ASM_EXPRESSION, result)
        return result
    }

    private fun parsePostfixExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parsePostFixExpr"))
            return false

        var result = parseAtomicExpr(builder, level)

        while (result) {
            if (nextTokenIs(builder, SlangTypes.INC_OP) || nextTokenIs(builder, SlangTypes.DEC_OP)) {
                val marker = enter_section_(builder, level, _LEFT_)
                result = parseOperator(builder, level + 1)
                exit_section_(builder, level, marker, SlangTypes.POSTFIX_EXPRESSION, result, false, null)
            }
            else if (nextTokenIs(builder, SlangTypes.LEFT_BRACKET)) {
                val marker = enter_section_(builder, level, _LEFT_)
                builder.advanceLexer()
                while (result) {
                    if (nextTokenIs(builder, SlangTypes.RIGHT_BRACKET))
                        break
                    result = parseArgExpr(builder, level + 1)
                    if (!consumeToken(builder, SlangTypes.COMMA))
                        break
                }
                result = result && consumeToken(builder, SlangTypes.RIGHT_BRACKET)
                exit_section_(builder, level, marker, SlangTypes.INDEX_EXPRESSION, result, false, null)
            }
            else if (nextTokenIs(builder, SlangTypes.LEFT_PAREN)) {
                val marker = enter_section_(builder, level, _LEFT_)

                builder.advanceLexer()
                while (result) {
                    if (nextTokenIs(builder, SlangTypes.RIGHT_PAREN))
                        break
                    result = parseArgExpr(builder, level + 1)
                    if (!consumeToken(builder, SlangTypes.COMMA))
                        break
                }
                result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)

                exit_section_(builder, level, marker, SlangTypes.INVOKE_EXPRESSION, result, false, null)
            }
            else if (nextTokenIs(builder, SlangTypes.SCOPE)) {
                val marker = enter_section_(builder, level, _LEFT_)
                builder.advanceLexer()
                result = consumeToken(builder, SlangTypes.IDENTIFIER)
                if (nextTokenIs(builder, SlangTypes.LESS_OP)) {
                    result = result && maybeParseGenericConstraints(builder, level + 1)
                }
                exit_section_(builder, level, marker, SlangTypes.STATIC_MEMBER_EXPRESSION, result, false, null)
            }
            else if (nextTokenIs(builder, SlangTypes.DOT) || nextTokenIs(builder, SlangTypes.RIGHT_ARROW)) {
                val tokenType = builder.tokenType
                val marker = enter_section_(builder, level, _LEFT_)
                builder.advanceLexer()
                result = parseDeclName(builder, level + 1)
                exit_section_(builder, level, marker,
                    if (tokenType == SlangTypes.DOT) SlangTypes.MEMBER_EXPRESSION else SlangTypes.DEREF_MEMBER_EXPRESSION,
                    result, false, null)
            }
            else
                break
        }

        return result
    }

    private fun parseAtomicExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseAtomicExpr"))
            return false

        // Either:
        // - parenthesized expression `(exp)`
        // - cast `(type) exp`
        //
        // Proper disambiguation requires mixing up parsing
        // and semantic checking (which we should do eventually)
        // but for now we will follow some heuristics.
        if (nextTokenIs(builder, SlangTypes.LEFT_PAREN)) {
            val marker = enter_section_(builder)

            // Only handles cases of `(type)`, where type is a single identifier,
            // and at this point the type is known
            if (nextTokenIs(builder, SlangTypes.IDENTIFIER) && builder.lookAhead(1) == SlangTypes.RIGHT_PAREN) {
                exit_section_(builder, marker, null, false)
                return false
                // TODO: see slang/slang-parser.cpp:6823
            }
            else
            {
                // TODO: see slang/slang-parser.cpp:6847
                exit_section_(builder, marker, null, false)
                return false
            }
        }
        // An initializer list `{ expr, ... }`
        else if (nextTokenIs(builder, SlangTypes.LEFT_BRACE)) {
            val marker = enter_section_(builder)
            var result = consumeToken(builder, SlangTypes.LEFT_BRACE)

            while (result) {
                if (nextTokenIs(builder, SlangTypes.RIGHT_BRACE))
                    break

                result = parseArgExpr(builder, level + 1)

                if (nextTokenIs(builder, SlangTypes.RIGHT_BRACE))
                    break
                result = result && consumeToken(builder, SlangTypes.COMMA)
            }
            result = result && consumeToken(builder, SlangTypes.RIGHT_BRACE)

            exit_section_(builder, marker, SlangTypes.INITIALIZER_LIST, result)
            return result
        }
        else if (nextTokenIs(builder, SlangTypes.INTEGER_LITERAL)) {
            val marker = enter_section_(builder)
            builder.advanceLexer()
            exit_section_(builder, marker, null, true)
            return true
        }
        else if (nextTokenIs(builder, SlangTypes.FLOAT_LITERAL)) {
            val marker = enter_section_(builder)
            builder.advanceLexer()
            exit_section_(builder, marker, null, true)
            return true
        }
        else if (nextTokenIs(builder, SlangTypes.STRING_LITERAL)) {
            val marker = enter_section_(builder)
            builder.advanceLexer()
            exit_section_(builder, marker, null, true)
            return true
        }
        else if (nextTokenIs(builder, SlangTypes.COMPLETION_REQUEST)) {
            val marker = enter_section_(builder)
            builder.advanceLexer()
            exit_section_(builder, marker, SlangTypes.VARIABLE_EXPRESSION, true)
            return true
        }
        else if (nextTokenIs(builder, SlangTypes.SCOPE)) {
            val marker = enter_section_(builder)
            builder.advanceLexer()

            var result = true
            if (nextTokenIs(builder, SlangTypes.COMPLETION_REQUEST))
                builder.advanceLexer()
            else {
                result = consumeToken(builder, SlangTypes.IDENTIFIER)
                if (nextTokenIs(builder, SlangTypes.LESS_OP)) {
                    maybeParseGenericConstraints(builder, level + 1)
                }
            }

            exit_section_(builder, marker, SlangTypes.VARIABLE_EXPRESSION, result)
            return result
        }
        else if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            val marker = enter_section_(builder)

            if (tryParseUsingSyntaxDecl(builder, level + 1, SyntaxType.Expression)) {
                exit_section_(builder, marker, SlangTypes.VARIABLE_EXPRESSION, true)
                return true
            }

            var result = parseDeclName(builder, level + 1)
            if (nextTokenIs(builder, SlangTypes.LESS_OP)) {
                result = maybeParseGenericConstraints(builder, level + 1)
            }
            exit_section_(builder, marker, SlangTypes.VARIABLE_EXPRESSION, result)
            return result
        }
        else {
            builder.error("Syntax error.")
            return false
        }
    }

    private fun parseOperator(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseOperator"))
            return false

        val marker = enter_section_(builder)
        builder.advanceLexer()
        exit_section_(builder, marker, SlangTypes.OPERATOR, true)
        return true
    }

    private fun parseArgExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseArgExpr"))
            return false

        return parseExpression(builder, level, Precedence.Assignment)
    }

    private fun parseType(builder: PsiBuilder, level: Int): Boolean {
        return parseInfixTypeExpr(builder, level)
    }

    private fun parseInfixTypeExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseInfixTypeExpr"))
            return false

        var result = parsePostFixTypeExpr(builder, level)
        result = result && parseInfixTypeExprSuffix(builder, level)
        return result
    }

    private fun parsePostFixTypeExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parsePostFixTypeExpr"))
            return false

        var result = parseAtomicTypeExpr(builder, level)
        result = result && parsePostFixTypeExprSuffix(builder, level)
        return result
    }

    private fun parseInfixTypeExprSuffix(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseInfixTypeExprSuffix"))
            return false

        var result = true
        while (result) {
            // As long as the next token is an `&`, we will try
            // to gobble up another type expression and form
            // a conjunction type expression.

            if (nextTokenIs(builder, SlangTypes.BIT_AND_OP)) {
                val marker = enter_section_(builder, level, _LEFT_)
                builder.advanceLexer()
                result = parsePostFixTypeExpr(builder, level)
                exit_section_(builder, level, marker, SlangTypes.AND_TYPE_EXPRESSION, result, false, null)
            }
            else
                break
        }

        return result
    }

    private fun parseAtomicTypeExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseAtomicTypeExpr"))
            return false

        return parseTypeSpec(builder, level) != null
    }

    private fun parsePostFixTypeExprSuffix(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parsePostFixTypeExprSuffix"))
            return false

        var result = true
        while (result) {
            if (nextTokenIs(builder, SlangTypes.LEFT_BRACKET)) {
                val marker = enter_section_(builder, level, _LEFT_)
                builder.advanceLexer()
                if (!nextTokenIs(builder, SlangTypes.RIGHT_BRACKET))
                    result = parseExpression(builder, level + 1)
                result = result && consumeToken(builder, SlangTypes.RIGHT_BRACKET)
                exit_section_(builder, level, marker, SlangTypes.INDEX_EXPRESSION, result, false, null)
            }
            else if (nextTokenIs(builder, SlangTypes.MUL_OP)) {
                val marker = enter_section_(builder, level, _LEFT_)
                builder.advanceLexer()
                exit_section_(builder, level, marker, SlangTypes.POINTER_TYPE_EXPRESSION, true, false, null)
            }
            else
                break
        }

        return result
    }

    private fun parseAttributeName(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseAttributeName"))
            return false

        // Strip initial :: if there is one
        val initialTokenWasScope = consumeToken(builder, SlangTypes.SCOPE)

        if (consumeToken(builder, SlangTypes.COMPLETION_REQUEST))
            return true

        var result = consumeToken(builder, SlangTypes.IDENTIFIER)

        if (!initialTokenWasScope && !nextTokenIs(builder, SlangTypes.SCOPE))
            return result

        while (result) {
            result = consumeToken(builder, SlangTypes.SCOPE)
            result = result && consumeToken(builder, SlangTypes.IDENTIFIER)

            if (!nextTokenIs(builder, SlangTypes.SCOPE))
                break
        }

        return result
    }

    private fun parseDeclName(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDeclName"))
            return false

        val marker = enter_section_(builder)

        var result = false
        if (consumeToken(builder, "operator")) {
            if (nextTokenIs(builder, null,
                    SlangTypes.ADD_OP,
                    SlangTypes.SUB_OP,
                    SlangTypes.MUL_OP,
                    SlangTypes.DIV_OP,
                    SlangTypes.MOD_OP,
                    SlangTypes.NOT_OP,
                    SlangTypes.BIT_NOT_OP,
                    SlangTypes.SHL_OP,
                    SlangTypes.SHR_OP,
                    SlangTypes.EQL_OP,
                    SlangTypes.NEQ_OP,
                    SlangTypes.GREATER_OP,
                    SlangTypes.LESS_OP,
                    SlangTypes.GEQ_OP,
                    SlangTypes.LEQ_OP,
                    SlangTypes.AND_OP,
                    SlangTypes.OR_OP,
                    SlangTypes.BIT_AND_OP,
                    SlangTypes.BIT_XOR_OP,
                    SlangTypes.BIT_OR_OP,
                    SlangTypes.INC_OP,
                    SlangTypes.DEC_OP,
                    SlangTypes.ADD_ASSIGN_OP,
                    SlangTypes.SUB_ASSIGN_OP,
                    SlangTypes.MUL_ASSIGN_OP,
                    SlangTypes.DIV_ASSIGN_OP,
                    SlangTypes.MOD_ASSIGN_OP,
                    SlangTypes.SHL_ASSIGN_OP,
                    SlangTypes.SHR_ASSIGN_OP,
                    SlangTypes.AND_ASSIGN_OP,
                    SlangTypes.OR_ASSIGN_OP,
                    SlangTypes.XOR_ASSIGN_OP,
                    SlangTypes.COMMA,
                    SlangTypes.ASSIGN_OP
                )
            ) {
                builder.advanceLexer()
            }
            else if (consumeToken(builder, SlangTypes.LEFT_PAREN))
                result = consumeToken(builder, SlangTypes.RIGHT_PAREN)
            else if (consumeToken(builder, SlangTypes.QUESTION_MARK))
                result = consumeToken(builder, SlangTypes.COLON)
            else
                result = false

            if (!result)
                builder.error("Invalid operator")
        }
        else if (consumeToken(builder, SlangTypes.IDENTIFIER))
            result = true

        exit_section_(builder, marker, SlangTypes.DECLARATION_NAME, result)
        return result
    }

    private fun parseTraditionalFuncDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseTraditionalFuncDecl"))
            return false

        val parseInner: (PsiBuilder, Int) -> Boolean = { b, l ->
            // HACK: The return type of the function will already have been
            // parsed in a scope that didn't include the function's generic
            // parameters.
            //
            // We will use a visitor here to try and replace the scope associated
            // with any name expressiosn in the reuslt type.
            //
            // TODO: This should be fixed by not associating scopes with
            // such expressions at parse time, and instead pushing down scopes
            // as part of the state during semantic checking.
            //

            var result = parseParameterList(b, l)

            if (result)
                consumeToken(b, "throws")

            result = result && parseOptSemantics(b, l)
            result = result && maybeParseGenericConstraints(b, l)
            result = result && parseOptBody(b, l)

            result
        }

        return parseOptGenericDecl(builder, level, parseInner)
    }

    private fun parseParameterList(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseParameterList"))
            return false

        if (!consumeToken(builder, SlangTypes.LEFT_PAREN))
            return false

        // Allow a declaration to use the keyword `void` for a parameter list,
        // since that was required in ancient C, and continues to be supported
        // in a bunch of its derivatives even if it is a Bad Design Choice
        //
        if (nextTokenIs(builder, "void") && builder.lookAhead(1) == SlangTypes.RIGHT_PAREN) {
            builder.advanceLexer()
            builder.advanceLexer()
            return true
        }

        var result = true
        while (result) {
            if (consumeToken(builder, SlangTypes.RIGHT_PAREN))
                break

            result = parseParameter(builder, level)
            if (result && consumeToken(builder, SlangTypes.RIGHT_PAREN))
                break
            result = result && consumeToken(builder, SlangTypes.COMMA)
        }

        return result
    }

    private fun parseOptBody(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseBody"))
            return false

        if (consumeToken(builder, SlangTypes.SEMICOLON))
            return true
        return parseBlockStatement(builder, level)
    }

    private fun parseBlockStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseBlockStatement"))
            return false

        if (!nextTokenIs(builder, SlangTypes.LEFT_BRACE))
            return false

        val marker = enter_section_(builder)
        builder.advanceLexer()
        var result = true

        while (result) {
            if (consumeToken(builder, SlangTypes.RIGHT_BRACE))
                break

            result = if (nextTokenAfterModifiersIs(builder, "struct"))
                parseDecl(builder, level + 1)
            else if (consumeToken(builder, "typedef"))
                parseTypeDef(builder, level + 1)
            else if (consumeToken(builder, "typealias"))
                parseTypeAliasDecl(builder, level + 1)
            else
                parseStatement(builder, level + 1)

            // TODO: handle recovery
        }

        exit_section_(builder, marker, SlangTypes.BLOCK_STATEMENT, result)
        return result
    }

    private fun parseParameter(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseParameter"))
            return false

        val marker = enter_section_(builder)
        var result = parseModifiers(builder, level + 1)
        result = result && parseTraditionalParamDeclCommonBase(builder, level + 1, true)
        exit_section_(builder, marker, SlangTypes.PARAMETER_DECLARATION, result)
        return result
    }

    private fun parseTraditionalParamDeclCommonBase(builder: PsiBuilder, level: Int, allowEmpty: Boolean = false): Boolean {
        if (!recursion_guard_(builder, level, "parseTraditionalParamDeclCommon"))
            return false

        var result = parseType(builder, level)
        val marker = enter_section_(builder)
        result = result && (parseInitDeclarator(builder, level + 1, allowEmpty) != null)
        exit_section_(builder, marker, SlangTypes.VARIABLE_DECL, result)
        return result
    }

    private fun parseTypeDef(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5737
    }

    private fun parseTypeAliasDecl(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5743
    }

    private fun parseStatement(builder: PsiBuilder, level: Int, isIfStmt: Boolean = false): Boolean {
        if (!recursion_guard_(builder, level, "parseStatement"))
            return false

        val marker = enter_section_(builder)

        val currentOffset = builder.currentOffset
        var result = parseModifiers(builder, level + 1)
        val hadModifiers = currentOffset < builder.currentOffset

        if (nextTokenIs(builder, SlangTypes.LEFT_BRACE))
            result = result && parseDeclBody(builder, level + 1)
        else if (nextTokenIs(builder, "if")) {
            result = if (nextTokenAheadIs(builder, "let", 2))
                result && parseIfLetStatement(builder, level + 1)
            else
                result && parseIfStatement(builder, level + 1)
        }
        else if (nextTokenIs(builder, "for"))
            result = result && parseForStatement(builder, level + 1)
        else if (nextTokenIs(builder, "while"))
            result = result && parseWhileStatement(builder, level + 1)
        else if (nextTokenIs(builder, "do"))
            result = result && parseDoWhileStatement(builder, level + 1)
        else if (nextTokenIs(builder, "break"))
            result = result && parseBreakStatement(builder, level + 1)
        else if (nextTokenIs(builder, "continue"))
            result = result && parseContinueStatement(builder, level + 1)
        else if (nextTokenIs(builder, "return"))
            result = result && parseReturnStatement(builder, level + 1)
        else if (nextTokenIs(builder, "discard"))
            result = result && parseDiscardStatement(builder, level + 1)
        else if (nextTokenIs(builder, "switch"))
            result = result && parseSwitchStmt(builder, level + 1)
        else if (nextTokenIs(builder, "__target_switch"))
            result = result && parseTargetSwitchStmt(builder, level + 1)
        else if (nextTokenIs(builder, "__intrisic_asm"))
            result = result && parseIntrisicAsmStmt(builder, level + 1)
        else if (nextTokenIs(builder, "case"))
            result = result && parseCaseStmt(builder, level + 1)
        else if (nextTokenIs(builder, "default"))
            result = result && parseDefaultStmt(builder, level + 1)
        else if (nextTokenIs(builder, "__GPU_FOREACH"))
            result = result && parseGpuForeachStmt(builder, level + 1)
        else if (nextTokenIs(builder, "__intrisic_asm"))
            result = result && parseIntrisicAsmStmt(builder, level + 1)
        else if (nextTokenIs(builder, SlangTypes.DOLLAR))
            result = result && parseCompileTimeStmt(builder, level + 1)
        else if (nextTokenIs(builder, "try"))
            result = result && parseExpressionStatement(builder, level + 1)
        else if (nextTokenIs(builder, null, SlangTypes.IDENTIFIER, SlangTypes.SCOPE)) {
            result = if (nextTokenIs(builder, SlangTypes.IDENTIFIER) && builder.lookAhead(1) == SlangTypes.COLON)
            // An identifier followed by an ":" is a label.
                result && parseLabelStatement(builder, level + 1)
            else {
                result && parseDisambiguateVarDeclOrExpression(builder, level + 1, hadModifiers)
            }
        }
        else if (nextTokenIs(builder, SlangTypes.SEMICOLON)) {
            builder.advanceLexer()
            if (isIfStmt) {
                // An empty statement after an `if` is probably a mistake,
                // so we will diagnose it as such.
                //
                builder.error("Unintended empty statement")
                result = false
            }
        }
        else {
            result = result && parseExpressionStatement(builder, level + 1)
        }

        exit_section_(builder, marker, SlangTypes.STATEMENT, result)
        return result
    }

    private fun parseIfStatementCommon(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIfStatementCommon"))
            return false

        var result = consumeToken(builder, SlangTypes.RIGHT_PAREN)

        result = result && parseStatement(builder, level, true)

        if (result && consumeToken(builder, "else")) {
            result = parseStatement(builder, level, true)
        }

        return result
    }

    private fun parseIfLetStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIfStatement"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "if")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)
        if (result) {
            val declMarker = enter_section_(builder)
            result = consumeToken(builder, "let")
            if (result) {
                val varDeclMarker = enter_section_(builder)
                result = consumeToken(builder, SlangTypes.IDENTIFIER)
                result = result && consumeToken(builder, SlangTypes.ASSIGN_OP)
                result = result && parseInitExpr(builder, level + 3)
                exit_section_(builder, varDeclMarker, SlangTypes.VARIABLE_DECL, result)
            }
            exit_section_(builder, declMarker, SlangTypes.LET_DECLARATION, result)
        }
        result = result && parseIfStatementCommon(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.IF_STATEMENT, result)
        return result
    }

    private fun parseIfStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIfStatement"))
            return false

        val marker = enter_section_(builder)

        var result = consumeToken(builder, "if")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && parseIfStatementCommon(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.IF_STATEMENT, result)
        return result
    }

    private fun parseForStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseForStatement"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "for")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)

        let {
            val initialMarker = enter_section_(builder)

            val currentOffset = builder.currentOffset
            result = result && parseModifiers(builder, level + 2)
            val hadModifiers = currentOffset < builder.currentOffset

            if (result && !nextTokenIs(builder, SlangTypes.SEMICOLON)) {
                result = parseDisambiguateVarDeclOrExpression(builder, level + 2, hadModifiers)
            }
            result = result && consumeToken(builder, SlangTypes.SEMICOLON)

            exit_section_(builder, initialMarker, SlangTypes.INITIAL_STATEMENT, result)
        }

        let {
            val predicateMarker = enter_section_(builder)

            if (result && !nextTokenIs(builder, SlangTypes.SEMICOLON)) {
                result = parseExpression(builder, level + 2)
            }
            result = result && consumeToken(builder, SlangTypes.SEMICOLON)

            exit_section_(builder, predicateMarker, SlangTypes.PREDICATE_EXPRESSION, result)
        }

        let {
            val sideEffectMarker = enter_section_(builder)

            if (result && !nextTokenIs(builder, SlangTypes.RIGHT_PAREN)) {
                result = parseExpression(builder, level + 2)
            }

            exit_section_(builder, sideEffectMarker, SlangTypes.SIDE_EFFECT_EXPRESSION, result)
        }

        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        result = result && parseStatement(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.FOR_STATEMENT, result)
        return result
    }

    private fun parseWhileStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseWhileStatement"))
            return false

        val marker = enter_section_(builder)

        var result = consumeToken(builder, "while")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        result = result && parseStatement(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.WHILE_STATEMENT, result)
        return result
    }

    private fun parseDoWhileStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDoWhileStatement"))
            return false

        val marker = enter_section_(builder)

        var result = consumeToken(builder, "do")
        result = result && parseStatement(builder, level + 1)
        result = result && consumeToken(builder, "while")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, SlangTypes.DO_WHILE_STATEMENT, result)
        return result
    }

    private fun parseBreakStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseBreakStatement"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "break")
        if (result && nextTokenIs(builder, SlangTypes.IDENTIFIER))
            result = consumeToken(builder, SlangTypes.IDENTIFIER)
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)
        exit_section_(builder, marker, SlangTypes.BREAK_STATEMENT, result)
        return result
    }

    private fun parseContinueStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseContinueStatement"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "continue")
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)
        exit_section_(builder, marker, SlangTypes.CONTINUE_STATEMENT, result)
        return result
    }

    private fun parseReturnStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseReturnStatement"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "return")
        if (result && !nextTokenIs(builder, SlangTypes.SEMICOLON))
            result = parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)
        exit_section_(builder, marker, SlangTypes.RETURN_STATEMENT, result)
        return result
    }

    private fun parseDiscardStatement(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5502
    }

    private fun parseSwitchStmt(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5509
    }

    private fun parseTargetSwitchStmt(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5511
    }

    private fun parseIntrisicAsmStmt(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5513
    }

    private fun parseCaseStmt(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5515
    }

    private fun parseDefaultStmt(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5517
    }

    private fun parseGpuForeachStmt(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5519
    }

    private fun parseCompileTimeStmt(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5522
    }

    private fun parseExpressionStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseExpressionStatement"))
            return false

        val marker = enter_section_(builder)
        var result = parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)
        exit_section_(builder, marker, SlangTypes.EXPRESSION_STATEMENT, result)
        return result
    }

    private fun parseLabelStatement(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:6025
    }

    private fun parseVarDeclStatement(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5779
    }

    private fun parseEnumCaseDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseEnumCaseDecl"))
            return false

        val marker = enter_section_(builder)
        var result = let {
            val nameMarker = enter_section_(builder)
            val nameResult = consumeToken(builder, SlangTypes.IDENTIFIER)
            exit_section_(builder, nameMarker, SlangTypes.ENUM_CASE_NAME, nameResult)
            nameResult
        }
        if (result && consumeToken(builder, SlangTypes.ASSIGN_OP)) {
            result = parseArgExpr(builder, level)
        }
        exit_section_(builder, marker, SlangTypes.ENUM_CASE_DECLARATION, result)
        return result
    }

    private fun parseDisambiguateVarDeclOrExpression(builder: PsiBuilder, level: Int, hadModifiers: Boolean): Boolean {
        if (!recursion_guard_(builder, level, "parseDisambiguateVarDeclOrExpression"))
            return false

        // Easy case: we have some modifiers preceding this, it only happens for variable declarations, not expressions
        //
        if (hadModifiers)
            return parseVarDeclStatement(builder, level)

        // We might be looking at a local declaration, or an
        // expression statement, and we need to figure out which.
        //
        // We'll solve this with backtracking for now.
        //
        val backtrackingMarker = builder.mark()
        // TODO: Investigate 'hasSeenCompletionToken' usage

        // Try to parse a type (knowing that the type grammar is
        // a subset of the expression grammar, and so this should
        // always succeed).
        //
        // HACK: The type grammar that `ParseType` supports is *not*
        // a subset of the expression grammar because it includes
        // type specifiers like `struct` and `enum` declarations
        // which should always be the start of a declaration.
        //
        // Before launching into this attempt to parse a type,
        // this logic should really be looking up the `SyntaxDecl`,
        // if any, associated with the identifier. If a piece of
        // syntax is discovered, then it should dictate the next
        // steps of parsing, and only in the case where the lookahead
        // isn't a keyword should we fall back to the approach
        // here.
        //
        parseType(builder, level)

        // If the next token after we parsed a type looks like
        // we are going to declare a variable, then lets guess
        // that this is a declaration.
        //
        // TODO(tfoley): this wouldn't be robust for more
        // general kinds of declarators (notably pointer declarators),
        // so we'll need to be careful about this.
        //
        // If the line being parsed token is `Something* ...`, then the `*`
        // is already consumed by `ParseType` above and the current logic
        // will continue to parse as var declaration instead of a mul expr.
        // In this context it makes sense to disambiguate
        // in favor of a pointer over a multiply, since a multiply
        // expression can't appear at the start of a statement
        // with any side effects.
        //
        if (nextTokenIs(builder, null, SlangTypes.IDENTIFIER, SlangTypes.COMPLETION_REQUEST)) {
            // Reset the cursor and try to parse a declaration now.
            // Note: the declaration will consume any modifiers
            // that had been in place on the statement.
            backtrackingMarker.rollbackTo()
            return parseVarDeclStatement(builder, level + 1)
        }
        else {
            // Fallback: reset and parse an expression
            backtrackingMarker.rollbackTo()
            return parseExpressionStatement(builder, level + 1)
        }
    }

    private fun parseExpandExpr(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:7820
    }

    private fun parseEachExpr(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:7824
    }

    private fun parseGenericDeclImpl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGenericDecl"))
            return false

        val wasInVariadicGenerics = isInVariadicGenerics

        val marker = enter_section_(builder)

        var result = consumeToken(builder, SlangTypes.LESS_OP)

        while (result) {
            if (nextTokenIs(builder, SlangTypes.GREATER_OP))
                break

            val (resultDecl, isTypePack) = parseGenericParamDecl(builder, level + 1)
            result = resultDecl

            if (isTypePack)
                isInVariadicGenerics = true

            if (result && nextTokenIs(builder, SlangTypes.GREATER_OP))
                break
            if (result && !consumeToken(builder, SlangTypes.COMMA))
                break
        }

        exit_section_(builder, marker, SlangTypes.GENERIC_DECLARATION, result)
        isInVariadicGenerics = wasInVariadicGenerics

        return result
    }

    private fun parseGenericParamDecl(builder: PsiBuilder, level: Int): Pair<Boolean, Boolean> {
        if (!recursion_guard_(builder, level, "parseGenericParamDecl"))
            return Pair(false, false)

        // simple syntax to introduce a value parameter
        //
        if (consumeToken(builder, "let")) {
            val marker = enter_section_(builder)
            var result = true
            if (consumeToken(builder, SlangTypes.COLON))
                result = parseTypeExp(builder, level + 1)
            if (result && consumeToken(builder, SlangTypes.ASSIGN_OP))
                result = parseInitExpr(builder, level + 1)
            exit_section_(builder, marker, SlangTypes.GENERIC_PARAMETER_DECLARATION, result)
            return Pair(result, false)
        }

        val marker = enter_section_(builder)
        val type: IElementType

        var result = true
        if (consumeToken(builder, "each")) {
            type = SlangTypes.GENERIC_TYPE_PACK_PARAMETER_DECLARATION
            result = consumeToken(builder, SlangTypes.IDENTIFIER)
        }
        else {
            // Disambiguate between a type parameter and a value parameter.
            // If next token is "typename", then it is a type parameter.
            if (consumeToken(builder, "typename"))
                type = SlangTypes.GENERIC_TYPE_PARAMETER_DECLARATION
            else {
                // Otherwise, if the next token is an identifier, followed by a colon, comma, '=' or
                // '>', then it is a type parameter.
                type = if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
                    when (builder.lookAhead(1)) {
                        SlangTypes.COLON, SlangTypes.COMMA, SlangTypes.GREATER_OP, SlangTypes.ASSIGN_OP
                            -> SlangTypes.GENERIC_TYPE_PARAMETER_DECLARATION

                        else -> SlangTypes.GENERIC_VALUE_PARAMETER_DECLARATION
                    }
                } else
                    SlangTypes.GENERIC_VALUE_PARAMETER_DECLARATION

                if (type == SlangTypes.GENERIC_TYPE_PARAMETER_DECLARATION)
                    result = consumeToken(builder, SlangTypes.IDENTIFIER)
                else {
                    result = parseTypeExp(builder, level + 1)
                    result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
                    if (result && consumeToken(builder, SlangTypes.ASSIGN_OP)) {
                        result = parseInitExpr(builder, level + 1)
                    }
                }
            }
        }

        if (result && (type != SlangTypes.GENERIC_VALUE_PARAMETER_DECLARATION) && consumeToken(builder, SlangTypes.COLON)) {
            // The user is applying a constraint to this type parameter...

            val constraintMarker = enter_section_(builder)
            result = parseTypeExp(builder, level + 2)
            exit_section_(builder, constraintMarker, SlangTypes.GENERIC_TYPE_CONSTRAINT_DECLARATION, result)
        }

        if (type == SlangTypes.GENERIC_TYPE_PARAMETER_DECLARATION) {
            if (result && consumeToken(builder, SlangTypes.ASSIGN_OP))
                result = parseTypeExp(builder, level + 1)
        }

        exit_section_(builder, marker, type, result)
        return Pair(result, type == SlangTypes.GENERIC_TYPE_PACK_PARAMETER_DECLARATION)
    }

    private fun parseSpirVAsmInst(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see l7579
    }
}