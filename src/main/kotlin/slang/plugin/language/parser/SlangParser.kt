package slang.plugin.language.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.LightPsiParser
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.lang.parser.GeneratedParserUtilBase.*
import com.intellij.psi.tree.IElementType
import slang.plugin.language.parser.data.TypeSpec
import slang.plugin.psi.types.SlangTypes
import slang.plugin.psi.SlangOldTypes.*
import slang.plugin.psi.SlangPsiUtil

open class SlangParser: PsiParser, LightPsiParser {

    private val enableGlslCode = true

    override fun parse(type: IElementType, builder: PsiBuilder): ASTNode {
        parseLight(type, builder)
        return builder.treeBuilt
    }

    override fun parseLight(type: IElementType, baseBuilder: PsiBuilder) {

        val builder = adapt_builder_(type, baseBuilder, this, null)
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
            when (SlangPsiUtil.nextToken(builder)) {
                IDENTIFIER -> {
                    if (tryParseUsingSyntaxDecl(builder, level))
                        continue
                    else if (consumeToken(builder, "no_diff"))
                        continue
                    else if (enableGlslCode)
                        if (consumeToken(builder, "flat"))
                            continue
                    return false
                }
                LEFT_BRACKET -> {
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

        when (SlangPsiUtil.nextToken(builder)) {
            IDENTIFIER -> return false // TODO: see slang/slang-parser.cpp:4706

            // It is valid in HLSL/GLSL to have an "empty" declaration
            // that consists of just a semicolon. In particular, this
            // gets used a lot in GLSL to attach custom semantics to
            // shader input or output.
            SEMICOLON -> {
                builder.advanceLexer()
            }

            LEFT_BRACE, LEFT_PAREN -> {
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

    private fun tryParseUsingSyntaxDecl(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO :see slang/slang-parser.cpp:1183
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

        if (consumeToken(builder, SEMICOLON)) {
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
            when (SlangPsiUtil.nextToken(builder)) {
                COMMA -> {
                    val markerB = enter_section_(builder)
                    result = parseInitDeclarator(builder, level + 1)
                    exit_section_(builder, markerB, SlangTypes.VARIABLE_DECL, result)
                }
                else -> break
            }
        }

        result = result && consumeToken(builder, SEMICOLON)

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
            when (SlangPsiUtil.nextToken(builder)) {
                LEFT_BRACKET -> {
                    val marker = enter_section_(builder)
                    var result = consumeToken(builder, LEFT_BRACKET)

                    if (!nextTokenIs(builder, RIGHT_BRACKET)) {
                        result = result && parseExpression(builder, level + 1)
                    }
                    result = result && consumeToken(builder, RIGHT_BRACKET)

                    exit_section_(builder, marker, SlangTypes.ARRAY_SPECIFIER, result)

                    if (!result)
                        return false
                }
                else -> break;
            }
        }

        return true
    }

    private fun parseExpression(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseExpression"))
            return false

        return false // TODO: see slang/slang-parser.cpp:2371
    }

    private fun parseInitDeclarator(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseInitDeclarator"))
            return false

        val marker = enter_section_(builder)
        var result = parseSemanticDeclarator(builder, level + 1)
        if (consumeToken(builder, ASSIGN)) {
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

        if (consumeToken(builder, MUL_OP)) {
            val marker = enter_section_(builder)
            val result = parseDeclarator(builder, level + 1);
            exit_section_(builder, marker, SlangTypes.POINTER_DECLARATOR, result)
            return result
        }

        val result = parseDirectAbstractDeclarator(builder, level)
        return result
    }

    private fun parseOptSemantics(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:2098
    }

    private fun parseDirectAbstractDeclarator(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDirectAbstractDeclarator"))
            return false

        var result = true

        when (SlangPsiUtil.nextToken(builder)) {
            IDENTIFIER -> {
                val marker = enter_section_(builder)
                result = consumeToken(builder, IDENTIFIER)
                exit_section_(builder, marker, SlangTypes.NAME_DECLARATOR, result)
            }
            LEFT_PAREN -> {
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
                result = consumeToken(builder, LEFT_PAREN)
                result = result && parseDeclarator(builder, level)
                result = result && consumeToken(builder, RIGHT_PAREN)
            }
            else -> return false
        }

        // Postfix additions
        while (result) {
            when (SlangPsiUtil.nextToken(builder)) {
                LEFT_BRACKET -> {
                    val marker = enter_section_(builder, level, _LEFT_, SlangTypes.ARRAY_DECLARATOR, null)
                    result = consumeToken(builder, LEFT_BRACKET)
                    if (!nextTokenIs(builder, RIGHT_BRACKET))
                        result = result && parseExpression(builder, level + 1)
                    result = result && consumeToken(builder, RIGHT_BRACKET)
                    exit_section_(builder, level, marker, result, false, null)
                    break
                }
                LEFT_PAREN -> break
                LESS_OP -> break // TODO: see slang/slang-parser.cpp:2011
                else -> break
            }
        }

        return result
    }

    private fun parseInitExpr(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:2108
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
        consumeToken(builder, SCOPE)

        var result = consumeToken(builder, IDENTIFIER)

        typeSpec.expr = true

        while (result) {
            when (SlangPsiUtil.nextToken(builder)) {
                LESS_OP -> {
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

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "struct")

        if (nextTokenIs(builder, LEFT_BRACKET)) {
            result = result && parseSquareBracketAttributes(builder, level + 1)
        }

        consumeToken(builder, COMPLETION_REQUEST)

        if (nextTokenIs(builder, IDENTIFIER)) {
            val nameMarker = enter_section_(builder, level + 1, _NONE_)
            result = result && consumeToken(builder, IDENTIFIER)
            exit_section_(builder, level + 1, nameMarker, SlangTypes.STRUCT_NAME, result, false, null)
        }

        val callback: (PsiBuilder, Int) -> Boolean = { b, l ->
            if (!recursion_guard_(b, l, "parseStructCallback"))
                false
            else {
                var callbackResult = parseOptionalInheritanceClause(b, l)
                if (consumeToken(builder, ASSIGN)) {
                    callbackResult = callbackResult && parseTypeExp(b, l)
                    callbackResult = callbackResult && consumeToken(builder, SEMICOLON)
                    callbackResult
                }
                else if (consumeToken(builder, SEMICOLON)) {
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

        exit_section_(builder, marker, SlangTypes.STRUCT_DECLARATION, result)

        return result
    }

    private fun parseClass(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5018
    }

    private fun parseEnumDecl(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:5067
    }

    private fun parsePrefixExpr(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:7781
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
        if (nextTokenIs(builder, LESS_OP)) {
            return false // TODO: see slang/slang-parser.cpp:1639
        }
        return parseInner(builder, level)
    }

    private fun parseOptionalInheritanceClause(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:3406
    }

    private fun parseTypeExp(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:6119
    }

    private fun maybeParseGenericConstraints(builder: PsiBuilder, level: Int): Boolean {
        return false // TODO: see slang/slang-parser.cpp:1654
    }

    private fun parseDeclBody(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDeclBody"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, LEFT_BRACE)
        while (result)
        {
            when (SlangPsiUtil.nextToken(builder)) {
                RIGHT_BRACKET -> {
                    result = consumeToken(builder, RIGHT_BRACKET)
                    break
                }
                else -> result = parseDecl(builder, level + 1)
            }
        }

        exit_section_(builder, marker, SlangTypes.BODY_DECL, result)
        return result
    }

}