// This is a generated file. Not intended for manual editing.
package slang.plugin.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static slang.plugin.psi.SlangTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class SlangParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return file(b, l + 1);
  }

  /* ********************************************************** */
  // PREDEFINED_MACROS
  //     |   declaration
  //     |   statement
  static boolean base(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base")) return false;
    boolean r;
    r = consumeToken(b, PREDEFINED_MACROS);
    if (!r) r = declaration(b, l + 1);
    if (!r) r = statement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TYPE IDENTIFIER (EQUALS expression)?
  public static boolean declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration")) return false;
    if (!nextTokenIs(b, TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TYPE, IDENTIFIER);
    r = r && declaration_2(b, l + 1);
    exit_section_(b, m, DECLARATION, r);
    return r;
  }

  // (EQUALS expression)?
  private static boolean declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_2")) return false;
    declaration_2_0(b, l + 1);
    return true;
  }

  // EQUALS expression
  private static boolean declaration_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUALS);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // declaration (COMMA declaration)*
  public static boolean declaration_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement")) return false;
    if (!nextTokenIs(b, TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = declaration(b, l + 1);
    r = r && declaration_statement_1(b, l + 1);
    exit_section_(b, m, DECLARATION_STATEMENT, r);
    return r;
  }

  // (COMMA declaration)*
  private static boolean declaration_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!declaration_statement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "declaration_statement_1", c)) break;
    }
    return true;
  }

  // COMMA declaration
  private static boolean declaration_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && declaration(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LEFT_PAREN expression RIGHT_PAREN
  //     |   IDENTIFIER EQUALS expression
  //     |   IDENTIFIER
  //     |   LITERAL
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION, "<expression>");
    r = expression_0(b, l + 1);
    if (!r) r = expression_1(b, l + 1);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT_PAREN expression RIGHT_PAREN
  private static boolean expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT_PAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RIGHT_PAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENTIFIER EQUALS expression
  private static boolean expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, EQUALS);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expression (COMMA expression)*
  public static boolean expression_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_STATEMENT, "<expression statement>");
    r = expression(b, l + 1);
    r = r && expression_statement_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA expression)*
  private static boolean expression_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expression_statement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expression_statement_1", c)) break;
    }
    return true;
  }

  // COMMA expression
  private static boolean expression_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // base*
  static boolean file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file")) return false;
    while (true) {
      int c = current_position_(b);
      if (!base(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "file", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // expression-statement SEMICOLON
  //     |   declaration-statement SEMICOLON
  //     |   SEMICOLON
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT, "<statement>");
    r = statement_0(b, l + 1);
    if (!r) r = statement_1(b, l + 1);
    if (!r) r = consumeToken(b, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expression-statement SEMICOLON
  private static boolean statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression_statement(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // declaration-statement SEMICOLON
  private static boolean statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = declaration_statement(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

}
