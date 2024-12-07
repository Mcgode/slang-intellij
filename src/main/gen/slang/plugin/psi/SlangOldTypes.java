// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import slang.plugin.psi.impl.*;

public interface SlangOldTypes {

  IElementType ARRAY_DECLARATOR_TT = new SlangElementType("ARRAY_DECLARATOR_TT");
  IElementType DECLARATOR_TT = new SlangElementType("DECLARATOR_TT");
  IElementType NAME_DECLARATOR_TT = new SlangElementType("NAME_DECLARATOR_TT");

  IElementType ADD_ASSIGN = new SlangTokenType("+=");
  IElementType ADD_OP = new SlangTokenType("+");
  IElementType AND_ASSIGN = new SlangTokenType("&=");
  IElementType ASSIGN = new SlangTokenType("ASSIGN");
  IElementType BITWISE_AND_OP = new SlangTokenType("&");
  IElementType BITWISE_NOT_OP = new SlangTokenType("~");
  IElementType BITWISE_OR_OP = new SlangTokenType("|");
  IElementType BITWISE_XOR_OP = new SlangTokenType("^");
  IElementType BOOL = new SlangTokenType("bool");
  IElementType BOOL_LITERAL = new SlangTokenType("BOOL_LITERAL");
  IElementType CLASS = new SlangTokenType("class");
  IElementType COLON = new SlangTokenType("COLON");
  IElementType COMMA = new SlangTokenType("COMMA");
  IElementType COMPLETION_REQUEST = new SlangTokenType("COMPLETION_REQUEST");
  IElementType CONST = new SlangTokenType("const");
  IElementType DIV_ASSIGN = new SlangTokenType("/=");
  IElementType DIV_OP = new SlangTokenType("/");
  IElementType DOUBLE = new SlangTokenType("double");
  IElementType DOUBLE_LITERAL = new SlangTokenType("DOUBLE_LITERAL");
  IElementType EACH = new SlangTokenType("each");
  IElementType ENUM = new SlangTokenType("enum");
  IElementType EXPAND = new SlangTokenType("expand");
  IElementType FLAT = new SlangTokenType("flat");
  IElementType FLOAT = new SlangTokenType("float");
  IElementType FLOAT_LITERAL = new SlangTokenType("FLOAT_LITERAL");
  IElementType FUNCTYPE = new SlangTokenType("functype");
  IElementType GREATER_OP = new SlangTokenType(">");
  IElementType HALF = new SlangTokenType("half");
  IElementType IDENTIFIER = new SlangTokenType("IDENTIFIER");
  IElementType IN = new SlangTokenType("in");
  IElementType INT16 = new SlangTokenType("int16_t");
  IElementType INT32 = new SlangTokenType("int");
  IElementType INT64 = new SlangTokenType("int64_t");
  IElementType INT8 = new SlangTokenType("int8_t");
  IElementType INTERFACE = new SlangTokenType("interface");
  IElementType INT_LITERAL = new SlangTokenType("INT_LITERAL");
  IElementType LEFT_BRACE = new SlangTokenType("LEFT_BRACE");
  IElementType LEFT_BRACKET = new SlangTokenType("LEFT_BRACKET");
  IElementType LEFT_PAREN = new SlangTokenType("LEFT_PAREN");
  IElementType LEFT_SHIFT_ASSIGN = new SlangTokenType("<<=");
  IElementType LESS_OP = new SlangTokenType("<");
  IElementType LINE_COMMENT = new SlangTokenType("LINE_COMMENT");
  IElementType MOD_ASSIGN = new SlangTokenType("%=");
  IElementType MOD_OP = new SlangTokenType("%");
  IElementType MULTILINE_COMMENT = new SlangTokenType("MULTILINE_COMMENT");
  IElementType MUL_ASSIGN = new SlangTokenType("*=");
  IElementType MUL_OP = new SlangTokenType("*");
  IElementType NAMESPACE = new SlangTokenType("namespace");
  IElementType NO_DIFF = new SlangTokenType("no_diff");
  IElementType OR_ASSIGN = new SlangTokenType("|=");
  IElementType OUT = new SlangTokenType("out");
  IElementType PRECISION = new SlangTokenType("precision");
  IElementType RIGHT_BRACE = new SlangTokenType("RIGHT_BRACE");
  IElementType RIGHT_BRACKET = new SlangTokenType("RIGHT_BRACKET");
  IElementType RIGHT_PAREN = new SlangTokenType("RIGHT_PAREN");
  IElementType RIGHT_SHIFT_ASSIGN = new SlangTokenType(">>=");
  IElementType SCOPE = new SlangTokenType("SCOPE");
  IElementType SEMICOLON = new SlangTokenType("SEMICOLON");
  IElementType STRUCT = new SlangTokenType("struct");
  IElementType SUB_ASSIGN = new SlangTokenType("-=");
  IElementType SUB_OP = new SlangTokenType("-");
  IElementType TODO = new SlangTokenType("TODO");
  IElementType TODO_IDENTIFIER = new SlangTokenType("TODO_IDENTIFIER");
  IElementType UINT16 = new SlangTokenType("uint16_t");
  IElementType UINT32 = new SlangTokenType("uint");
  IElementType UINT64 = new SlangTokenType("uint64_t");
  IElementType UINT8 = new SlangTokenType("uint8_t");
  IElementType UINT_LITERAL = new SlangTokenType("UINT_LITERAL");
  IElementType VOID = new SlangTokenType("void");
  IElementType WHERE = new SlangTokenType("where");
  IElementType XOR_ASSIGN = new SlangTokenType("^=");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARRAY_DECLARATOR_TT) {
        return new SlangOldArrayDeclaratorTtImpl(node);
      }
      else if (type == DECLARATOR_TT) {
        return new SlangOldDeclaratorTtImpl(node);
      }
      else if (type == NAME_DECLARATOR_TT) {
        return new SlangOldNameDeclaratorTtImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
