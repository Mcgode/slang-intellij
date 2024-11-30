// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import slang.plugin.psi.impl.*;

public interface SlangTypes {

  IElementType ARRAY_SPECIFIER = new SlangElementType("ARRAY_SPECIFIER");
  IElementType ASSIGNMENT_OPERATOR = new SlangElementType("ASSIGNMENT_OPERATOR");
  IElementType DECLARATION_STATEMENT = new SlangElementType("DECLARATION_STATEMENT");
  IElementType EXPRESSION = new SlangElementType("EXPRESSION");
  IElementType EXPRESSION_ASSIGNMENT = new SlangElementType("EXPRESSION_ASSIGNMENT");
  IElementType EXPRESSION_NO_ASSIGNMENT = new SlangElementType("EXPRESSION_NO_ASSIGNMENT");
  IElementType FULL_TYPE = new SlangElementType("FULL_TYPE");
  IElementType INITIALIZER = new SlangElementType("INITIALIZER");
  IElementType LITERAL = new SlangElementType("LITERAL");
  IElementType POSTFIX_EXPRESSION = new SlangElementType("POSTFIX_EXPRESSION");
  IElementType PRIMARY_EXPRESSION = new SlangElementType("PRIMARY_EXPRESSION");
  IElementType PRIMARY_EXPRESSION_VARIABLE = new SlangElementType("PRIMARY_EXPRESSION_VARIABLE");
  IElementType SINGLE_DECLARATION = new SlangElementType("SINGLE_DECLARATION");
  IElementType STATEMENT = new SlangElementType("STATEMENT");
  IElementType TYPE_NAME = new SlangElementType("TYPE_NAME");
  IElementType TYPE_SPECIFICATION = new SlangElementType("TYPE_SPECIFICATION");
  IElementType UNARY_EXPRESSION = new SlangElementType("UNARY_EXPRESSION");
  IElementType VARIABLE_IDENTIFIER = new SlangElementType("VARIABLE_IDENTIFIER");

  IElementType ADD_ASSIGN = new SlangTokenType("+=");
  IElementType AND_ASSIGN = new SlangTokenType("&=");
  IElementType BOOL_LITERAL = new SlangTokenType("BOOL_LITERAL");
  IElementType COMMA = new SlangTokenType(",");
  IElementType DIV_ASSIGN = new SlangTokenType("/=");
  IElementType DOUBLE_LITERAL = new SlangTokenType("DOUBLE_LITERAL");
  IElementType EQUALS = new SlangTokenType("=");
  IElementType FLOAT_LITERAL = new SlangTokenType("FLOAT_LITERAL");
  IElementType IDENTIFIER = new SlangTokenType("IDENTIFIER");
  IElementType INT_LITERAL = new SlangTokenType("INT_LITERAL");
  IElementType LEFT_BRACE = new SlangTokenType("{");
  IElementType LEFT_BRACKET = new SlangTokenType("[");
  IElementType LEFT_PAREN = new SlangTokenType("(");
  IElementType LEFT_SHIFT_ASSIGN = new SlangTokenType("<<=");
  IElementType LINE_COMMENT = new SlangTokenType("LINE_COMMENT");
  IElementType MOD_ASSIGN = new SlangTokenType("%=");
  IElementType MULTILINE_COMMENT = new SlangTokenType("MULTILINE_COMMENT");
  IElementType MUL_ASSIGN = new SlangTokenType("*=");
  IElementType OR_ASSIGN = new SlangTokenType("|=");
  IElementType PREDEFINED_MACROS = new SlangTokenType("PREDEFINED_MACROS");
  IElementType RIGHT_BRACE = new SlangTokenType("}");
  IElementType RIGHT_BRACKET = new SlangTokenType("]");
  IElementType RIGHT_PAREN = new SlangTokenType(")");
  IElementType RIGHT_SHIFT_ASSIGN = new SlangTokenType(">>=");
  IElementType SEMICOLON = new SlangTokenType(";");
  IElementType SUB_ASSIGN = new SlangTokenType("-=");
  IElementType UINT_LITERAL = new SlangTokenType("UINT_LITERAL");
  IElementType USER_TYPE_NAME = new SlangTokenType("USER_TYPE_NAME");
  IElementType VOID = new SlangTokenType("void");
  IElementType XOR_ASSIGN = new SlangTokenType("^=");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARRAY_SPECIFIER) {
        return new SlangArraySpecifierImpl(node);
      }
      else if (type == ASSIGNMENT_OPERATOR) {
        return new SlangAssignmentOperatorImpl(node);
      }
      else if (type == DECLARATION_STATEMENT) {
        return new SlangDeclarationStatementImpl(node);
      }
      else if (type == EXPRESSION) {
        return new SlangExpressionImpl(node);
      }
      else if (type == EXPRESSION_ASSIGNMENT) {
        return new SlangExpressionAssignmentImpl(node);
      }
      else if (type == EXPRESSION_NO_ASSIGNMENT) {
        return new SlangExpressionNoAssignmentImpl(node);
      }
      else if (type == FULL_TYPE) {
        return new SlangFullTypeImpl(node);
      }
      else if (type == INITIALIZER) {
        return new SlangInitializerImpl(node);
      }
      else if (type == LITERAL) {
        return new SlangLiteralImpl(node);
      }
      else if (type == POSTFIX_EXPRESSION) {
        return new SlangPostfixExpressionImpl(node);
      }
      else if (type == PRIMARY_EXPRESSION) {
        return new SlangPrimaryExpressionImpl(node);
      }
      else if (type == PRIMARY_EXPRESSION_VARIABLE) {
        return new SlangPrimaryExpressionVariableImpl(node);
      }
      else if (type == SINGLE_DECLARATION) {
        return new SlangSingleDeclarationImpl(node);
      }
      else if (type == STATEMENT) {
        return new SlangStatementImpl(node);
      }
      else if (type == TYPE_NAME) {
        return new SlangTypeNameImpl(node);
      }
      else if (type == TYPE_SPECIFICATION) {
        return new SlangTypeSpecificationImpl(node);
      }
      else if (type == UNARY_EXPRESSION) {
        return new SlangUnaryExpressionImpl(node);
      }
      else if (type == VARIABLE_IDENTIFIER) {
        return new SlangVariableIdentifierImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
