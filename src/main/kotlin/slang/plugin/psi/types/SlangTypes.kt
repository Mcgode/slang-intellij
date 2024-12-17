package slang.plugin.psi.types

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import slang.plugin.psi.SlangElementType
import slang.plugin.psi.SlangTokenType
import slang.plugin.psi.types.impl.*

object SlangTypes {
    val ALIGN_OF_EXPRESSION = SlangElementType("ALIGN_OF_EXPRESSION")
    val AND_TYPE_EXPRESSION = SlangElementType("AND_TYPE_EXPRESSION")
    val ARRAY_DECLARATOR = SlangElementType("ARRAY_DECLARATOR")
    val ARRAY_SPECIFIER = SlangElementType("ARRAY_SPECIFIER")
    val ASSIGN_EXPRESSION = SlangElementType("ASSIGN_EXPRESSION")
    val AS_TYPE_EXPRESSION = SlangElementType("AS_TYPE_EXPRESSION")
    val ATTRIBUTE_TARGET_MODIFIER = SlangElementType("ATTRIBUTE_TARGET_MODIFIER")
    val BACKWARD_DIFFERENTIATE_EXPRESSION = SlangElementType("BACKWARD_DIFFERENTIATE_EXPRESSION")
    val BITFIELD_MODIFIER = SlangElementType("BITFIELD_MODIFIER")
    val BLOCK_STATEMENT = SlangElementType("BLOCK_STATEMENT")
    val BODY_DECL = SlangElementType("BODY_DECL")
    val BREAK_STATEMENT = SlangElementType("BREAK_STATEMENT")
    val BUILTIN_REQUIREMENT_MODIFIER = SlangElementType("BUILTIN_REQUIREMENT_MODIFIER")
    val BUILTIN_TYPE_MODIFIER = SlangElementType("BUILTIN_TYPE_MODIFIER")
    val CASE_STATEMENT = SlangElementType("CASE_STATEMENT")
    val CLASS_DECLARATION = SlangElementType("CLASS_DECLARATION")
    val CLASS_NAME = SlangElementType("CLASS_NAME")
    val COMPILE_TIME_FOR_STATEMENT = SlangElementType("COMPILE_TIME_FOR_STATEMENT")
    val CONTINUE_STATEMENT = SlangElementType("CONTINUE_STATEMENT")
    val COUNT_OF_EXPRESSION = SlangElementType("COUNT_OF_EXPRESSION")
    val DECLARATION = SlangElementType("DECLARATION")
    val DECLARATION_NAME = SlangElementType("DECLARATION_NAME")
    val DECLARATION_STATEMENT = SlangElementType("DECLARATION_STATEMENT")
    val DEFAULT_STATEMENT = SlangElementType("DEFAULT_STATEMENT")
    val DISCARD_STATEMENT = SlangElementType("DISCARD_STATEMENT")
    val DISPATCH_KERNEL_EXPRESSION = SlangElementType("DISPATCH_KERNEL_EXPRESSION")
    val DO_WHILE_STATEMENT = SlangElementType("DO_WHILE_STATEMENT")
    val EACH_EXPRESSION = SlangElementType("EACH_EXPRESSION")
    val ENUM_CASE_DECLARATION = SlangElementType("ENUM_CASE_DECLARATION")
    val ENUM_CASE_NAME = SlangElementType("ENUM_CASE_NAME")
    val ENUM_DECLARATION = SlangElementType("ENUM_DECLARATION")
    val ENUM_NAME = SlangElementType("ENUM_NAME")
    val EXPAND_EXPRESSION = SlangElementType("EXPAND_EXPRESSION")
    val DEREF_MEMBER_EXPRESSION = SlangElementType("DEREF_MEMBER_EXPRESSION")
    val EXPRESSION = SlangElementType("EXPRESSION")
    val EXPRESSION_STATEMENT = SlangElementType("EXPRESSION_STATEMENT")
    val FILE_DECLARATION = SlangElementType("FILE_DECLARATION")
    val FOR_STATEMENT = SlangElementType("FOR_STATEMENT")
    val FORWARD_DIFFERENTIATE_EXPRESSION = SlangElementType("FORWARD_DIFFERENTIATE_EXPRESSION")
    val FUNCTION_DECLARATION = SlangElementType("FUNCTION_DECLARATION")
    val FUNCTYPE_EXPRESSION = SlangElementType("FUNCTYPE_EXPRESSION")
    val GENERIC_APP_ARGUMENT = SlangElementType("GENERIC_APP_ARGUMENT")
    val GENERIC_APP_EXPRESSION = SlangElementType("GENERIC_APP_EXPRESSION")
    val GENERIC_DECLARATION = SlangElementType("GENERIC_DECLARATION")
    val GENERIC_PARAMETER_DECLARATION = SlangElementType("GENERIC_PARAMETER_DECLARATION")
    val GENERIC_TYPE_CONSTRAINT_DECLARATION = SlangElementType("GENERIC_TYPE_CONSTRAINT_DECLARATION")
    val GENERIC_TYPE_PACK_PARAMETER_DECLARATION = SlangElementType("GENERIC_TYPE_PACK_PARAMETER_DECLARATION")
    val GENERIC_TYPE_PARAMETER_DECLARATION = SlangElementType("GENERIC_TYPE_PARAMETER_DECLARATION")
    val GENERIC_VALUE_PARAMETER_DECLARATION = SlangElementType("GENERIC_VALUE_PARAMETER_DECLARATION")
    val GLSL_BINDING_ATTRIBUTE = SlangElementType("GLSL_BINDING_ATTRIBUTE")
    val GLSL_INPUT_ATTACHMENT_INDEX_LAYOUT_ATTRIBUTE = SlangElementType("GLSL_INPUT_ATTACHMENT_INDEX_LAYOUT_ATTRIBUTE")
    val GLSL_LAYOUT_LOCAL_SIZE_ATTRIBUTE = SlangElementType("GLSL_LAYOUT_LOCAL_SIZE_ATTRIBUTE")
    val GLSL_LAYOUT_MODIFIER_GROUP = SlangElementType("GLSL_LAYOUT_MODIFIER_GROUP")
    val GLSL_LOCATION_LAYOUT_ATTRIBUTE = SlangElementType("GL_LOCATION_LAYOUT_ATTRIBUTE")
    val GLSL_OFFSET_LAYOUT_ATTRIBUTE = SlangElementType("GL_OFFSET_LAYOUT_ATTRIBUTE")
    val GPU_FOREACH_STATEMENT = SlangElementType("GPU_FOREACH_STATEMENT")
    val HLSL_PACK_OFFSET_SEMANTIC = SlangElementType("HLSL_PACK_OFFSET_SEMANTIC")
    val HLSL_REGISTER_SEMANTIC = SlangElementType("HLSL_REGISTER_SEMANTIC")
    val HLSL_SIMPLE_SEMANTIC = SlangElementType("HLSL_SIMPLE_SEMANTIC")
    val IF_STATEMENT = SlangElementType("IF_STATEMENT")
    val IMPLICIT_CONVERSION_MODIFIER = SlangElementType("IMPLICIT_CONVERSION_MODIFIER")
    val INDEX_EXPRESSION = SlangElementType("INDEX_EXPRESSION")
    val INFIX_EXPRESSION = SlangElementType("INFIX_EXPRESSION")
    val INHERITANCE_DECLARATION = SlangElementType("INHERITANCE_DECLARATION")
    val INIT_DECLARATOR = SlangElementType("INIT_DECLARATOR")
    val INITIALIZER_LIST = SlangElementType("INITIALIZER_LIST")
    val INITIAL_STATEMENT = SlangElementType("INITIAL_STATEMENT")
    val INTRINSIC_ASM_STATEMENT = SlangElementType("INTRINSIC_ASM_STATEMENT")
    val INTRINSIC_OP_MODIFIER = SlangElementType("INTRINSIC_OP_MODIFIER")
    val INTRINSIC_TYPE_MODIFIER = SlangElementType("INTRINSIC_TYPE_MODIFIER")
    val INVOKE_EXPRESSION = SlangElementType("INVOKE_EXPRESSION")
    val IS_TYPE_EXPRESSION = SlangElementType("IS_TYPE_EXPRESSION")
    val LABEL_STATEMENT = SlangElementType("LABEL_STATEMENT")
    val LET_DECLARATION = SlangElementType("LET_DECLARATION")
    val MAGIC_TYPE_MODIFIER = SlangElementType("MAGIC_TYPE_MODIFIER")
    val MEMBER_EXPRESSION = SlangElementType("MEMBER_EXPRESSION")
    val MODIFIER_ARGUMENT = SlangElementType("MODIFIER_ARGUMENT")
    val NAME_DECLARATOR = SlangElementType("NAME_DECLARATOR")
    val NEW_EXPRESSION = SlangElementType("NEW_EXPRESSION")
    val OPERATOR = SlangElementType("OPERATOR")
    val PARAMETER_DECLARATION = SlangElementType("PARAMETER_DECLARATION")
    val POINTER_DECLARATOR = SlangElementType("POINTER_DECLARATOR")
    val POINTER_TYPE_EXPRESSION = SlangElementType("POINTER_TYPE_EXPRESSION")
    val POSTFIX_EXPRESSION = SlangElementType("POSTFIX_EXPRESSION")
    val PREDICATE_EXPRESSION = SlangElementType("PREDICATE_EXPRESSION")
    val PREFIX_EXPRESSION = SlangElementType("PREFIX_EXPRESSION")
    val RAY_PAYLOAD_READ_SEMANTIC = SlangElementType("RAY_PAYLOAD_READ_SEMANTIC")
    val RAY_PAYLOAD_WRITE_SEMANTIC = SlangElementType("RAY_PAYLOAD_WRITE_SEMANTIC")
    val REQUIRE_CAPABILITIES_DECLARATION = SlangElementType("REQUIRE_CAPABILITIES_DECLARATION")
    val REQUIRED_CUDASM_VERSION_MODIFIER = SlangElementType("REQUIRED_CUDASM_VERSION_MODIFIER")
    val REQUIRED_GLSL_EXTENSION_MODIFIER = SlangElementType("REQUIRED_GLSL_EXTENSION_MODIFIER")
    val REQUIRED_GLSL_VERSION_MODIFIER = SlangElementType("REQUIRED_GLSL_VERSION_MODIFIER")
    val REQUIRED_SPIRV_VERSION_MODIFIER = SlangElementType("REQUIRED_SPIRV_VERSION_MODIFIER")
    val RETURN_STATEMENT = SlangElementType("RETURN_STATEMENT")
    val SELECT_EXPRESSION = SlangElementType("SELECT_EXPRESSION")
    val SIDE_EFFECT_EXPRESSION = SlangElementType("SIDE_EFFECT_EXPRESSION")
    val SIZE_OF_EXPRESSION = SlangElementType("SIZE_OF_EXPRESSION")
    val SPECIALIZED_FOR_TARGET_MODIFIER = SlangElementType("SPECIALIZED_FOR_TARGET_MODIFIER")
    val SPIRV_ASM_EXPRESSION = SlangElementType("SPIRV_ASM_EXPRESSION")
    val SPIRV_ASM_INSTRUCTION = SlangElementType("SPIRV_ASM_INSTRUCTION")
    val STATEMENT = SlangElementType("STATEMENT")
    val STATIC_MEMBER_EXPRESSION = SlangElementType("STATIC_MEMBER_EXPRESSION")
    val STRUCT_DECLARATION = SlangElementType("STRUCT_DECLARATION")
    val STRUCT_NAME = SlangElementType("STRUCT_NAME")
    val SWITCH_STATEMENT = SlangElementType("SWITCH_STATEMENT")
    val TARGET_INTRINSIC_MODIFIER = SlangElementType("TARGET_INTRISIC_MODIFIER")
    val TARGET_SWITCH_STATEMENT = SlangElementType("TARGET_SWITCH_STATEMENT")
    val TREAT_AS_DIFFERENTIABLE_EXPRESSION = SlangElementType("TREAT_AS_DIFFERENTIABLE_EXPRESSION")
    val TRY_EXPRESSION = SlangElementType("TRY_EXPRESSION")
    val TYPEALIAS_DECLARATION = SlangElementType("TYPEALIAS_DECLARATION")
    val TYPEDEF_DECLARATION = SlangElementType("TYPEDEF_DECLARATION")
    val TYPE_EXPRESSION = SlangElementType("TYPE_EXPRESSION")
    val TYPE_MODIFIER = SlangElementType("TYPE_MODIFIER")
    val TYPE_SPEC = SlangElementType("TYPE_SPEC")
    val UNCHECKED_ATTRIBUTE = SlangElementType("UNCHECKED_ATTRIBUTE")
    val VARIABLE_DECL = SlangElementType("VARIABLE_DECL")
    val VARIABLE_EXPRESSION = SlangElementType("VARIABLE_EXPRESSION")
    val VK_CONSTANT_ID_ATTRIBUTE = SlangElementType("VK_CONSTANT_ID_ATTRIBUTE")
    val WHILE_STATEMENT = SlangElementType("WHILE_STATEMENT")

    val LINE_COMMENT = SlangElementType("LINE_COMMENT")
    val MULTILINE_COMMENT = SlangElementType("MULTILINE_COMMENT")
    val NEW_LINE = SlangElementType("NEW_LINE")

    val TRANSPARENT_BLOCK_DECLARATION = SlangElementType("TRANSPARENT_BLOCK_DECLARATION")

    val IN_MODIFIER = SlangElementType("IN_MODIFIER")
    val OUT_MODIFIER = SlangElementType("OUT_MODIFIER")
    val INOUT_MODIFIER = SlangElementType("INOUT_MODIFIER")
    val REF_MODIFIER = SlangElementType("REF_MODIFIER")
    val CONSTREF_MODIFIER = SlangElementType("CONSTREF_MODIFIER")
    val CONST_MODIFIER = SlangElementType("CONST_MODIFIER")
    val BUILTIN_MODIFIER = SlangElementType("BUILTIN_MODIFIER")
    val GLSL_PRECISION_MODIFIER = SlangElementType("GLSL_PRECISION_MODIFIER")
    val ACTUAL_GLOBAL_MODIFIER = SlangElementType("ACTUAL_GLOBAL_MODIFIER")
    val INLINE_MODIFIER = SlangElementType("INLINE_MODIFIER")
    val PUBLIC_MODIFIER = SlangElementType("PUBLIC_MODIFIER")
    val PRIVATE_MODIFIER = SlangElementType("PRIVATE_MODIFIER")
    val INTERNAL_MODIFIER = SlangElementType("INTERNAL_MODIFIER")
    val REQUIRE_MODIFIER = SlangElementType("REQUIRE_MODIFIER")
    val PARAM_MODIFIER = SlangElementType("PARAM_MODIFIER")
    val EXTERN_MODIFIER = SlangElementType("EXTERN_MODIFIER")
    val HLSL_ROW_MAJOR_LAYOUT_MODIFIER = SlangElementType("HLSL_ROW_MAJOR_LAYOUT_MODIFIER")
    val HLSL_COLUMN_MAJOR_LAYOUT_MODIFIER = SlangElementType("HLSL_COLUMN_MAJOR_LAYOUT_MODIFIER")
    val HLSL_NO_INTERPOLATION_MODIFIER = SlangElementType("HLSL_NO_INTERPOLATION_MODIFIER")
    val HLSL_NO_PERSPECTIVE_MODIFIER = SlangElementType("HLSL_NO_PERSPECTIVE_MODIFIER")
    val HLSL_LINEAR_MODIFIER = SlangElementType("HLSL_LINEAR_MODIFIER")
    val HLSL_SAMPLE_MODIFIER = SlangElementType("HLSL_SAMPLE_MODIFIER")
    val HLSL_CENTROID_MODIFIER = SlangElementType("HLSL_CENTROID_MODIFIER")
    val PRECISE_MODIFIER = SlangElementType("PRECISE_MODIFIER")
    val HLSL_EFFECT_SHARED_MODIFIER = SlangElementType("HLSL_EFFECT_SHARED_MODIFIER")
    val HLSL_GROUP_SHARED_MODIFIER = SlangElementType("HLSL_GROUP_SHARED_MODIFIER")
    val HLSL_STATIC_MODIFIER = SlangElementType("HLSL_STATIC_MODIFIER")
    val HLSL_UNIFORM_MODIFIER = SlangElementType("HLSL_UNIFORM_MODIFIER")
    val VOLATILE_MODIFIER = SlangElementType("VOLATILE_MODIFIER")
    val GLSL_COHERENT_MODIFIER = SlangElementType("GLSL_COHERENT_MODIFIER")
    val GLSL_RESTRICT_MODIFIER = SlangElementType("GLSL_RESTRICT_MODIFIER")
    val GLSL_READ_ONLY_MODIFIER = SlangElementType("GLSL_READ_ONLY_MODIFIER")
    val GLSL_WRITE_ONLY_MODIFIER = SlangElementType("GLSL_WRITE_ONLY_MODIFIER")
    val HLSL_EXPORT_MODIFIER = SlangElementType("HLSL_EXPORT_MODIFIER")
    val HLSL_DYNAMIC_UNIFORM_MODIFIER = SlangElementType("HLSL_DYNAMIC_UNIFORM_MODIFIER")
    val HLSL_POINT_MODIFIER = SlangElementType("HLSL_POINT_MODIFIER")
    val HLSL_LINE_MODIFIER = SlangElementType("HLSL_LINE_MODIFIER")
    val HLSL_TRIANGLE_MODIFIER = SlangElementType("HLSL_TRIANGLE_MODIFIER")
    val HLSL_LINE_ADJ_MODIFIER = SlangElementType("HLSL_LINE_ADJ_MODIFIER")
    val HLSL_TRIANGLE_ADJ_MODIFIER = SlangElementType("HLSL_TRIANGLE_ADJ_MODIFIER")
    val HLSL_VERTICES_MODIFIER = SlangElementType("HLSL_VERTICES_MODIFIER")
    val HLSL_INDICES_MODIFIER = SlangElementType("HLSL_INDICES_MODIFIER")
    val HLSL_PRIMITIVES_MODIFIER = SlangElementType("HLSL_PRIMITIVES_MODIFIER")
    val HLSL_PAYLOAD_MODIFIER = SlangElementType("HLSL_PAYLOAD_MODIFIER")
    val PREFIX_MODIFIER = SlangElementType("PREFIX_MODIFIER")
    val POSTFIX_MODIFIER = SlangElementType("POSTFIX_MODIFIER")
    val EXPORTED_MODIFIER = SlangElementType("EXPORTED_MODIFIER")
    val VULKAN_HIT_ATTRIBUTES_MODIFIER = SlangElementType("VULKAN_HIT_ATTRIBUTES_MODIFIER")
    val GLSL_STD140_MODIFIER = SlangElementType("GLSL_STD140_MODIFIER")
    val GLSL_STD430_MODIFIER = SlangElementType("GLSL_STD430_MODIFIER")
    val GLSL_SCALAR_MODIFIER = SlangElementType("GLSL_SCALAR_MODIFIER")

    val THIS_EXPRESSION = SlangElementType("THIS_EXPRESSION")
    val BOOL_LITERAL = SlangElementType("BOOL_LITERAL")
    val RETURN_VAL_EXPRESSION = SlangElementType("RETURN_VAL_EXPRESSION")
    val NULLPTR_EXPRESSION = SlangElementType("NULLPTR_EXPRESSION")
    val NONE_EXPRESSION = SlangElementType("NONE_EXPRESSION")

    val FORMAT_ATTRIBUTE = SlangElementType("FORMAT_ATTRIBUTE")
    val GLSL_LAYOUT_DERIVATIVE_GROUP_LINEAR_ATTRIBUTE = SlangElementType("GLSL_LAYOUT_DERIVATIVE_GROUP_LINEAR_ATTRIBUTE")
    val GLSL_LAYOUT_DERIVATIVE_GROUP_QUAD_ATTRIBUTE = SlangElementType("GLSL_LAYOUT_DERIVATIVE_GROUP_QUAD_ATTRIBUTE")
    val GLSL_UNPARSED_ATTRIBUTE = SlangElementType("GLSL_UNPARSED_ATTRIBUTE")
    val PUSH_CONSTANT_ATTRIBUTE = SlangElementType("PUSH_CONSTANT_ATTRIBUTE")
    val SHADER_RECORD_ATTRIBUTE = SlangElementType("SHADER_RECORD_ATTRIBUTE")
    val VULKAN_CALLABLE_PAYLOAD_ATTRIBUTE = SlangElementType("VULKAN_CALLABLE_PAYLOAD_ATTRIBUTE")
    val VULKAN_CALLABLE_PAYLOAD_IN_ATTRIBUTE = SlangElementType("VULKAN_CALLABLE_PAYLOAD_IN_ATTRIBUTE")
    val VULKAN_HIT_OBJECT_ATTRIBUTE = SlangElementType("VULKAN_HIT_OBJECT_ATTRIBUTE")
    val VULKAN_RAY_PAYLOAD_ATTRIBUTE = SlangElementType("VULKAN_RAY_PAYLOAD_ATTRIBUTE")
    val VULKAN_RAY_PAYLOAD_IN_ATTRIBUTE = SlangElementType("VULKAN_RAY_PAYLOAD_IN_ATTRIBUTE")

    val LEFT_BRACE = SlangTokenType("{")
    val RIGHT_BRACE = SlangTokenType("}")
    val LEFT_PAREN = SlangTokenType("(")
    val RIGHT_PAREN = SlangTokenType(")")
    val LEFT_BRACKET = SlangTokenType("[")
    val RIGHT_BRACKET = SlangTokenType("]")

    val SEMICOLON = SlangTokenType(";")
    val COLON = SlangTokenType(":")
    val COMMA = SlangTokenType(",")
    val SCOPE = SlangTokenType("::")
    val COMPLETION_REQUEST = SlangTokenType("#?")
    val DOT = SlangTokenType(".")
    val RIGHT_ARROW = SlangTokenType("->")
    val QUESTION_MARK = SlangTokenType("?")
    val DOLLAR = SlangTokenType("$")

    val ADD_OP = SlangTokenType("+")
    val SUB_OP = SlangTokenType("-")
    val MUL_OP = SlangTokenType("*")
    val DIV_OP = SlangTokenType("/")
    val MOD_OP = SlangTokenType("%")
    val NOT_OP = SlangTokenType("!")
    val BIT_NOT_OP = SlangTokenType("~")
    val SHL_OP = SlangTokenType("<<")
    val SHR_OP = SlangTokenType(">>")
    val EQL_OP = SlangTokenType("==")
    val NEQ_OP = SlangTokenType("!=")
    val GREATER_OP = SlangTokenType(">")
    val LESS_OP = SlangTokenType("<")
    val GEQ_OP = SlangTokenType(">=")
    val LEQ_OP = SlangTokenType("<=")
    val AND_OP = SlangTokenType("&&")
    val OR_OP = SlangTokenType("||")
    val BIT_AND_OP = SlangTokenType("&")
    val BIT_OR_OP = SlangTokenType("|")
    val BIT_XOR_OP = SlangTokenType("^")
    val INC_OP = SlangTokenType("++")
    val DEC_OP = SlangTokenType("--")
    val ASSIGN_OP = SlangTokenType("=")

    val ADD_ASSIGN_OP = SlangTokenType("+=")
    val SUB_ASSIGN_OP = SlangTokenType("-=")
    val MUL_ASSIGN_OP = SlangTokenType("*=")
    val DIV_ASSIGN_OP = SlangTokenType("/=")
    val MOD_ASSIGN_OP = SlangTokenType("%=")
    val SHL_ASSIGN_OP = SlangTokenType("<<=")
    val SHR_ASSIGN_OP = SlangTokenType(">>=")
    val OR_ASSIGN_OP = SlangTokenType("|=")
    val AND_ASSIGN_OP = SlangTokenType("&=")
    val XOR_ASSIGN_OP = SlangTokenType("^=")

    val INTEGER_LITERAL = SlangTokenType("INTEGER_LITERAL")
    val FLOAT_LITERAL = SlangTokenType("FLOAT_LITERAL")
    val STRING_LITERAL = SlangTokenType("STRING_LITERAL")

    val IDENTIFIER = SlangTokenType("IDENTIFIER")

    class Factory {
        companion object {
            fun createElement(node: ASTNode?): PsiElement {
                when (node?.elementType) {
                    ALIGN_OF_EXPRESSION -> return SlangAlignOfExpressionImpl(node)
                    AND_TYPE_EXPRESSION -> return SlangAndTypeExpressionImpl(node)
                    ARRAY_DECLARATOR -> return SlangArrayDeclaratorImpl(node)
                    ARRAY_SPECIFIER -> return SlangArraySpecifierImpl(node)
                    ASSIGN_EXPRESSION -> return SlangAssignExpressionImpl(node)
                    AS_TYPE_EXPRESSION -> return SlangAsTypeExpressionImpl(node)
                    ATTRIBUTE_TARGET_MODIFIER -> SlangAttributeTargetModifierImpl(node)
                    BACKWARD_DIFFERENTIATE_EXPRESSION -> return SlangBackwardDifferentiateExpressionImpl(node)
                    BITFIELD_MODIFIER -> return SlangBitfieldModifierImpl(node)
                    BLOCK_STATEMENT -> return SlangBlockStatementImpl(node)
                    BODY_DECL -> return SlangBodyDeclImpl(node)
                    BREAK_STATEMENT -> return SlangBreakStatementImpl(node)
                    BUILTIN_REQUIREMENT_MODIFIER -> return SlangBuiltinRequirementModifierImpl(node)
                    BUILTIN_TYPE_MODIFIER -> return SlangBuiltinTypeModifierImpl(node)
                    CASE_STATEMENT -> return SlangCaseStatementImpl(node)
                    CLASS_DECLARATION -> return SlangClassDeclarationImpl(node)
                    CLASS_NAME -> return SlangClassNameImpl(node)
                    COMPILE_TIME_FOR_STATEMENT -> return SlangCompileTimeForStatementImpl(node)
                    CONTINUE_STATEMENT -> return SlangContinueStatementImpl(node)
                    COUNT_OF_EXPRESSION -> return SlangCountOfExpressionImpl(node)
                    DECLARATION -> return SlangDeclarationImpl(node)
                    DECLARATION_NAME -> return SlangDeclarationNameImpl(node)
                    DECLARATION_STATEMENT -> return SlangDeclarationStatementImpl(node)
                    DEFAULT_STATEMENT -> return SlangDefaultStatementImpl(node)
                    DEREF_MEMBER_EXPRESSION -> return SlangDerefMemberExpressionImpl(node)
                    DISCARD_STATEMENT -> return SlangDiscardStatementImpl(node)
                    DISPATCH_KERNEL_EXPRESSION -> return SlangDispatchKernelExpressionImpl(node)
                    DO_WHILE_STATEMENT -> return SlangDoWhileStatementImpl(node)
                    EACH_EXPRESSION -> return SlangEachExpressionImpl(node)
                    ENUM_CASE_DECLARATION -> return SlangEnumCaseDeclarationImpl(node)
                    ENUM_CASE_NAME -> return SlangEnumCaseNameImpl(node)
                    ENUM_DECLARATION -> return SlangEnumDeclarationImpl(node)
                    ENUM_NAME -> return SlangEnumNameImpl(node)
                    EXPAND_EXPRESSION -> return SlangExpandExpressionImpl(node)
                    EXPRESSION -> return SlangExpressionImpl(node)
                    EXPRESSION_STATEMENT -> return SlangExpressionStatementImpl(node)
                    FILE_DECLARATION -> return SlangFileDeclarationImpl(node)
                    FOR_STATEMENT -> return SlangForStatementImpl(node)
                    FORWARD_DIFFERENTIATE_EXPRESSION -> return SlangForwardDifferentiateExpressionImpl(node)
                    FUNCTION_DECLARATION -> return SlangFunctionDeclarationImpl(node)
                    FUNCTYPE_EXPRESSION -> return SlangFuncTypeExpressionImpl(node)
                    GENERIC_APP_ARGUMENT -> return SlangGenericAppArgumentImpl(node)
                    GENERIC_APP_EXPRESSION -> return SlangGenericAppExpressionImpl(node)
                    GENERIC_DECLARATION -> return SlangGenericDeclarationImpl(node)
                    GENERIC_PARAMETER_DECLARATION -> return SlangGenericParameterDeclarationImpl(node)
                    GENERIC_TYPE_CONSTRAINT_DECLARATION -> return SlangGenericTypeConstraintDeclarationImpl(node)
                    GENERIC_TYPE_PACK_PARAMETER_DECLARATION -> return SlangGenericTypePackParameterDeclarationImpl(node)
                    GENERIC_TYPE_PARAMETER_DECLARATION -> return SlangGenericTypeParameterDeclarationImpl(node)
                    GENERIC_VALUE_PARAMETER_DECLARATION -> return SlangGenericValueParameterDeclarationImpl(node)
                    GLSL_BINDING_ATTRIBUTE -> return SlangGlslBindingAttributeImpl(node)
                    GLSL_INPUT_ATTACHMENT_INDEX_LAYOUT_ATTRIBUTE -> return SlangGlslInputAttachmentIndexLayoutAttributeImpl(node)
                    GLSL_LAYOUT_LOCAL_SIZE_ATTRIBUTE -> return SlangGlslLayoutLocalSizeAttributeImpl(node)
                    GLSL_LAYOUT_MODIFIER_GROUP -> return SlangGlslLayoutModifierGroupImpl(node)
                    GLSL_LOCATION_LAYOUT_ATTRIBUTE -> return SlangGlslLocationLayoutAttributeImpl(node)
                    GLSL_OFFSET_LAYOUT_ATTRIBUTE -> return SlangGlslOffsetLayoutAttributeImpl(node)
                    GPU_FOREACH_STATEMENT -> return SlangGpuForeachStatementImpl(node)
                    HLSL_PACK_OFFSET_SEMANTIC -> return SlangHlslPackOffsetSemanticImpl(node)
                    HLSL_REGISTER_SEMANTIC -> return SlangHlslRegisterSemanticImpl(node)
                    HLSL_SIMPLE_SEMANTIC -> return SlangHlslSimpleSemanticImpl(node)
                    IF_STATEMENT -> return SlangIfStatementImpl(node)
                    IMPLICIT_CONVERSION_MODIFIER -> return SlangImplicitConversionModifierImpl(node)
                    INDEX_EXPRESSION -> return SlangIndexExpressionImpl(node)
                    INFIX_EXPRESSION -> return SlangInfixExpressionImpl(node)
                    INHERITANCE_DECLARATION -> return SlangInheritanceDeclarationImpl(node)
                    INIT_DECLARATOR -> return SlangInitDeclaratorImpl(node)
                    INITIALIZER_LIST -> return SlangInitializerListImpl(node)
                    INITIAL_STATEMENT -> return SlangInitialStatementImpl(node)
                    INTRINSIC_ASM_STATEMENT -> return SlangIntrinsicAsmStatementImpl(node)
                    INTRINSIC_OP_MODIFIER -> return SlangIntrinsicOpModifierImpl(node)
                    INTRINSIC_TYPE_MODIFIER -> return SlangIntrinsicTypeModifierImpl(node)
                    INVOKE_EXPRESSION -> return SlangInvokeExpressionImpl(node)
                    IS_TYPE_EXPRESSION -> return SlangIsTypeExpressionImpl(node)
                    LABEL_STATEMENT -> return SlangLabelStatementImpl(node)
                    LET_DECLARATION -> return SlangLetDeclarationImpl(node)
                    MAGIC_TYPE_MODIFIER -> return SlangMagicTypeModifierImpl(node)
                    MEMBER_EXPRESSION -> return SlangMemberExpressionImpl(node)
                    MODIFIER_ARGUMENT -> return SlangModifierArgumentImpl(node)
                    NAME_DECLARATOR -> return SlangNameDeclaratorImpl(node)
                    NEW_EXPRESSION -> return SlangNewExpressionImpl(node)
                    OPERATOR -> return SlangOperatorImpl(node)
                    PARAMETER_DECLARATION -> return SlangParameterDeclarationImpl(node)
                    POINTER_DECLARATOR -> return SlangPointerDeclaratorImpl(node)
                    POINTER_TYPE_EXPRESSION -> return SlangPointerTypeExpressionImpl(node)
                    POSTFIX_EXPRESSION -> return SlangPostfixExpressionImpl(node)
                    PREDICATE_EXPRESSION -> return SlangPredicateExpressionImpl(node)
                    PREFIX_EXPRESSION -> return SlangPrefixExpressionImpl(node)
                    RAY_PAYLOAD_READ_SEMANTIC -> return SlangRayPayloadReadSemanticImpl(node)
                    RAY_PAYLOAD_WRITE_SEMANTIC -> return SlangRayPayloadWriteSemanticImpl(node)
                    REQUIRE_CAPABILITIES_DECLARATION -> return SlangRequireCapabilityDeclarationImpl(node)
                    REQUIRED_CUDASM_VERSION_MODIFIER -> return SlangRequiredCudAsmVersionModifierImpl(node)
                    REQUIRED_GLSL_EXTENSION_MODIFIER -> return SlangRequiredGlslExtensionModifierImpl(node)
                    REQUIRED_GLSL_VERSION_MODIFIER -> return SlangRequiredGlslVersionModifierImpl(node)
                    REQUIRED_SPIRV_VERSION_MODIFIER -> return SlangRequiredSpirvVersionModifierImpl(node)
                    RETURN_STATEMENT -> return SlangReturnStatementImpl(node)
                    SELECT_EXPRESSION -> return SlangSelectExpressionImpl(node)
                    SIDE_EFFECT_EXPRESSION -> return SlangSideEffectExpressionImpl(node)
                    SIZE_OF_EXPRESSION -> return SlangSizeOfExpressionImpl(node)
                    SPECIALIZED_FOR_TARGET_MODIFIER -> return SlangSpecializedForTargetModifierImpl(node)
                    SPIRV_ASM_EXPRESSION -> return SlangSpirVAsmExpressionImpl(node)
                    SPIRV_ASM_INSTRUCTION -> return SlangSpirVAsmInstructionImpl(node)
                    STATEMENT -> return SlangStatementImpl(node)
                    STATIC_MEMBER_EXPRESSION -> return SlangStaticMemberExpressionImpl(node)
                    STRUCT_DECLARATION -> return SlangStructDeclarationImpl(node)
                    STRUCT_NAME -> return SlangStructNameImpl(node)
                    SWITCH_STATEMENT -> return SlangSwitchStatementImpl(node)
                    TARGET_INTRINSIC_MODIFIER -> return SlangTargetIntrinsicModifierImpl(node)
                    TARGET_SWITCH_STATEMENT -> return SlangTargetSwitchStatementImpl(node)
                    TREAT_AS_DIFFERENTIABLE_EXPRESSION -> return SlangTreasAsDifferentiableExpressionImpl(node)
                    TRY_EXPRESSION -> return SlangTryExpressionImpl(node)
                    TYPEALIAS_DECLARATION -> return SlangTypealiasDeclarationImpl(node)
                    TYPEDEF_DECLARATION -> return SlangTypedefDeclarationImpl(node)
                    TYPE_EXPRESSION -> return SlangTypeExpressionImpl(node)
                    TYPE_MODIFIER -> return SlangTypeModifierImpl(node)
                    TYPE_SPEC -> return SlangTypeSpecImpl(node)
                    UNCHECKED_ATTRIBUTE -> return SlangUncheckedAttribute(node)
                    VARIABLE_DECL -> return SlangVariableDeclImpl(node)
                    VARIABLE_EXPRESSION -> return SlangVariableExpressionImpl(node)
                    VK_CONSTANT_ID_ATTRIBUTE -> return SlangVkConstantIdAttributeImpl(node)
                    WHILE_STATEMENT -> return SlangWhileStatementImpl(node)
                }
                throw AssertionError("Unknown element type: ${node?.elementType}")
            }
        }
    }
}