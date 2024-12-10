package slang.plugin.psi.types

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import slang.plugin.psi.SlangElementType
import slang.plugin.psi.SlangTokenType
import slang.plugin.psi.types.impl.*

object SlangTypes {
    val AND_TYPE_EXPRESSION = SlangElementType("AND_TYPE_EXPRESSION")
    val ARRAY_DECLARATOR = SlangElementType("ARRAY_DECLARATOR")
    val ARRAY_SPECIFIER = SlangElementType("ARRAY_SPECIFIER")
    val ASSIGN_EXPRESSION = SlangElementType("ASSIGN_EXPRESSION")
    val AS_TYPE_EXPRESSION = SlangElementType("AS_TYPE_EXPRESSION")
    val BITFIELD_MODIFIER = SlangElementType("BITFIELD_MODIFIER")
    val BLOCK_STATEMENT = SlangElementType("BLOCK_STATEMENT")
    val BODY_DECL = SlangElementType("BODY_DECL")
    val CLASS_DECLARATION = SlangElementType("CLASS_DECLARATION")
    val CLASS_NAME = SlangElementType("CLASS_NAME")
    val DECLARATION = SlangElementType("DECLARATION")
    val DECLARATION_NAME = SlangElementType("DECLARATION_NAME")
    val ENUM_CASE_DECLARATION = SlangElementType("ENUM_CASE_DECLARATION")
    val ENUM_CASE_NAME = SlangElementType("ENUM_CASE_NAME")
    val ENUM_DECLARATION = SlangElementType("ENUM_DECLARATION")
    val ENUM_NAME = SlangElementType("ENUM_NAME")
    val DEREF_MEMBER_EXPRESSION = SlangElementType("DEREF_MEMBER_EXPRESSION")
    val EXPRESSION = SlangElementType("EXPRESSION")
    val FUNCTION_DECLARATION = SlangElementType("FUNCTION_DECLARATION")
    val HLSL_SIMPLE_SEMANTIC = SlangElementType("HLSL_SIMPLE_SEMANTIC")
    val IF_STATEMENT = SlangElementType("IF_STATEMENT")
    val INDEX_EXPRESSION = SlangElementType("INDEX_EXPRESSION")
    val INFIX_EXPRESSION = SlangElementType("INFIX_EXPRESSION")
    val INHERITANCE_DECLARATION = SlangElementType("INHERITANCE_DECLARATION")
    val INIT_DECLARATOR = SlangElementType("INIT_DECLARATOR")
    val INITIALIZER_LIST = SlangElementType("INITIALIZER_LIST")
    val INVOKE_EXPRESSION = SlangElementType("INVOKE_EXPRESSION")
    val IS_TYPE_EXPRESSION = SlangElementType("IS_TYPE_EXPRESSION")
    val LET_DECLARATION = SlangElementType("LET_DECLARATION")
    val MEMBER_EXPRESSION = SlangElementType("MEMBER_EXPRESSION")
    val MODIFIER_ARGUMENT = SlangElementType("MODIFIER_ARGUMENT")
    val NAME_DECLARATOR = SlangElementType("NAME_DECLARATOR")
    val OPERATOR = SlangElementType("OPERATOR")
    val PARAMETER_DECLARATION = SlangElementType("PARAMETER_DECLARATION")
    val POINTER_DECLARATOR = SlangElementType("POINTER_DECLARATOR")
    val POINTER_TYPE_EXPRESSION = SlangElementType("POINTER_TYPE_EXPRESSION")
    val POSTFIX_EXPRESSION = SlangElementType("POSTFIX_EXPRESSION")
    val SELECT_EXPRESSION = SlangElementType("SELECT_EXPRESSION")
    val STATEMENT = SlangElementType("STATEMENT")
    val STATIC_MEMBER_EXPRESSION = SlangElementType("STATIC_MEMBER_EXPRESSION")
    val STRUCT_DECLARATION = SlangElementType("STRUCT_DECLARATION")
    val STRUCT_NAME = SlangElementType("STRUCT_NAME")
    val TYPE_EXPRESSION = SlangElementType("TYPE_EXPRESSION")
    val TYPE_MODIFIER = SlangElementType("TYPE_MODIFIER")
    val TYPE_SPEC = SlangElementType("TYPE_SPEC")
    val UNCHECKED_ATTRIBUTE = SlangElementType("UNCHECKED_ATTRIBUTE")
    val VARIABLE_DECL = SlangElementType("VARIABLE_DECL")
    val VARIABLE_EXPRESSION = SlangElementType("VARIABLE_EXPRESSION")

    val LINE_COMMENT = SlangElementType("LINE_COMMENT")
    val MULTILINE_COMMENT = SlangElementType("MULTILINE_COMMENT")

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
                    AND_TYPE_EXPRESSION -> return SlangAndTypeExpressionImpl(node)
                    ARRAY_DECLARATOR -> return SlangArrayDeclaratorImpl(node)
                    ARRAY_SPECIFIER -> return SlangArraySpecifierImpl(node)
                    ASSIGN_EXPRESSION -> return SlangAssignExpressionImpl(node)
                    AS_TYPE_EXPRESSION -> return SlangAsTypeExpressionImpl(node)
                    BITFIELD_MODIFIER -> return SlangBitfieldModifierImpl(node)
                    BLOCK_STATEMENT -> return SlangBlockStatementImpl(node)
                    BODY_DECL -> return SlangBodyDeclImpl(node)
                    CLASS_DECLARATION -> return SlangClassDeclarationImpl(node)
                    CLASS_NAME -> return SlangClassNameImpl(node)
                    DECLARATION -> return SlangDeclarationImpl(node)
                    DECLARATION_NAME -> return SlangDeclarationNameImpl(node)
                    DEREF_MEMBER_EXPRESSION -> return SlangDerefMemberExpressionImpl(node)
                    ENUM_CASE_DECLARATION -> return SlangEnumCaseDeclarationImpl(node)
                    ENUM_CASE_NAME -> return SlangEnumCaseNameImpl(node)
                    ENUM_DECLARATION -> return SlangEnumDeclarationImpl(node)
                    ENUM_NAME -> return SlangEnumNameImpl(node)
                    EXPRESSION -> return SlangExpressionImpl(node)
                    FUNCTION_DECLARATION -> return SlangFunctionDeclarationImpl(node)
                    HLSL_SIMPLE_SEMANTIC -> return SlangHlslSimpleSemanticImpl(node)
                    IF_STATEMENT -> return SlangIfStatementImpl(node)
                    INDEX_EXPRESSION -> return SlangIndexExpressionImpl(node)
                    INFIX_EXPRESSION -> return SlangInfixExpressionImpl(node)
                    INHERITANCE_DECLARATION -> return SlangInheritanceDeclarationImpl(node)
                    INIT_DECLARATOR -> return SlangInitDeclaratorImpl(node)
                    INITIALIZER_LIST -> return SlangInitializerListImpl(node)
                    INVOKE_EXPRESSION -> return SlangInvokeExpressionImpl(node)
                    IS_TYPE_EXPRESSION -> return SlangIsTypeExpressionImpl(node)
                    LET_DECLARATION -> return SlangLetDeclarationImpl(node)
                    MEMBER_EXPRESSION -> return SlangMemberExpressionImpl(node)
                    MODIFIER_ARGUMENT -> return SlangModifierArgumentImpl(node)
                    NAME_DECLARATOR -> return SlangNameDeclaratorImpl(node)
                    OPERATOR -> return SlangOperatorImpl(node)
                    PARAMETER_DECLARATION -> return SlangParameterDeclarationImpl(node)
                    POINTER_DECLARATOR -> return SlangPointerDeclaratorImpl(node)
                    POINTER_TYPE_EXPRESSION -> return SlangPointerTypeExpressionImpl(node)
                    POSTFIX_EXPRESSION -> return SlangPostfixExpressionImpl(node)
                    SELECT_EXPRESSION -> return SlangSelectExpressionImpl(node)
                    STATEMENT -> return SlangStatementImpl(node)
                    STATIC_MEMBER_EXPRESSION -> return SlangStaticMemberExpressionImpl(node)
                    STRUCT_DECLARATION -> return SlangStructDeclarationImpl(node)
                    STRUCT_NAME -> return SlangStructNameImpl(node)
                    TYPE_EXPRESSION -> return SlangTypeExpressionImpl(node)
                    TYPE_MODIFIER -> return SlangTypeModifierImpl(node)
                    TYPE_SPEC -> return SlangTypeSpecImpl(node)
                    UNCHECKED_ATTRIBUTE -> return SlangUncheckedAttribute(node)
                    VARIABLE_DECL -> return SlangVariableDeclImpl(node)
                    VARIABLE_EXPRESSION -> return SlangVariableExpressionImpl(node)
                }
                throw AssertionError("Unknown element type: ${node?.elementType}")
            }
        }
    }
}