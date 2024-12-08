package slang.plugin.language.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.LightPsiParser
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.lang.parser.GeneratedParserUtilBase.*
import com.intellij.psi.tree.IElementType
import slang.plugin.language.parser.data.TypeSpec
import slang.plugin.psi.types.SlangTypes
import slang.plugin.psi.SlangPsiUtil

open class SlangParser: PsiParser, LightPsiParser {

    private val enableGlslCode = true
    private var isInVariadicGenerics = false
    private var genericDepth = 0
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
        exit_section_(builder, marker, null, result)
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
                // However recovery logic may lead us here. In this case we just
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

    private fun parseSquareBracketAttributes(builder: PsiBuilder, level: Int): Boolean
    {
        return false // TODO: see slang/slang-parser.cpp:1226
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
        // same line as the closing `}`, and if not, we
        // will treat it like the end of the declaration.
        //
        if (typeSpec?.decl == true)
        {
            // TODO: see slang/slang-parser.cpp:2891
//            if (builder.eof() || SlangPsiUtil.atStartOfLine(builder)) {
//                // The token after the `}` is at the start of its
//                // own line, which means it can't be on the same line.
//                //
//                // This means the programmer probably wants to
//                // just treat this as a declaration.
//                return result
//            }
        }

        val marker = enter_section_(builder)
        result = result && parseInitDeclarator(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.VARIABLE_DECL, result)

        // TODO: see slang/slang-parser.cpp:2916

        while (result) {
            when (builder.tokenType) {
                SlangTypes.COMMA -> {
                    val markerB = enter_section_(builder)
                    result = parseInitDeclarator(builder, level + 1)
                    exit_section_(builder, markerB, SlangTypes.VARIABLE_DECL, result)
                }
                else -> break
            }
        }

        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

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
                else -> break;
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
        Prefix,
        Postfix,
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

    private fun parseInitDeclarator(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseInitDeclarator"))
            return false

        val marker = enter_section_(builder)
        var result = parseSemanticDeclarator(builder, level + 1)
        if (consumeToken(builder, SlangTypes.ASSIGN_OP)) {
            result = result && parseInitExpr(builder, level + 1)
        }
        exit_section_(builder, marker, SlangTypes.INIT_DECLARATOR, result)
        return result;
    }

    private fun parseSemanticDeclarator(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseSemanticDeclarator"))
            return false

        var result = parseDeclarator(builder, level)
        result = result && parseOptSemantics(builder, level)
        return result
    }

    private fun parseDeclarator(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDeclarator"))
            return false

        if (consumeToken(builder, SlangTypes.MUL_OP)) {
            val marker = enter_section_(builder)
            val result = parseDeclarator(builder, level + 1);
            exit_section_(builder, marker, SlangTypes.POINTER_DECLARATOR, result)
            return result
        }

        val result = parseDirectAbstractDeclarator(builder, level)
        return result
    }

    private fun parseOptSemantics(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseOptSemantics"))
            return false

        if (!consumeToken(builder, SlangTypes.COLON))
            return true

        var result = true
        while (result) {
            result = parseSemantic(builder, level)

            // If we see a '<', ignore the remaining.
            if (nextTokenIs(builder, SlangTypes.LESS_OP))
            {
                builder.advanceLexer();
                while (true) {
                    if (builder.eof()) {
                        break
                    } else if (nextTokenIs(builder, SlangTypes.GREATER_OP)) {
                        builder.advanceLexer();
                        break;
                    } else {
                        builder.advanceLexer();
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
            // However, that is an uncommon occurence, and trying
            // to continue parsing semantics here even if we didn't
            // see a colon forces us to be careful about
            // avoiding an infinite loop here.
            if (!consumeToken(builder, SlangTypes.COLON)) {
                return result
            }
        }
        return false
    }

    private fun parseDirectAbstractDeclarator(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDirectAbstractDeclarator"))
            return false

        var result = true

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
                // Or we could be looking at the use of parenthesese in an ordinary
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
                result = result && parseDeclarator(builder, level)
                result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
            }
            else -> return false
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
        if (nextTokenIs(builder, "functype")) {
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
        return false // TODO: see slang/slang-parser.cpp:5018
    }

    private fun parseEnumDecl(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5067
    }

    private fun parsePrefixExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parsePrefixExpr"))
            return false

        if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            if (consumeToken(builder, "new")) {
                // TODO: see slang/slang-parser.cpp:7791
                return false
            }
            else if (consumeToken(builder, "spirv_asm")) {
                return parseSpirVAsmExpr(builder, level)
            }
            else if (isInVariadicGenerics)
            {
                // TODO: see slang/slang-parser.cpp:7817
                return false
            }
            return parsePostFixExpr(builder, level)
        }

        // TODO: see slang/slang-parser.cpp:7834

        return parsePostFixExpr(builder, level)
    }

    private fun parseFuncTypeExpr(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:2493
    }

    private fun parseGenericApp(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:2240
    }

    private fun parseStaticMemberType(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:2344
    }

    private fun parseMemberType(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:2333
    }

    private fun parseOptGenericDecl(builder: PsiBuilder, level: Int, parseInner: (PsiBuilder, Int) -> Boolean): Boolean {
        if (nextTokenIs(builder, SlangTypes.LESS_OP)) {
            return false // TODO: see slang/slang-parser.cpp:1639
        }
        return parseInner(builder, level)
    }

    private fun parseOptionalInheritanceClause(builder: PsiBuilder, level: Int): Boolean {
        return true // TODO: see slang/slang-parser.cpp:3406
    }

    private fun parseTypeExp(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:6119
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
        if (precedence == Precedence.Assignment)
            return Associativity.Right
        else
            return Associativity.Left
    }

    private fun parseInfixExprWithPrecedence(builder: PsiBuilder, level: Int, precedence: Precedence): Boolean {
        if (!recursion_guard_(builder, level, "parseInfixExpression"))
            return false

        var result = true;
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
        return false // TODO: see slang/slang-parser.cpp:7813
    }

    private fun parsePostFixExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parsePostFixExpr"))
            return false

        var result = parseAtomicExpr(builder, level)

        if (nextTokenIs(builder, SlangTypes.INC_OP) || nextTokenIs(builder, SlangTypes.DEC_OP)) {
            val marker = enter_section_(builder, level, _LEFT_)
            result = result && parseOperator(builder, level + 1)
            exit_section_(builder, level, marker, SlangTypes.POSTFIX_EXPRESSION, result, false, null)
        }
        else
        {
            // TODO: see slang/slang-parser.cpp:7277
            result = false
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
        // TODO: see slang/slang-parser.cpp:6919
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
}