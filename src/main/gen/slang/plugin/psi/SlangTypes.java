// This is a generated file. Not intended for manual editing.
package slang.plugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import slang.plugin.psi.impl.*;

public interface SlangTypes {

  IElementType DECLARATION = new SlangElementType("DECLARATION");
  IElementType DECLARATION_STATEMENT = new SlangElementType("DECLARATION_STATEMENT");
  IElementType EXPRESSION = new SlangElementType("EXPRESSION");
  IElementType EXPRESSION_STATEMENT = new SlangElementType("EXPRESSION_STATEMENT");
  IElementType STATEMENT = new SlangElementType("STATEMENT");

  IElementType COMMA = new SlangTokenType(",");
  IElementType EQUALS = new SlangTokenType("=");
  IElementType IDENTIFIER = new SlangTokenType("IDENTIFIER");
  IElementType LEFT_BRACE = new SlangTokenType("{");
  IElementType LEFT_PAREN = new SlangTokenType("(");
  IElementType LINE_COMMENT = new SlangTokenType("LINE_COMMENT");
  IElementType LITERAL = new SlangTokenType("LITERAL");
  IElementType MULTILINE_COMMENT = new SlangTokenType("MULTILINE_COMMENT");
  IElementType PREDEFINED_MACROS = new SlangTokenType("PREDEFINED_MACROS");
  IElementType RIGHT_BRACE = new SlangTokenType("}");
  IElementType RIGHT_PAREN = new SlangTokenType(")");
  IElementType SEMICOLON = new SlangTokenType(";");
  IElementType TYPE = new SlangTokenType("TYPE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == DECLARATION) {
        return new SlangDeclarationImpl(node);
      }
      else if (type == DECLARATION_STATEMENT) {
        return new SlangDeclarationStatementImpl(node);
      }
      else if (type == EXPRESSION) {
        return new SlangExpressionImpl(node);
      }
      else if (type == EXPRESSION_STATEMENT) {
        return new SlangExpressionStatementImpl(node);
      }
      else if (type == STATEMENT) {
        return new SlangStatementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
