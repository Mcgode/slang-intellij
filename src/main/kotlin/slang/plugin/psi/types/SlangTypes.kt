package slang.plugin.psi.types

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import slang.plugin.psi.SlangElementType
import slang.plugin.psi.SlangTokenType
import slang.plugin.psi.types.impl.*

object SlangTypes {
    val TYPE_SPEC = SlangElementType("TYPE_SPEC")
    val VARIABLE_DECL = SlangElementType("VARIABLE_DECL")
    val INIT_DECLARATOR = SlangElementType("INIT_DECLARATOR")
    val POINTER_DECLARATOR = SlangElementType("POINTER_DECLARATOR")
    val NAME_DECLARATOR = SlangElementType("NAME_DECLARATOR")
    val ARRAY_DECLARATOR = SlangElementType("ARRAY_DECLARATOR")
    val BODY_DECL = SlangElementType("BODY_DECL")
    val ARRAY_SPECIFIER = SlangElementType("ARRAY_SPECIFIER")
    val STRUCT_DECLARATION = SlangElementType("STRUCT_DECLARATION")
    val STRUCT_NAME = SlangElementType("STRUCT_NAME")

    val LEFT_BRACE = SlangTokenType("{")
    val RIGHT_BRACE = SlangTokenType("}")
    val LEFT_PAREN = SlangTokenType("(")
    val RIGHT_PAREN = SlangTokenType(")")
    val LEFT_BRACKET = SlangTokenType("[")
    val RIGHT_BRACKET = SlangTokenType("]")
    val SEMICOLON = SlangTokenType(";")
    val COLON = SlangTokenType(":")
    val ASSIGN = SlangTokenType("=")
    val COMMA = SlangTokenType(",")
    val SCOPE = SlangTokenType("::")
    val COMPLETION_REQUEST = SlangTokenType("#?")
    val DOT = SlangTokenType(".")
    val MUL_OP = SlangTokenType("*")
    val LESS_OP = SlangTokenType("<")
    val IDENTIFIER = SlangTokenType("IDENTIFIER")

    class Factory {
        companion object {
            fun createElement(node: ASTNode?): PsiElement {
                when (node?.elementType) {
                    ARRAY_DECLARATOR -> return SlangArrayDeclaratorImpl(node)
                    ARRAY_SPECIFIER -> return SlangArraySpecifierImpl(node)
                    BODY_DECL -> return SlangBodyDeclImpl(node)
                    INIT_DECLARATOR -> return SlangInitDeclaratorImpl(node)
                    NAME_DECLARATOR -> return SlangNameDeclaratorImpl(node)
                    POINTER_DECLARATOR -> return SlangPointerDeclaratorImpl(node)
                    STRUCT_DECLARATION -> return SlangStructDeclarationImpl(node)
                    STRUCT_NAME -> return SlangStructNameImpl(node)
                    TYPE_SPEC -> return SlangTypeSpecImpl(node)
                    VARIABLE_DECL -> return SlangVariableDeclImpl(node)
                }
                throw AssertionError("Unknown element type: ${node?.elementType}")
            }
        }
    }
}