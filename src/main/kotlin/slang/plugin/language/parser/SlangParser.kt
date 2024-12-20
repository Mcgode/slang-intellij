package slang.plugin.language.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.LightPsiParser
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.lang.parser.GeneratedParserUtilBase.*
import com.intellij.psi.tree.IElementType
import org.intellij.markdown.lexer.pop
import org.intellij.markdown.lexer.push
import slang.plugin.language.parser.data.*
import slang.plugin.psi.SlangElementType
import slang.plugin.psi.SlangIFileElementType
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
    private var scopes = ArrayList<Scope>()
    private var scopeStack = ArrayList<Scope>()
    private val scope: Scope?
        get() = if (scopeStack.size > 0) scopeStack.last() else null

    @Suppress("LeakingThis")
    private val builtinSyntaxDeclarations = arrayListOf<SyntaxDeclaration>(
        // !!!!!!!!!!!!!!!!!!!! Decls !!!!!!!!!!!!!!!!!!

        makeParseDeclaration("typedef", this::parseTypeDef),
        makeParseDeclaration("associatedtype", this::parseAssocType),
        makeParseDeclaration("type_param", this::parseGlobalGenericTypeParamDecl),
        makeParseDeclaration("cbuffer", this::parseHLSLCBufferDecl),
        makeParseDeclaration("tbuffer", this::parseHLSLTBufferDecl),
        makeParseDeclaration("__generic", this::parseGenericDecl),
        makeParseDeclaration("__extension", this::parseExtensionDecl),
        makeParseDeclaration("extension", this::parseExtensionDecl),
        makeParseDeclaration("__init", this::parseConstructorDecl),
        makeParseDeclaration("__subscript", this::parseSubscriptDecl),
        makeParseDeclaration("property", this::parsePropertyDecl),
        makeParseDeclaration("interface", this::parseInterfaceDecl),
        makeParseDeclaration("syntax", this::parseSyntaxDecl),
        makeParseDeclaration("attribute_syntax", this::parseAttributeSyntaxDecl),
        makeParseDeclaration("__import", this::parseImportDecl),
        makeParseDeclaration("import", this::parseImportDecl),
        makeParseDeclaration("__include", this::parseIncludeDecl),
        makeParseDeclaration("module", this::parseModuleDeclarationDecl),
        makeParseDeclaration("implementing", this::parseImplementingDecl),
        makeParseDeclaration("let", this::parseLetDecl),
        makeParseDeclaration("var", this::parseVarDecl),
        makeParseDeclaration("func", this::parseFuncDecl),
        makeParseDeclaration("typealias", this::parseTypeAliasDecl),
        makeParseDeclaration("__generic_value_param", this::parseGlobalGenericValueParamDecl),
        makeParseDeclaration("namespace", this::parseNamespaceDecl),
        makeParseDeclaration("using", this::parseUsingDecl),
        makeParseDeclaration("__ignored_block", this::parseIgnoredBlockDecl),
        makeParseDeclaration("__transparent_block", this::parseTransparentBlockDecl),
        makeParseDeclaration("__file_decl", this::parseFileDecl),
        makeParseDeclaration("__require_capability", this::parseRequireCapabilityDecl),

        // !!!!!!!!!!!!!!!!!!!!!! Modifier !!!!!!!!!!!!!!!!!!!!!!

        // Add syntax for "simple" modifier keywords.
        // These are the ones that just appear as a single
        // keyword (no further tokens expected/allowed),
        // and which can be represented just by creating
        // a new AST node of the corresponding type.
        makeParseModifier("in", SlangTypes.IN_MODIFIER),
        makeParseModifier("out", SlangTypes.OUT_MODIFIER),
        makeParseModifier("inout", SlangTypes.INOUT_MODIFIER),
        makeParseModifier("__ref", SlangTypes.REF_MODIFIER),
        makeParseModifier("__constref", SlangTypes.CONSTREF_MODIFIER),
        makeParseModifier("const", SlangTypes.CONST_MODIFIER),
        makeParseModifier("__builtin", SlangTypes.BUILTIN_MODIFIER),
        makeParseModifier("highp", SlangTypes.GLSL_PRECISION_MODIFIER),
        makeParseModifier("lowp", SlangTypes.GLSL_PRECISION_MODIFIER),
        makeParseModifier("mediump", SlangTypes.GLSL_PRECISION_MODIFIER),

        makeParseModifier("__global", SlangTypes.ACTUAL_GLOBAL_MODIFIER),

        makeParseModifier("inline", SlangTypes.INLINE_MODIFIER),
        makeParseModifier("public", SlangTypes.PUBLIC_MODIFIER),
        makeParseModifier("private", SlangTypes.PRIVATE_MODIFIER),
        makeParseModifier("internal", SlangTypes.INTERNAL_MODIFIER),

        makeParseModifier("require", SlangTypes.REQUIRE_MODIFIER),
        makeParseModifier("param", SlangTypes.PARAM_MODIFIER),
        makeParseModifier("extern", SlangTypes.EXTERN_MODIFIER),

        makeParseModifier("row_major", SlangTypes.HLSL_ROW_MAJOR_LAYOUT_MODIFIER),
        makeParseModifier("column_major", SlangTypes.HLSL_COLUMN_MAJOR_LAYOUT_MODIFIER),

        makeParseModifier("nointerpolation", SlangTypes.HLSL_NO_INTERPOLATION_MODIFIER),
        makeParseModifier("noperspective", SlangTypes.HLSL_NO_PERSPECTIVE_MODIFIER),
        makeParseModifier("linear", SlangTypes.HLSL_LINEAR_MODIFIER),
        makeParseModifier("sample", SlangTypes.HLSL_SAMPLE_MODIFIER),
        makeParseModifier("centroid", SlangTypes.HLSL_CENTROID_MODIFIER),
        makeParseModifier("precise", SlangTypes.PRECISE_MODIFIER),
        makeParseModifier("shared", SlangTypes.HLSL_EFFECT_SHARED_MODIFIER),
        makeParseModifier("groupshared", SlangTypes.HLSL_GROUP_SHARED_MODIFIER),
        makeParseModifier("static", SlangTypes.HLSL_STATIC_MODIFIER),
        makeParseModifier("uniform", SlangTypes.HLSL_UNIFORM_MODIFIER),
        makeParseModifier("volatile", SlangTypes.VOLATILE_MODIFIER),
        makeParseModifier("coherent", SlangTypes.GLSL_COHERENT_MODIFIER),
        makeParseModifier("restrict", SlangTypes.GLSL_RESTRICT_MODIFIER),
        makeParseModifier("readonly", SlangTypes.GLSL_READ_ONLY_MODIFIER),
        makeParseModifier("writeonly", SlangTypes.GLSL_WRITE_ONLY_MODIFIER),
        makeParseModifier("export", SlangTypes.HLSL_EXPORT_MODIFIER),
        makeParseModifier("dynamic_uniform", SlangTypes.HLSL_DYNAMIC_UNIFORM_MODIFIER),

        // Modifiers for geometry shader input
        makeParseModifier("point", SlangTypes.HLSL_POINT_MODIFIER),
        makeParseModifier("line", SlangTypes.HLSL_LINE_MODIFIER),
        makeParseModifier("triangle", SlangTypes.HLSL_TRIANGLE_MODIFIER),
        makeParseModifier("lineadj", SlangTypes.HLSL_LINE_ADJ_MODIFIER),
        makeParseModifier("triangleadj", SlangTypes.HLSL_TRIANGLE_ADJ_MODIFIER),

        // Modifiers for mesh shader parameters
        makeParseModifier("vertices", SlangTypes.HLSL_VERTICES_MODIFIER),
        makeParseModifier("indices", SlangTypes.HLSL_INDICES_MODIFIER),
        makeParseModifier("primitives", SlangTypes.HLSL_PRIMITIVES_MODIFIER),
        makeParseModifier("payload", SlangTypes.HLSL_PAYLOAD_MODIFIER),

        // Modifiers for unary operator declarations
        makeParseModifier("__prefix", SlangTypes.PREFIX_MODIFIER),
        makeParseModifier("__postfix", SlangTypes.POSTFIX_MODIFIER),

        // Modifier to apply to `import` that should be re-exported
        makeParseModifier("__exported", SlangTypes.EXPORTED_MODIFIER),

        // Add syntax for more complex modifiers, which allow
        // or expect more tokens after the initial keyword.

        makeParseModifier("layout", this::parseLayoutModifier),
        makeParseModifier("hitAttributeEXT", SlangTypes.VULKAN_HIT_ATTRIBUTES_MODIFIER),
        makeParseModifier("__intrinsic_op", this::parseIntrinsicOpModifier),
        makeParseModifier("__target_intrinsic", this::parseTargetIntrinsicModifier),
        makeParseModifier("__specialized_for_target", this::parseSpecializedForTargetModifier),
        makeParseModifier("__glsl_extension", this::parseGLSLExtensionModifier),
        makeParseModifier("__glsl_version", this::parseGLSLVersionModifier),
        makeParseModifier("__spirv_version", this::parseSPIRVVersionModifier),
        makeParseModifier("__cuda_sm_version", this::parseCUDASMVersionModifier),

        makeParseModifier("__builtin_type", this::parseBuiltinTypeModifier),
        makeParseModifier("__builtin_requirement", this::parseBuiltinRequirementModifier),

        makeParseModifier("__magic_type", this::parseMagicTypeModifier),
        makeParseModifier("__intrinsic_type", this::parseIntrinsicTypeModifier),
        makeParseModifier("__implicit_conversion", this::parseImplicitConversionModifier),

        makeParseModifier("__attributeTarget", this::parseAttributeTargetModifier),

        // !!!!!!!!!!!!!!!!!!!!!!! Expr !!!!!!!!!!!!!!!!!!!!!!!!!!!

        makeParseExpression("this", this::parseThisExpr),
        makeParseExpression("true", this::parseTrueExpr),
        makeParseExpression("false", this::parseFalseExpr),
        makeParseExpression("__return_val", this::parseReturnValExpr),
        makeParseExpression("nullptr", this::parseNullPtrExpr),
        makeParseExpression("none", this::parseNoneExpr),
        makeParseExpression("try", this::parseTryExpr),
        makeParseExpression("no_diff", this::parseTreatAsDifferentiableExpr),
        makeParseExpression("__fwd_diff", this::parseForwardDifferentiate),
        makeParseExpression("__bwd_diff", this::parseBackwardDifferentiate),
        makeParseExpression("fwd_diff", this::parseForwardDifferentiate),
        makeParseExpression("bwd_diff", this::parseBackwardDifferentiate),
        makeParseExpression("__dispatch_kernel", this::parseDispatchKernel),
        makeParseExpression("sizeof", this::parseSizeOfExpr),
        makeParseExpression("alignof", this::parseAlignOfExpr),
        makeParseExpression("countof", this::parseCountOfExpr),

    )

    override fun parse(type: IElementType, builder: PsiBuilder): ASTNode {
        parseLight(type, builder)
        return builder.treeBuilt
    }

    override fun parseLight(type: IElementType, baseBuilder: PsiBuilder) {

        val builder = adapt_builder_(type, baseBuilder, this, null)
        val marker = enter_section_(builder, 0, _COLLAPSE_, null)

        pushScope(SlangIFileElementType())
        for (syntaxDeclaration in builtinSyntaxDeclarations)
            scope?.syntaxDeclarations?.set(syntaxDeclaration.name, syntaxDeclaration)

        val result = parseSourceFile(builder, 1)
        popScope()

        exit_section_(builder, 0, marker, type, result, true, TRUE_CONDITION)

    }

    private fun nextTokenAfterModifiersIs(builder: PsiBuilder, level: Int, name: String): Boolean {
        while (true) {
            if (nextTokenIs(builder, name))
                return true
            else if (tryParseUsingSyntaxDecl(builder, level, SyntaxDeclaration.Type.Modifier)) {
                // No lexer advance, since the parse has already consumed the tokens
                continue
            }
            return false
        }
    }

    private fun nextTokenAheadIs(builder: PsiBuilder, name: String, offset: Int): Boolean {
        if (offset <= 0)
            return false

        val marker = builder.mark()
        for (i in 0 until offset)
            builder.advanceLexer()
        val result = builder.tokenText == name
        marker.rollbackTo()
        return result
    }

    private fun pushScope(type: IElementType, namespaceName: String? = null) {
        val scope = Scope(type, this.scope, namespaceName)
        pushScope(scope)
    }

    private fun pushScope(scope: Scope) {
        scope.parent = if (scopeStack.size > 0) scopeStack.last() else null
        scopeStack.push(scope)
        scopes.push(scope)
    }

    private fun popScope(): Scope {
        return scopeStack.pop()
    }

    private fun makeParseDeclaration(name: String, callback: (PsiBuilder, Int) -> Boolean): SyntaxDeclaration {
        val syntaxDeclaration = SyntaxDeclaration(name, SyntaxDeclaration.Type.Declaration)
        syntaxDeclaration.parseCallback = callback
        return syntaxDeclaration
    }

    private fun makeParseModifier(name: String, elementCast: SlangElementType): SyntaxDeclaration {
        val syntaxDeclaration = SyntaxDeclaration(name, SyntaxDeclaration.Type.Modifier)
        syntaxDeclaration.elementSimpleCast = elementCast
        return syntaxDeclaration
    }

    private fun makeParseModifier(name: String, callback: (PsiBuilder, Int) -> Boolean): SyntaxDeclaration {
        val syntaxDeclaration = SyntaxDeclaration(name, SyntaxDeclaration.Type.Modifier)
        syntaxDeclaration.parseCallback = callback
        return syntaxDeclaration
    }

    private fun makeParseExpression(name: String, callback: (PsiBuilder, Int) -> Boolean): SyntaxDeclaration {
        val syntaxDeclaration = SyntaxDeclaration(name, SyntaxDeclaration.Type.Expression)
        syntaxDeclaration.parseCallback = callback
        return syntaxDeclaration
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
                    val marker = enter_section_(builder)
                    if (tryParseUsingSyntaxDecl(builder, level, SyntaxDeclaration.Type.Modifier)) {
                        // No lexer advance, since the parse has already consumed the tokens
                        exit_section_(builder, marker, SlangTypes.TYPE_MODIFIER, true)
                        continue
                    }
                    else if (nextTokenIs(builder, "no_diff")) {
                        builder.advanceLexer()
                        exit_section_(builder, marker, SlangTypes.TYPE_MODIFIER, true)
                        continue
                    }
                    else if (enableGlslCode)
                        if (consumeToken(builder, "flat")) {
                            builder.advanceLexer()
                            exit_section_(builder, marker, SlangTypes.TYPE_MODIFIER, true)
                            continue
                        }
                    exit_section_(builder, marker, null, true)
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
                // A declaration that starts with an identifier might be:
                //
                // - A keyword-based declaration (e.g., `cbuffer ...`)
                // - The beginning of a type in a declarator-based declaration (e.g., `int ...`)

                if (tryParseUsingSyntaxDecl(builder, level, SyntaxDeclaration.Type.Declaration))
                    return true

                // TODO: see slang/slang-parser.cpp:4723

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

    private fun tryParseUsingSyntaxDecl(builder: PsiBuilder, level: Int, type: SyntaxDeclaration.Type): Boolean {
        if (!recursion_guard_(builder, level, "tryParseUsingSyntaxDecl"))
            return false

        if (!nextTokenIs(builder, SlangTypes.IDENTIFIER))
            return false

        val name = builder.tokenText!!
        val result = lookUp(name) ?: return false

        if (result.type != type)
            return false

        if (result.parseCallback != null) {
            result.parseCallback!!.invoke(builder, level)
        }
        else if (result.elementSimpleCast != null) {
            builder.remapCurrentToken(result.elementSimpleCast!!)
            builder.advanceLexer()
        }
        else {
            throw IllegalStateException()
        }
        return true
    }

    private fun lookUp(name: String): SyntaxDeclaration? {
        var currentScope = scope

        while (currentScope != null) {
            val originalParent = currentScope.parent

            while (currentScope != null)
            {
                if (currentScope.syntaxDeclarations.containsKey(name))
                    return currentScope.syntaxDeclarations[name]

                currentScope = currentScope.nextSibling
            }

            currentScope = originalParent
        }

        return null
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

        val callback: (PsiBuilder, Int, Boolean) -> Boolean = { b, l, g ->
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
                    pushScope(SlangTypes.STRUCT_DECLARATION)
                    popScope()
                    callbackResult
                }
                else {
                    callbackResult = callbackResult && maybeParseGenericConstraints(b, l, g)
                    pushScope(SlangTypes.STRUCT_DECLARATION)
                    callbackResult = callbackResult && parseDeclBody(b, l)
                    popScope()
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

        pushScope(SlangTypes.CLASS_DECLARATION)
        result = result && parseDeclBody(builder, level + 1)
        popScope()

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

        val parseInner: (PsiBuilder, Int, Boolean) -> Boolean = { b, l, g ->
            var innerResult = parseOptionalInheritanceClause(b, l)
            innerResult = innerResult && maybeParseGenericConstraints(b, l, g)
            innerResult = innerResult && consumeToken(builder, SlangTypes.LEFT_BRACE)

            pushScope(SlangTypes.ENUM_DECLARATION)
            while (innerResult) {
                if (consumeToken(builder, SlangTypes.RIGHT_BRACE))
                    break
                innerResult = parseEnumCaseDecl(b, l)
                if (innerResult && consumeToken(builder, SlangTypes.RIGHT_BRACE))
                    break
                else
                    innerResult = innerResult && consumeToken(builder, SlangTypes.COMMA)
            }
            popScope()

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

    private fun parseGenericApp(builder: PsiBuilder, level: Int, isTesting: Boolean = false): Boolean {
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

        if (isTesting)
            marker.rollbackTo()
        else
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
        if (!recursion_guard_(builder, level, "parseStaticMemberType"))
            return false

        val marker = enter_section_(builder, level, _LEFT_)

        // When called the :: or . have been consumed, so don't need to consume here.
        val result = consumeToken(builder, SlangTypes.IDENTIFIER)

        exit_section_(builder, level, marker, SlangTypes.STATIC_MEMBER_EXPRESSION, result, false, null)
        return result
    }

    private fun parseMemberType(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseMemberType"))
            return false

        val marker = enter_section_(builder, level, _LEFT_)

        // When called the :: or . have been consumed, so don't need to consume here.
        val result = consumeToken(builder, SlangTypes.IDENTIFIER)

        exit_section_(builder, level, marker, SlangTypes.MEMBER_EXPRESSION, result, false, null)
        return result
    }

    private fun parseOptGenericDecl(
        builder: PsiBuilder,
        level: Int,
        parseInner: (PsiBuilder, Int, Boolean) -> Boolean)
            : Boolean
    {
        if (nextTokenIs(builder, SlangTypes.LESS_OP)) {
            pushScope(SlangTypes.GENERIC_DECLARATION)
            val result = parseGenericDeclImpl(builder, level)
            popScope()
            return result && parseInner(builder, level, true)
        }
        return parseInner(builder, level, scope?.type == SlangTypes.GENERIC_DECLARATION)
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

    private fun maybeParseGenericConstraints(builder: PsiBuilder, level: Int, hasGenericParent: Boolean): Boolean {
        if (!recursion_guard_(builder, level, "parseGenericConstraints"))
            return false

        if (!hasGenericParent)
            return true

        var result = true
        while (result && nextTokenIs(builder, "where")) {
            val marker = enter_section_(builder)
            builder.advanceLexer() // Consume 'where'

            result = parseTypeExp(builder, level + 1)
            if (result && consumeToken(builder, SlangTypes.COLON))
                while (result) {
                    result = parseTypeExp(builder, level + 1)
                    if (result && !consumeToken(builder, SlangTypes.COMMA))
                        break
                }
            else if (result && consumeToken(builder, SlangTypes.ASSIGN_OP))
                result = parseTypeExp(builder, level + 1)

            exit_section_(builder, marker, SlangTypes.GENERIC_TYPE_CONSTRAINT_DECLARATION, result)
        }

        return result
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

    private fun parseHLSLRegisterNameAndOptionalComponentMask(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseHlslRegisterNameAndOptionalComponentMask"))
            return false

        var result = consumeToken(builder, SlangTypes.IDENTIFIER)

        if (result && consumeToken(builder, SlangTypes.DOT))
            result = consumeToken(builder, SlangTypes.IDENTIFIER)

        return result
    }

    private fun parseHlslRegisterSemantic(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseHlslRegisterSemantic"))
            return false

        val marker = enter_section_(builder)

        // Read the `register` keyword
        var result = consumeToken(builder, "register")

        // Expect a parenthesized list of additional arguments
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)

        // First argument is a required register name and optional component mask
        result = result && parseHLSLRegisterNameAndOptionalComponentMask(builder, level + 1)

        // Second argument is an optional register space
        if (result && consumeToken(builder, SlangTypes.COMMA))
            result = consumeToken(builder, SlangTypes.IDENTIFIER)

        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)

        exit_section_(builder, marker, SlangTypes.HLSL_REGISTER_SEMANTIC, result)
        return result
    }

    private fun parseHlslPackOffsetSemantic(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseHlslPackOffset"))
            return false

        val marker = enter_section_(builder)

        // Read the `packoffset` keyword
        var result = consumeToken(builder, "packoffset")

        // Expect a parenthesized list of additional arguments
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)

        // First and only argument is a required register name and optional component mask
        result = result && parseHLSLRegisterNameAndOptionalComponentMask(builder, level + 1)

        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)

        exit_section_(builder, marker, SlangTypes.HLSL_PACK_OFFSET_SEMANTIC, result)
        return result
    }

    private fun parseRayPayloadAccessSemantic(builder: PsiBuilder, level: Int, write: Boolean): Boolean {
        if (!recursion_guard_(builder, level, "parseRayPayloadAccess"))
            return false

        val marker = enter_section_(builder)

        // Read the keyword that introduced the semantic
        var result = consumeToken(builder, SlangTypes.IDENTIFIER)

        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)

        while (result) {
            if (consumeToken(builder, SlangTypes.RIGHT_PAREN))
                break

            result = consumeToken(builder, SlangTypes.IDENTIFIER)

            if (result && consumeToken(builder, SlangTypes.RIGHT_PAREN))
                break
            result = result && consumeToken(builder, SlangTypes.COMMA)
        }

        exit_section_(builder, marker, if (write) SlangTypes.RAY_PAYLOAD_WRITE_SEMANTIC else SlangTypes.RAY_PAYLOAD_READ_SEMANTIC, result)
        return result
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
                exit_section_(builder, level, marker, SlangTypes.STATIC_MEMBER_EXPRESSION, result, false, null)

                if (result && nextTokenIs(builder, SlangTypes.LESS_OP)) {
                    result = maybeParseGenericApp(builder, level)
                }
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

    private fun maybeParseGenericApp(builder: PsiBuilder, level: Int): Boolean {
        return if (!nextTokenIs(builder, SlangTypes.LESS_OP)) false
            else tryParseGenericApp(builder, level)
    }

    private fun tryParseGenericApp(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "tryParseGenericApp"))
            return false

        if (parseGenericApp(builder, level, true))
            return parseGenericApp(builder, level)
        return false
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
            val wasIdentifier = if (nextTokenIs(builder, SlangTypes.COMPLETION_REQUEST)) {
                builder.advanceLexer()
                false
            }
            else {
                result = consumeToken(builder, SlangTypes.IDENTIFIER)
                true
            }

            exit_section_(builder, marker, SlangTypes.VARIABLE_EXPRESSION, result)

            if (wasIdentifier)
                maybeParseGenericApp(builder, level)

            return result
        }
        else if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            if (tryParseUsingSyntaxDecl(builder, level + 1, SyntaxDeclaration.Type.Expression))
                return true

            val marker = enter_section_(builder)
            var result = parseDeclName(builder, level + 1)
            exit_section_(builder, marker, SlangTypes.VARIABLE_EXPRESSION, result)

            if (result && nextTokenIs(builder, SlangTypes.LESS_OP)) {
                result = maybeParseGenericApp(builder, level)
            }

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

        val parseInner: (PsiBuilder, Int, Boolean) -> Boolean = { b, l, g ->
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

            pushScope(SlangTypes.FUNCTION_DECLARATION)

            var result = parseParameterList(b, l)

            if (result)
                consumeToken(b, "throws")

            result = result && parseOptSemantics(b, l)

            val funcScope = popScope()
            result = result && maybeParseGenericConstraints(b, l, g)
            pushScope(funcScope)

            result = result && parseOptBody(b, l)

            popScope()

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

        pushScope(SlangTypes.BLOCK_STATEMENT)
        while (result) {
            if (nextTokenIs(builder, SlangTypes.RIGHT_BRACE))
                break

            result = if (nextTokenAfterModifiersIs(builder, level + 1, "struct"))
                parseDecl(builder, level + 1)
            else if (nextTokenIs(builder, "typedef"))
                parseTypeDef(builder, level + 1)
            else if (nextTokenIs(builder, "typealias"))
                parseTypeAliasDecl(builder, level + 1)
            else
                parseStatement(builder, level + 1)

            // TODO: handle recovery
        }
        popScope()

        result = result && consumeToken(builder, SlangTypes.RIGHT_BRACE)

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
        if (!recursion_guard_(builder, level, "parseTypeDef"))
            return false

        // Skip 'typedef'
        builder.advanceLexer()

        val marker = enter_section_(builder)
        var result = parseTypeExp(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
        exit_section_(builder, marker, SlangTypes.TYPEDEF_DECLARATION, result)
        return result
    }

    private fun parseTypeAliasDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseTypeAlias"))
            return false

        // Skip 'typealias'
        builder.advanceLexer()

        val marker = enter_section_(builder)
        var result = consumeToken(builder, SlangTypes.IDENTIFIER)

        val parseInner: (PsiBuilder, Int, Boolean) -> Boolean = { b, l, g ->
            var r = maybeParseGenericConstraints(b, l, g)
            r = r && consumeToken(b, SlangTypes.ASSIGN_OP)
            r = r && parseTypeExp(b, l)
            r = r && consumeToken(b, SlangTypes.SEMICOLON)
            r
        }
        result = result && parseOptGenericDecl(builder, level + 1, parseInner)

        exit_section_(builder, marker, SlangTypes.TYPEALIAS_DECLARATION, result)
        return result
    }

    private fun parseStatement(builder: PsiBuilder, level: Int, isIfStmt: Boolean = false): Boolean {
        if (!recursion_guard_(builder, level, "parseStatement"))
            return false

        val marker = enter_section_(builder)

        val currentOffset = builder.currentOffset
        var result = parseModifiers(builder, level + 1)
        val hadModifiers = currentOffset < builder.currentOffset

        if (nextTokenIs(builder, SlangTypes.LEFT_BRACE)) {
            pushScope(SlangTypes.STATEMENT)
            result = result && parseDeclBody(builder, level + 1)
            popScope()
        }
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
        else if (nextTokenIs(builder, "__intrinsic_asm"))
            result = result && parseIntrinsicAsmStmt(builder, level + 1)
        else if (nextTokenIs(builder, "case"))
            result = result && parseCaseStmt(builder, level + 1)
        else if (nextTokenIs(builder, "default"))
            result = result && parseDefaultStmt(builder, level + 1)
        else if (nextTokenIs(builder, "__GPU_FOREACH"))
            result = result && parseGpuForeachStmt(builder, level + 1)
        else if (nextTokenIs(builder, "__intrinsic_asm"))
            result = result && parseIntrinsicAsmStmt(builder, level + 1)
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

    private fun parseIfLetStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIfStatement"))
            return false

        val marker = enter_section_(builder)

        pushScope(SlangTypes.LET_DECLARATION)
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
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)

        pushScope(SlangTypes.IF_STATEMENT)
        result = result && parseStatement(builder, level + 1, true)
        popScope()

        if (result && consumeToken(builder, "else")) {
            result = parseStatement(builder, level + 1, true)
        }

        popScope()

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
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)

        result = result && parseStatement(builder, level + 1, true)

        if (result && consumeToken(builder, "else")) {
            result = parseStatement(builder, level + 1, true)
        }

        exit_section_(builder, marker, SlangTypes.IF_STATEMENT, result)
        return result
    }

    private fun parseForStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseForStatement"))
            return false

        // HLSL implements the bad approach to scoping a `for` loop
        // variable, and we want to respect that, but *only* when
        // parsing HLSL code.
        //
        val brokenScoping = false

        val marker = enter_section_(builder)
        if (!brokenScoping)
            pushScope(SlangTypes.FOR_STATEMENT)
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

        if (!brokenScoping)
            popScope()

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
        if (!recursion_guard_(builder, level, "parseDiscardStatement"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "discard")
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)
        exit_section_(builder, marker, SlangTypes.DISCARD_STATEMENT, result)
        return result
    }

    private fun parseSwitchStmt(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseSwitchStmt"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "switch")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        result = result && parseBlockStatement(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.SWITCH_STATEMENT, result)
        return result
    }

    private fun parseTargetSwitchStmt(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseTargetSwitchStmt"))
            return false

        val marker = enter_section_(builder)

        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_BRACE)

        while (result && !consumeToken(builder, SlangTypes.RIGHT_BRACE)) {
            pushScope(SlangTypes.TARGET_SWITCH_STATEMENT)

            val beforeCasesOffset = builder.currentOffset
            while (result) {
                if (nextTokenIs(builder, "case")) {
                    result = consumeToken(builder, "case")
                    result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
                    result = result && consumeToken(builder, SlangTypes.COLON)
                }
                else if (nextTokenIs(builder, "default")) {
                    result = consumeToken(builder, "default")
                    result = result && consumeToken(builder, SlangTypes.COLON)
                }
                else
                    break
            }

            if (builder.currentOffset == beforeCasesOffset) {
                builder.error("Unexpected token type ${builder.tokenType}, expected 'case', or 'default'")
                result = false
                // TODO: Handle recovery
            }
            else {
                while (result) {
                    if (nextTokenIs(builder, "case")
                        || nextTokenIs(builder, "default")
                        || nextTokenIs(builder, SlangTypes.RIGHT_BRACE))
                        break

                    result = parseStatement(builder, level + 1)
                }
            }

            popScope()
        }

        exit_section_(builder, marker, SlangTypes.TARGET_SWITCH_STATEMENT, result)
        return result
    }

    private fun parseIntrinsicAsmStmt(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIntrinsicAsmStmt"))
            return false

        val marker = enter_section_(builder)
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.STRING_LITERAL)
        while (result && consumeToken(builder, SlangTypes.COMMA)) {
            result = parseArgExpr(builder, level + 1)
        }
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, SlangTypes.INTRINSIC_ASM_STATEMENT, result)
        return result
    }

    private fun parseCaseStmt(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseCaseStmt"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "case")
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.COLON)

        exit_section_(builder, marker, SlangTypes.CASE_STATEMENT, result)
        return result
    }

    private fun parseDefaultStmt(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDefaultStmt"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, "default")
        result = result && consumeToken(builder, SlangTypes.COLON)

        exit_section_(builder, marker, SlangTypes.DEFAULT_STATEMENT, result)
        return result
    }

    private fun parseGpuForeachStmt(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGpuForeachStmt"))
            return false

        // Hard-coding parsing of the following:
        // __GPU_FOREACH(renderer, gridDims, LAMBDA(uint3 dispatchThreadID) {
        //  kernelCall(args, ...); });
        val marker = enter_section_(builder)

        var result = consumeToken(builder, "__GPU_FOREACH")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)

        result = result && parseArgExpr(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.COMMA)

        result = result && parseArgExpr(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.COMMA)

        result = result && consumeToken(builder, "LAMBDA")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)
        let {
            val varMarker = enter_section_(builder)
            result = result && parseTypeExp(builder, level + 2)
            result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
            exit_section_(builder, varMarker, SlangTypes.VARIABLE_DECL, result)
        }
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        result = result && consumeToken(builder, SlangTypes.LEFT_BRACE)

        pushScope(SlangTypes.GPU_FOREACH_STATEMENT)
        result = result && parseExpression(builder, level + 1)
        popScope()

        result = result && consumeToken(builder, SlangTypes.SEMICOLON)
        result = result && consumeToken(builder, SlangTypes.RIGHT_BRACE)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, SlangTypes.GPU_FOREACH_STATEMENT, result)
        return result
    }

    private fun parseCompileTimeStmt(builder: PsiBuilder, level: Int): Boolean {
        consumeToken(builder, SlangTypes.DOLLAR)

        return nextTokenIs(builder, "for") && parseCompileTimeForStmt(builder, level)
    }

    private fun parseCompileTimeForStmt(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseCompileTimeForStmt"))
            return false

        val marker = enter_section_(builder)

        var result = consumeToken(builder, "for")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)

        let {
            val varMarker = enter_section_(builder)
            result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
            exit_section_(builder, varMarker, SlangTypes.VARIABLE_DECL, result)
        }
        result = result && consumeToken(builder, "in")
        result = result && consumeToken(builder, "range")
        result = result && consumeToken(builder, SlangTypes.LEFT_PAREN)

        result = result && parseArgExpr(builder, level + 1)

        if (result && consumeToken(builder, SlangTypes.COMMA))
            result = parseArgExpr(builder, level + 1)

        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)

        pushScope(SlangTypes.COMPILE_TIME_FOR_STATEMENT)
        result = result && parseStatement(builder, level + 1)
        popScope()

        exit_section_(builder, marker, SlangTypes.COMPILE_TIME_FOR_STATEMENT, result)
        return result
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
        if (!recursion_guard_(builder, level, "parseLabelStatement"))
            return false

        val marker = enter_section_(builder)
        var result = consumeToken(builder, SlangTypes.IDENTIFIER)
        result = result && consumeToken(builder, SlangTypes.COLON)
        result = result && parseStatement(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.LABEL_STATEMENT, result)
        return result
    }

    private fun parseVarDeclStatement(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseVarDeclStatement"))
            return false

        val marker = enter_section_(builder)
        val result = parseDeclWithModifiers(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.DECLARATION_STATEMENT, result)
        return result
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
        if (!recursion_guard_(builder, level, "parseExpandExpr"))
            return false

        val marker = enter_section_(builder)
        val result = parseArgExpr(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.EXPAND_EXPRESSION, result)
        return result
    }

    private fun parseEachExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseEachExpr"))
            return false

        val marker = enter_section_(builder)
        val result = parsePostfixExpr(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.EACH_EXPRESSION, result)
        return result
    }

    private fun parseGenericDeclImpl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGenericDeclImpl"))
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

    private fun parseAssocType(builder: PsiBuilder, level: Int): Boolean { TODO("Not yet implemented") }
    private fun parseGlobalGenericTypeParamDecl(builder: PsiBuilder, level: Int): Boolean { TODO("Not yet implemented") }

    private fun parseHLSLCBufferDecl(builder: PsiBuilder, level: Int): Boolean {
        return parseBufferBlockDecl(builder, level, SlangTypes.HLSL_CBUFFER_DECLARATION)
    }

    private fun parseHLSLTBufferDecl(builder: PsiBuilder, level: Int): Boolean {
        return parseBufferBlockDecl(builder, level, SlangTypes.HLSL_TBUFFER_DECLARATION)
    }

    private fun parseBufferBlockDecl(builder: PsiBuilder, level: Int, type: SlangElementType): Boolean {
        if (!recursion_guard_(builder, level, "parseBufferBlockDecl"))
            return false

        val marker = enter_section_(builder)
        // Skip 'cbuffer' or 'tbuffer' keyword
        builder.advanceLexer()

        var result = nextTokenIs(builder, SlangTypes.IDENTIFIER)
        if (result) {
            builder.remapCurrentToken(SlangTypes.VARIABLE_NAME)
            builder.advanceLexer()
        }

        result = result && parseOptSemantics(builder, level + 1)

        result = result && parseDeclBody(builder, level + 1)

        if (result && nextTokenIs(builder, SlangTypes.IDENTIFIER) && builder.lookAhead(1) == SlangTypes.SEMICOLON) {
            builder.remapCurrentToken(SlangTypes.VARIABLE_NAME)
            builder.advanceLexer()
            builder.advanceLexer()
        }

        exit_section_(builder, marker, type, result)
        return result
    }

    private fun parseGenericDecl(builder: PsiBuilder, level: Int): Boolean { TODO("Not yet implemented") }
    private fun parseExtensionDecl(builder: PsiBuilder, level: Int): Boolean { TODO("Not yet implemented") }
    private fun parseConstructorDecl(builder: PsiBuilder, level: Int): Boolean { TODO("Not yet implemented") }
    private fun parseSubscriptDecl(builder: PsiBuilder, level: Int): Boolean { TODO("Not yet implemented") }

    private fun parsePropertyDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseInterfaceDecl"))
            return false

        pushScope(SlangTypes.PARAMETER_DECLARATION)

        // We want to support property declarations with two
        // different syntaxes.
        //
        // First, we want to support a syntax that is consistent
        // with C-style ("traditional") variable declarations:
        //
        //                int myVar = 2;
        //      proprerty int myProp { ... }
        //
        // Second we want to support a syntax that is
        // consistent with `let` and `var` declarations:
        //
        //      let      myVar  : int = 2;
        //      property myProp : int { ... }
        //
        // The latter case is more constrained, and we will
        // detect with two tokens of lookahead. If the
        // next token (after `property`) is an identifier,
        // and the token after that is a colon (`:`), then
        // we assume we are in the `let`/`var`-style case.
        //

        if (!recursion_guard_(builder, level, "parseInterfaceDecl"))
            return false

        val marker = enter_section_(builder)

        // Skip 'property' keyword
        builder.advanceLexer()

        var result: Boolean
        if (SlangPsiUtil.peekModernStyleVarDeclaration(builder)) {
            builder.remapCurrentToken(SlangTypes.VARIABLE_NAME)
            builder.advanceLexer()

            result = consumeToken(builder, SlangTypes.COLON)
            result = result && parseTypeExp(builder, level + 1)
        }
        else {
            // The traditional syntax requires a bit more
            // care to parse, since it needs to support
            // C declarator syntax.
            //
            result = parseType(builder, level + 1)

            result = result && parseDeclarator(builder, level + 1, false)
        }
        result = result && parseStorageDeclBody(builder, level + 1)

        popScope()

        exit_section_(builder, marker, SlangTypes.PARAMETER_DECLARATION, result)
        return result
    }

    private fun parseStorageDeclBody(builder: PsiBuilder, level: Int): Boolean {
        var result = true

        if (consumeToken(builder, SlangTypes.LEFT_BRACE)) {
            while (result && !consumeToken(builder, SlangTypes.RIGHT_BRACE)) {
                result = parseAccessorDecl(builder, level)
            }
        }
        else {
            result = consumeToken(builder, SlangTypes.SEMICOLON)
        }

        return result
    }

    private fun parseAccessorDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseAccessorDecl"))
            return false

        var type: IElementType? = null

        val marker = enter_section_(builder)

        var result = parseModifiers(builder, level + 1)

        if (result && consumeToken(builder, "get"))
            type = SlangTypes.GETTER_DECLARATION
        else if (result && consumeToken(builder, "set"))
            type = SlangTypes.SETTER_DECLARATION
        else if (result && consumeToken(builder, "ref"))
            type = SlangTypes.SETTER_DECLARATION
        else {
            result = false
            builder.error("Unexpected accessor '${builder.tokenText}'")
        }

        if (type != null)
            pushScope(type)

        // A `set` declaration should support declaring an explicit
        // name for the parameter representing the new value.
        //
        // We handle this by supporting an arbitrary parameter list
        // on any accessor, and then assume that semantic checking
        // will diagnose any cases that aren't allowed.
        //
        if (result && nextTokenIs(builder, SlangTypes.LEFT_PAREN))
            result = parseModernParamList(builder, level + 1)

        result = if (result && nextTokenIs(builder, SlangTypes.LEFT_BRACE))
            parseBlockStatement(builder, level + 1)
        else
            result && consumeToken(builder, SlangTypes.SEMICOLON)

        if (type != null)
            popScope()

        exit_section_(builder, marker, type, result)
        return result
    }

    private fun parseInterfaceDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseInterfaceDecl"))
            return false

        val marker = enter_section_(builder)

        // Skip "interface" keyword
        builder.advanceLexer()

        consumeToken(builder, SlangTypes.COMPLETION_REQUEST)

        var result = nextTokenIs(builder, SlangTypes.IDENTIFIER)
        if (result) {
            builder.remapCurrentToken(SlangTypes.INTERFACE_NAME)
            builder.advanceLexer()
        }

        val parseInner: (PsiBuilder, Int, Boolean) -> Boolean = { b, l, g ->
            var r = parseOptionalInheritanceClause(b, l)
            r = r && maybeParseGenericConstraints(b, l, g)
            r = r && parseDeclBody(b, l)
            r
        }
        result = result && parseOptGenericDecl(builder, level + 1, parseInner)

        exit_section_(builder, marker, SlangTypes.PARAMETER_DECLARATION, result)
        return result
    }

    private fun parseSyntaxDecl(builder: PsiBuilder, level: Int): Boolean { TODO("Not yet implemented") }

    // Parse declaration of a name to be used for resolving `[attribute(...)]` style modifiers.
    //
    // These are distinct from `syntax` declarations, because their names don't get added
    // to the current scope using their default name.
    //
    // Also, attribute-specific code doesn't get invoked during parsing. We always parse
    // using the default attribute-parsing logic and then all specialized behavior takes
    // place during semantic checking.
    //
    private fun parseAttributeSyntaxDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseAttributeSyntaxDecl"))
            return false

        // Right now the basic form is:
        //
        // attribute_syntax <name:id> : <syntaxClass:id>;
        //
        // - `name` gives the name of the attribute to define.
        // - `syntaxClass` is the name of an AST node class that we expect
        //   this attribute to create when checked.
        // - `existingKeyword` is the name of an existing keyword that
        //   the new syntax should be an alias for.
        //
        val marker = enter_section_(builder)

        // Skip "attribute_syntax" keyword
        builder.advanceLexer()

        var result = consumeToken(builder, SlangTypes.LEFT_BRACKET)
        result = result && consumeToken(builder, SlangTypes.IDENTIFIER)

        if (result && consumeToken(builder, SlangTypes.LEFT_PAREN)) {
            while (result && !consumeToken(builder, SlangTypes.RIGHT_PAREN)) {
                result = parseAttributeParamDecl(builder, level + 1)

                if (result && consumeToken(builder, SlangTypes.RIGHT_PAREN))
                    break

                result = result && consumeToken(builder, SlangTypes.COMMA)
            }
        }

        result = result && consumeToken(builder, SlangTypes.RIGHT_BRACKET)

        // Next we look for a clause that specified the AST node class.
        if (result && consumeToken(builder, SlangTypes.COLON)) {
            result = consumeToken(builder, SlangTypes.IDENTIFIER)
            // TODO: Validate AST node syntax (slang-parser:4546)
        }

        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, SlangTypes.ATTRIBUTE_SYNTAX_DECLARATION, result)
        return result
    }

    private fun parseAttributeParamDecl(builder: PsiBuilder, level: Int): Boolean {
        val marker = enter_section_(builder)

        var result = nextTokenIs(builder, SlangTypes.IDENTIFIER)
        if (result) {
            builder.remapCurrentToken(SlangTypes.PARAMETER_NAME)
            builder.advanceLexer()
        }

        if (result && consumeToken(builder, SlangTypes.COLON))
            result = parseTypeExp(builder, level + 1)

        if (result && consumeToken(builder, SlangTypes.ASSIGN_OP))
            result = parseInitExpr(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.PARAMETER_DECLARATION, result)
        return result
    }

    private fun parseImportDecl(builder: PsiBuilder, level: Int): Boolean {
        return parseFileReferenceDeclBase(builder, level, SlangTypes.IMPORT_DECLARATION)
    }

    private fun parseIncludeDecl(builder: PsiBuilder, level: Int): Boolean {
        return parseFileReferenceDeclBase(builder, level, SlangTypes.INCLUDE_DECLARATION)
    }

    private fun parseImplementingDecl(builder: PsiBuilder, level: Int): Boolean {
        return parseFileReferenceDeclBase(builder, level, SlangTypes.IMPLEMENTING_DECLARATION)
    }

    private fun parseFileReferenceDeclBase(builder: PsiBuilder, level: Int, elementType: SlangElementType): Boolean {
        if (!recursion_guard_(builder, level, "parseFileReferenceDeclBase"))
            return false

        val marker = enter_section_(builder)

        // Skip '__import', 'import', '__include' or 'implementing' keyword
        builder.advanceLexer()

        var result = true

        if (consumeToken(builder, SlangTypes.IDENTIFIER)) {
            while (result && consumeToken(builder, SlangTypes.DOT))
                result = consumeToken(builder, SlangTypes.IDENTIFIER)
        }
        else if (!consumeToken(builder, SlangTypes.STRING_LITERAL))
            result = false

        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, elementType, result)
        return result
    }

    private fun parseModuleDeclarationDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseModuleDeclarationDecl"))
            return false

        val marker = enter_section_(builder)

        // Skip 'module' keyword
        builder.advanceLexer()

        consumeToken(builder, SlangTypes.IDENTIFIER) || consumeToken(builder, SlangTypes.STRING_LITERAL)
        val result = consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, SlangTypes.MODULE_DECLARATION, result)
        return result
    }

    private fun parseLetDecl(builder: PsiBuilder, level: Int): Boolean {
        return parseModernVarDeclCommon(builder, level)
    }
    private fun parseVarDecl(builder: PsiBuilder, level: Int): Boolean {
        return parseModernVarDeclCommon(builder, level)
    }

    private fun parseModernVarDeclCommon(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseModernVarDeclCommon"))
            return false

        val marker = enter_section_(builder)

        // Skip 'var' or 'let' keyword
        builder.advanceLexer()

        var result = nextTokenIs(builder, SlangTypes.IDENTIFIER)
        if (result) {
            builder.remapCurrentToken(SlangTypes.VARIABLE_NAME)
            builder.advanceLexer()
        }

        if (result && consumeToken(builder, SlangTypes.COLON))
            result = parseTypeExp(builder, level + 1)


        if (result && consumeToken(builder, SlangTypes.ASSIGN_OP))
            result = parseInitExpr(builder, level + 1)

        exit_section_(builder, marker, SlangTypes.VARIABLE_DECL, result)
        return result
    }

    private fun parseFuncDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseFuncDecl"))
            return false

        val marker = enter_section_(builder)

        // Skip 'func' keyword
        builder.advanceLexer()

        var result = nextTokenIs(builder, SlangTypes.IDENTIFIER)
        if (result) {
            builder.remapCurrentToken(SlangTypes.FUNCTION_NAME)
            builder.advanceLexer()
        }

        val parseInner: (PsiBuilder, Int, Boolean) -> Boolean = { b, l, g ->
            pushScope(SlangTypes.FUNCTION_DECLARATION)
            var r = parseModernParamList(b, l)
            if (r && consumeToken(builder, "throws"))
                r = parseTypeExp(b, l)
            if (r && consumeToken(builder, SlangTypes.RIGHT_ARROW))
                r = parseTypeExp(b, l)
            val funcScope = this.scope!!
            popScope()
            r = r && maybeParseGenericConstraints(b, l, g)
            pushScope(funcScope)
            r = r && parseOptBody(b, l)
            popScope()
            r
        }
        result = result && parseOptGenericDecl(builder, level + 1, parseInner)

        exit_section_(builder, marker, SlangTypes.FUNCTION_DECLARATION, result)
        return result
    }

    private fun parseModernParamList(builder: PsiBuilder, level: Int): Boolean {
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)

        while (result && !consumeToken(builder, SlangTypes.RIGHT_PAREN)) {
            result = parseModernParamDecl(builder, level)
            if (result && consumeToken(builder, SlangTypes.RIGHT_PAREN))
                break
            result = result && consumeToken(builder, SlangTypes.COMMA)
        }

        return result
    }

    private fun parseModernParamDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseModernParamDecl"))
            return false

        // TODO: For "modern" parameters, we should probably
        // not allow arbitrary keyword-based modifiers (only allowing
        // `[attribute]`s), and should require that direction modifiers
        // like `in`, `out`, and `in out`/`inout` be applied to the
        // type (after the colon).
        //

        val marker = enter_section_(builder)

        var result = parseModifiers(builder, level + 1)

        // We want to allow both "modern"-style and traditional-style
        // parameters to appear in any modern-style parameter list,
        // in order to allow programmers the flexibility to code in
        // a way that feels natural and not run into lots of
        // errors.
        //

        val isModernDecl = SlangPsiUtil.peekModernStyleVarDeclaration(builder)

        result = if (isModernDecl)
            result && parseModernVarDeclBaseCommon(builder, level + 1)
        else
            result && parseTraditionalParamDeclCommonBase(builder, level + 1)

        exit_section_(builder, marker, if (isModernDecl) SlangTypes.MODERN_PARAMETER_DECLARATION else SlangTypes.PARAMETER_DECLARATION, result)
        return result
    }

    private fun parseModernVarDeclBaseCommon(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseModernVarDeclBaseCommon"))
            return false

        var result = nextTokenIs(builder, SlangTypes.IDENTIFIER)
        if (result) {
            builder.remapCurrentToken(SlangTypes.PARAMETER_NAME)
            builder.advanceLexer()
        }

        if (result && consumeToken(builder, SlangTypes.COLON))
            result = parseTypeExp(builder, level)

        if (result && consumeToken(builder, SlangTypes.ASSIGN_OP))
            result = parseInitExpr(builder, level)

        return result
    }

    private fun parseGlobalGenericValueParamDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGlobalGenericValueParamDecl"))
            return false

        val marker = enter_section_(builder)
        // Skip '__generic_value_param' keyword
        builder.advanceLexer()

        var result = nextTokenIs(builder, SlangTypes.IDENTIFIER)
        if (result) {
            builder.remapCurrentToken(SlangTypes.GLOBAL_GENERIC_VALUE_PARAMETER_NAME)
            builder.advanceLexer()
        }

        if (result && consumeToken(builder, SlangTypes.COLON))
            result = parseTypeExp(builder, level + 1)

        if (result && consumeToken(builder, SlangTypes.ASSIGN_OP))
            result = parseInitExpr(builder, level + 1)

        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, SlangTypes.GLOBAL_GENERIC_VALUE_PARAMETER_DECLARATION, result)
        return result
    }

    private fun parseNamespaceDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseNamespaceDecl"))
            return false

        val marker = enter_section_(builder)
        // Skip 'namespace' keyword
        builder.advanceLexer()

        val initialScopeStackSize = scopeStack.size

        var result = true
        var namespaceName = this.scope!!.namespaceName
        do {
            if (!nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
                result = false
                break
            }
            namespaceName += "${builder.tokenText}::"
            val existingScope = SlangPsiUtil.findNamespaceScope(namespaceName, this.scopes)

            if (existingScope == null)
                pushScope(SlangTypes.NAMESPACE_DECLARATION, namespaceName)
            else
                pushScope(existingScope)

            builder.remapCurrentToken(SlangTypes.NAMESPACE_NAME)
            builder.advanceLexer()
        } while (consumeToken(builder, SlangTypes.DOT) || consumeToken(builder, SlangTypes.SCOPE))

        // Now that we have a namespace declaration to fill in
        // (whether a new or existing one), we can parse the
        // `{}`-enclosed body to add declarations as children
        // of the namespace.
        //
        result = result && parseDeclBody(builder, level + 1)

        while (scopeStack.size > initialScopeStackSize)
            popScope()

        exit_section_(builder, marker, SlangTypes.NAMESPACE_DECLARATION, result)
        return result
    }

    private fun parseUsingDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseUsingDecl"))
            return false

        val marker = enter_section_(builder)
        // Skip 'using' keyword
        builder.advanceLexer()

        // TODO: We may eventually want to support declarations
        // of the form `using <id> = <expr>;` which introduce
        // a shorthand alias for a namespace/type/whatever.
        //
        // For now we are just sticking to the most basic form.

        // As a compatibility feature for programmers used to C++,
        // we allow the `namespace` keyword to come after `using`,
        // where it has no effect.
        //
        consumeToken(builder, "namespace")

        // The entity that is going to be used is identified
        // using an arbitrary expression (although we expect
        // that valid code will not typically use the full
        // freedom of what the expression grammar supports.)
        //
        var result = parseExpression(builder, level + 1)

        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, SlangTypes.USING_DECLARATION, result)
        return result
    }

    private fun parseIgnoredBlockDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIgnoredBLockDecl"))
            return false

        val marker = enter_section_(builder)
        // Skip '__ignored_block' keyword
        builder.advanceLexer()
        val result = nextTokenIs(builder, SlangTypes.LEFT_BRACE)
        if (result)
            SlangPsiUtil.skipBalancedToken(builder)
        exit_section_(builder, marker, SlangTypes.EMPTY_DECLARATION, result)
        return result
    }

    private fun parseTransparentBlockDecl(builder: PsiBuilder, level: Int): Boolean {
        // TODO: test scope slang-parser.cpp:3731
        builder.remapCurrentToken(SlangTypes.TRANSPARENT_BLOCK_DECLARATION)
        builder.advanceLexer()
        return parseDeclBody(builder, level)
    }

    private fun parseFileDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseFileDecl"))
            return false

        val marker = enter_section_(builder)
        // Skip '__file_decl' keyword
        builder.advanceLexer()
        val result = parseDeclBody(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.FILE_DECLARATION, result)
        return result
    }

    private fun parseRequireCapabilityDecl(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseRequireCapabilityDecl"))
            return false

        val marker = enter_section_(builder)

        // Skip '__require_capability' keyword
        builder.advanceLexer()

        var result = true
        while (result && nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
            if (CapabilityNames.entries.find { it.tokenText == builder.tokenText } == null) {
                result = false
                builder.error("Unknown capability: ${builder.tokenText}")
            }
            else
                builder.advanceLexer()
            if (result && consumeToken(builder, SlangTypes.ADD_OP) && consumeToken(builder, SlangTypes.COMMA))
                continue

            result = false
        }
        result = result && consumeToken(builder, SlangTypes.SEMICOLON)

        exit_section_(builder, marker, SlangTypes.REQUIRE_CAPABILITIES_DECLARATION, result)
        return result
    }

    private fun parseLayoutModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseLayoutModifier"))
            return false

        val marker = enter_section_(builder)

        // Skip 'layout' keyword
        builder.advanceLexer()

        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        while (result && !consumeToken(builder, SlangTypes.RIGHT_PAREN)) {
            if (nextTokenIs(builder, "local_size_x")
                || nextTokenIs(builder, "local_size_y")
                || nextTokenIs(builder, "local_size_z"))
            {
                val localSizeMarker = enter_section_(builder)
                builder.advanceLexer()
                if (consumeToken(builder, SlangTypes.ASSIGN_OP))
                    result = parseAtomicExpr(builder, level + 2)
                exit_section_(builder, localSizeMarker, SlangTypes.GLSL_LAYOUT_LOCAL_SIZE_ATTRIBUTE, result)
            }
            else if (nextTokenIs(builder, "derivative_group_quadsNV")) {
                builder.remapCurrentToken(SlangTypes.GLSL_LAYOUT_DERIVATIVE_GROUP_QUAD_ATTRIBUTE)
                builder.advanceLexer()
            }
            else if (nextTokenIs(builder, "derivative_group_linearNV")) {
                builder.remapCurrentToken(SlangTypes.GLSL_LAYOUT_DERIVATIVE_GROUP_LINEAR_ATTRIBUTE)
                builder.advanceLexer()
            }
            else if (nextTokenIs(builder, "input_attachment_index")) {
                val attachmentMarker = enter_section_(builder)
                builder.advanceLexer()
                if (consumeToken(builder, SlangTypes.ASSIGN_OP))
                    result = consumeToken(builder, SlangTypes.INTEGER_LITERAL)
                exit_section_(builder, attachmentMarker, SlangTypes.GLSL_INPUT_ATTACHMENT_INDEX_LAYOUT_ATTRIBUTE, result)
            }
            else if (nextTokenIs(builder, "binding") || nextTokenIs(builder, "set")) {
                val attachmentMarker = enter_section_(builder)
                builder.advanceLexer()
                result = consumeToken(builder, SlangTypes.ASSIGN_OP)
                result = result && consumeToken(builder, SlangTypes.INTEGER_LITERAL)
                exit_section_(builder, attachmentMarker, SlangTypes.GLSL_BINDING_ATTRIBUTE, result)
            }
            else if (nextTokenIs(builder, "binding") || nextTokenIs(builder, "set")) {
                val bindingMarker = enter_section_(builder)
                builder.advanceLexer()
                result = consumeToken(builder, SlangTypes.ASSIGN_OP)
                result = result && consumeToken(builder, SlangTypes.INTEGER_LITERAL)
                exit_section_(builder, bindingMarker, SlangTypes.GLSL_BINDING_ATTRIBUTE, result)
            }
            else if (SlangPsiUtil.nextTokenIs(builder, GlslImageFormats.array)) {
                builder.remapCurrentToken(SlangTypes.FORMAT_ATTRIBUTE)
                builder.advanceLexer()
            }
            else if (nextTokenIs(builder, "push_constant")) {
                builder.remapCurrentToken(SlangTypes.PUSH_CONSTANT_ATTRIBUTE)
                builder.advanceLexer()
            }
            else if (SlangPsiUtil.nextTokenIs(builder, arrayListOf("shaderRecordNV", "shaderRecordEXT"))) {
                builder.remapCurrentToken(SlangTypes.SHADER_RECORD_ATTRIBUTE)
                builder.advanceLexer()
            }
            else if (nextTokenIs(builder, "constant_id")) {
                val idMarker = enter_section_(builder)
                builder.advanceLexer()
                result = consumeToken(builder, SlangTypes.ASSIGN_OP)
                result = result && consumeToken(builder, SlangTypes.INTEGER_LITERAL)
                exit_section_(builder, idMarker, SlangTypes.VK_CONSTANT_ID_ATTRIBUTE, result)
            }
            else if (nextTokenIs(builder, "std140")) {
                builder.remapCurrentToken(SlangTypes.GLSL_STD140_MODIFIER)
                builder.advanceLexer()
            }
            else if (nextTokenIs(builder, "std430")) {
                builder.remapCurrentToken(SlangTypes.GLSL_STD430_MODIFIER)
                builder.advanceLexer()
            }
            else if (nextTokenIs(builder, "scalar")) {
                builder.remapCurrentToken(SlangTypes.GLSL_SCALAR_MODIFIER)
                builder.advanceLexer()
            }
            else if (nextTokenIs(builder, "offset")) {
                val offsetMarker = enter_section_(builder)
                builder.advanceLexer()
                result = consumeToken(builder, SlangTypes.ASSIGN_OP)
                result = result && consumeToken(builder, SlangTypes.INTEGER_LITERAL)
                exit_section_(builder, offsetMarker, SlangTypes.GLSL_OFFSET_LAYOUT_ATTRIBUTE, result)
            }
            else if (nextTokenIs(builder, "location")) {
                val locationMarker = enter_section_(builder)
                builder.advanceLexer()
                result = consumeToken(builder, SlangTypes.ASSIGN_OP)
                result = result && consumeToken(builder, SlangTypes.INTEGER_LITERAL)
                exit_section_(builder, locationMarker, SlangTypes.GLSL_LOCATION_LAYOUT_ATTRIBUTE, result)
            }
            else if (nextTokenIs(builder, SlangTypes.IDENTIFIER)) {
                builder.remapCurrentToken(SlangTypes.GLSL_UNPARSED_ATTRIBUTE)
                builder.advanceLexer()
            }
            else
                result = false

            if (result && consumeToken(builder, SlangTypes.RIGHT_PAREN))
                break
            result = result && consumeToken(builder, SlangTypes.COMMA)
        }

        if (result && SlangPsiUtil.nextTokenIs(builder, arrayListOf("rayPayloadEXT", "rayPayloadNV"))) {
            builder.remapCurrentToken(SlangTypes.VULKAN_RAY_PAYLOAD_ATTRIBUTE)
            builder.advanceLexer()
        }
        else if (result && SlangPsiUtil.nextTokenIs(builder, arrayListOf("rayPayloadInEXT", "rayPayloadInNV"))) {
            builder.remapCurrentToken(SlangTypes.VULKAN_RAY_PAYLOAD_IN_ATTRIBUTE)
            builder.advanceLexer()
        }
        else if (result && nextTokenIs(builder, "hitObjectAttributeNV")) {
            builder.remapCurrentToken(SlangTypes.VULKAN_HIT_OBJECT_ATTRIBUTE)
            builder.advanceLexer()
        }
        else if (result && nextTokenIs(builder, "callableDataEXT")) {
            builder.remapCurrentToken(SlangTypes.VULKAN_CALLABLE_PAYLOAD_ATTRIBUTE)
            builder.advanceLexer()
        }
        else if (result && nextTokenIs(builder, "callableDataInEXT")) {
            builder.remapCurrentToken(SlangTypes.VULKAN_CALLABLE_PAYLOAD_IN_ATTRIBUTE)
            builder.advanceLexer()
        }

        exit_section_(builder, marker, SlangTypes.GLSL_LAYOUT_MODIFIER_GROUP, result)
        return result
    }

    private fun parseIntrinsicOpModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIntrinsicOpModifier"))
            return false

        // We allow a few difference forms here:
        //
        // First, we can specify the intrinsic op `enum` value directly:
        //
        //     __intrinsic_op(<integer literal>)
        //
        // Second, we can specify the operation by name:
        //
        //     __intrinsic_op(<identifier>)
        //
        // Finally, we can leave off the specification, so that the
        // op name will be derived from the function name:
        //
        //     __intrinsic_op
        //
        val marker = enter_section_(builder)
        // Skip '__intrinsic_op' keyword
        builder.advanceLexer()
        var result = true
        if (consumeToken(builder, SlangTypes.LEFT_PAREN)) {
            result = parseIROp(builder, level + 1)
            result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        }
        exit_section_(builder, marker, SlangTypes.INTRINSIC_OP_MODIFIER, result)
        return result
    }

    private fun parseTargetIntrinsicModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseTargetIntrinsicModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__target_intrinsic' keyword
        builder.advanceLexer()
        var result = true
        if (consumeToken(builder, SlangTypes.LEFT_PAREN)) {
            result = consumeToken(builder, SlangTypes.IDENTIFIER)
            if (result && consumeToken(builder, SlangTypes.COMMA)) {
                if (builder.lookAhead(1) == SlangTypes.LEFT_PAREN) {
                    result = consumeToken(builder, SlangTypes.IDENTIFIER)
                    if (result) builder.advanceLexer()
                    result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
                    result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
                    result = result && consumeToken(builder, SlangTypes.COMMA)
                }
                if (result && nextTokenIs(builder, SlangTypes.STRING_LITERAL)) {
                    @Suppress("ControlFlowWithEmptyBody")
                    while (consumeToken(builder, SlangTypes.STRING_LITERAL)) {}
                }
                else {
                    result = consumeToken(builder, SlangTypes.IDENTIFIER)
                }
            }
            result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        }
        exit_section_(builder, marker, SlangTypes.TARGET_INTRINSIC_MODIFIER, result)
        return result
    }

    private fun parseSpecializedForTargetModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseSpecializedForTargetModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__specialized_for_target' keyword
        builder.advanceLexer()
        var result = true
        if (consumeToken(builder, SlangTypes.LEFT_PAREN)) {
            result = consumeToken(builder, SlangTypes.IDENTIFIER)
            result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        }
        exit_section_(builder, marker, SlangTypes.SPECIALIZED_FOR_TARGET_MODIFIER, result)
        return result
    }

    private fun parseGLSLExtensionModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGLSLExtensionModifier"))
        return false

        val marker = enter_section_(builder)
        // Skip '__glsl_extension' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.REQUIRED_GLSL_EXTENSION_MODIFIER, result)
        return result
    }

    private fun parseGLSLVersionModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGLSLVersionModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__glsl_version' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && consumeToken(builder, SlangTypes.INTEGER_LITERAL)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.REQUIRED_GLSL_VERSION_MODIFIER, result)
        return result
    }

    private fun parseSPIRVVersionModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGLSLVersionModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__spirv_version' keyword
        builder.advanceLexer()
        val result = parseSemanticVersion(builder)
        exit_section_(builder, marker, SlangTypes.REQUIRED_SPIRV_VERSION_MODIFIER, result)
        return result
    }

    private fun parseCUDASMVersionModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseGLSLVersionModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__cuda_sm_version' keyword
        builder.advanceLexer()
        val result = parseSemanticVersion(builder)
        exit_section_(builder, marker, SlangTypes.REQUIRED_CUDASM_VERSION_MODIFIER, result)
        return result
    }

    private fun parseSemanticVersion(builder: PsiBuilder): Boolean {
        // We allow specified as major.minor or as a string (in quotes)
        if (nextTokenIs(builder, null, SlangTypes.FLOAT_LITERAL, SlangTypes.STRING_LITERAL)) {
            builder.advanceLexer()
            return true
        }
        return false
    }

    private fun parseBuiltinTypeModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseBuiltinTypeModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__builtin_type' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.BUILTIN_TYPE_MODIFIER, result)
        return result
    }

    private fun parseBuiltinRequirementModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseBuiltinTypeModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__builtin_requirement' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.BUILTIN_REQUIREMENT_MODIFIER, result)
        return result
    }

    private fun parseMagicTypeModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseMagicTypeModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__magic_type' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
        if (consumeToken(builder, SlangTypes.COMMA))
            result = consumeToken(builder, SlangTypes.INTEGER_LITERAL)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        // TODO: Check if valid magic class name, slang-parser.cpp:8526
        exit_section_(builder, marker, SlangTypes.MAGIC_TYPE_MODIFIER, result)
        return result
    }

    private fun parseIntrinsicTypeModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIntrinsicTypeModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__intrinsic_type' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseIROp(builder, level + 1)
        while (result && consumeToken(builder, SlangTypes.COMMA))
            result = consumeToken(builder, SlangTypes.INTEGER_LITERAL)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.INTRINSIC_TYPE_MODIFIER, result)
        return result
    }

    private fun parseIROp(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseIntrinsicTypeModifier"))
            return false

        return if (consumeToken(builder, SlangTypes.SUB_OP)) // Optional sub op
            consumeToken(builder, SlangTypes.INTEGER_LITERAL)
        else if (consumeToken(builder, SlangTypes.INTEGER_LITERAL))
            true
        else {
            // TODO: Support IR, slang-parser.cpp:8042
            false
        }
    }

    private fun parseImplicitConversionModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseImplicitConversionModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__implicit_conversion' keyword
        builder.advanceLexer()
        var result = true
        if (consumeToken(builder, SlangTypes.LEFT_PAREN)) {
            result = consumeToken(builder, SlangTypes.INTEGER_LITERAL)
            if (result && consumeToken(builder, SlangTypes.COMMA))
                result = consumeToken(builder, SlangTypes.INTEGER_LITERAL)
            result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        }
        exit_section_(builder, marker, SlangTypes.IMPLICIT_CONVERSION_MODIFIER, result)
        return result
    }

    private fun parseAttributeTargetModifier(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseAttributeTargetModifier"))
            return false

        val marker = enter_section_(builder)
        // Skip '__attributeTarget' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && consumeToken(builder, SlangTypes.IDENTIFIER)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.ATTRIBUTE_TARGET_MODIFIER, result)
        return result
    }

    private fun parseThisExpr(builder: PsiBuilder, @Suppress("UNUSED_PARAMETER") level: Int): Boolean {
        builder.remapCurrentToken(SlangTypes.THIS_EXPRESSION)
        builder.advanceLexer()
        return true
    }

    private fun parseTrueExpr(builder: PsiBuilder, @Suppress("UNUSED_PARAMETER") level: Int): Boolean {
        builder.remapCurrentToken(SlangTypes.BOOL_LITERAL)
        builder.advanceLexer()
        return true
    }

    private fun parseFalseExpr(builder: PsiBuilder, @Suppress("UNUSED_PARAMETER") level: Int): Boolean {
        builder.remapCurrentToken(SlangTypes.BOOL_LITERAL)
        builder.advanceLexer()
        return true
    }

    private fun parseReturnValExpr(builder: PsiBuilder, @Suppress("UNUSED_PARAMETER") level: Int): Boolean {
        builder.remapCurrentToken(SlangTypes.RETURN_VAL_EXPRESSION)
        builder.advanceLexer()
        return true
    }

    private fun parseNullPtrExpr(builder: PsiBuilder, @Suppress("UNUSED_PARAMETER") level: Int): Boolean {
        builder.remapCurrentToken(SlangTypes.NULLPTR_EXPRESSION)
        builder.advanceLexer()
        return true
    }

    private fun parseNoneExpr(builder: PsiBuilder, @Suppress("UNUSED_PARAMETER") level: Int): Boolean {
        builder.remapCurrentToken(SlangTypes.NONE_EXPRESSION)
        builder.advanceLexer()
        return true
    }

    private fun parseTryExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseTryExpr")) 
            return false
        
        val marker = enter_section_(builder)
        // Skip 'try' keyword
        builder.advanceLexer()
        val result = parseLeafExpression(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.TRY_EXPRESSION, result)
        return result
    }
    
    private fun parseTreatAsDifferentiableExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseTreatAsDifferentiableExpr"))
            return false

        val marker = enter_section_(builder)
        // Skip 'no_diff' keyword
        builder.advanceLexer()
        val result = parseLeafExpression(builder, level + 1)
        exit_section_(builder, marker, SlangTypes.TREAT_AS_DIFFERENTIABLE_EXPRESSION, result)
        return result
    }

    private fun parseForwardDifferentiate(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseForwardDifferentiate"))
            return false

        val marker = enter_section_(builder)
        // Skip keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.FORWARD_DIFFERENTIATE_EXPRESSION, result)
        return result
    }

    private fun parseBackwardDifferentiate(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseBackwardDifferentiate"))
            return false

        val marker = enter_section_(builder)
        // Skip keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.BACKWARD_DIFFERENTIATE_EXPRESSION, result)
        return result
    }

    private fun parseDispatchKernel(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseDispatchKernel"))
            return false

        val marker = enter_section_(builder)
        // Skip keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseArgExpr(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.COMMA)
        result = result && parseArgExpr(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.COMMA)
        result = result && parseArgExpr(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.DISPATCH_KERNEL_EXPRESSION, result)
        return result
    }

    private fun parseSizeOfExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseSizeOfExpr"))
            return false

        val marker = enter_section_(builder)
        // Skip 'sizeof' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.SIZE_OF_EXPRESSION, result)
        return result
    }

    private fun parseAlignOfExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseAlignOfExpr"))
            return false

        val marker = enter_section_(builder)
        // Skip 'sizeof' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.ALIGN_OF_EXPRESSION, result)
        return result
    }

    private fun parseCountOfExpr(builder: PsiBuilder, level: Int): Boolean {
        if (!recursion_guard_(builder, level, "parseCountOfExpr"))
            return false

        val marker = enter_section_(builder)
        // Skip 'sizeof' keyword
        builder.advanceLexer()
        var result = consumeToken(builder, SlangTypes.LEFT_PAREN)
        result = result && parseExpression(builder, level + 1)
        result = result && consumeToken(builder, SlangTypes.RIGHT_PAREN)
        exit_section_(builder, marker, SlangTypes.COUNT_OF_EXPRESSION, result)
        return result
    }
}