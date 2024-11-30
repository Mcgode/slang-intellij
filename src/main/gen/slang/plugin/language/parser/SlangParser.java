// This is a generated file. Not intended for manual editing.
package slang.plugin.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static slang.plugin.psi.SlangTypes.*;
import static slang.plugin.psi.SlangPsiUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
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
  // LEFT_BRACKET expression? RIGHT_BRACKET
  public static boolean array_specifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_specifier")) return false;
    if (!nextTokenIs(b, LEFT_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT_BRACKET);
    r = r && array_specifier_1(b, l + 1);
    r = r && consumeToken(b, RIGHT_BRACKET);
    exit_section_(b, m, ARRAY_SPECIFIER, r);
    return r;
  }

  // expression?
  private static boolean array_specifier_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_specifier_1")) return false;
    expression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // EQUALS
  //     |   ADD_ASSIGN
  //     |   SUB_ASSIGN
  //     |   MUL_ASSIGN
  //     |   DIV_ASSIGN
  //     |   MOD_ASSIGN
  //     |   AND_ASSIGN
  //     |   OR_ASSIGN
  //     |   XOR_ASSIGN
  //     |   LEFT_SHIFT_ASSIGN
  //     |   RIGHT_SHIFT_ASSIGN
  public static boolean assignment_operator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "assignment_operator")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ASSIGNMENT_OPERATOR, "<assignment operator>");
    r = consumeToken(b, EQUALS);
    if (!r) r = consumeToken(b, ADD_ASSIGN);
    if (!r) r = consumeToken(b, SUB_ASSIGN);
    if (!r) r = consumeToken(b, MUL_ASSIGN);
    if (!r) r = consumeToken(b, DIV_ASSIGN);
    if (!r) r = consumeToken(b, MOD_ASSIGN);
    if (!r) r = consumeToken(b, AND_ASSIGN);
    if (!r) r = consumeToken(b, OR_ASSIGN);
    if (!r) r = consumeToken(b, XOR_ASSIGN);
    if (!r) r = consumeToken(b, LEFT_SHIFT_ASSIGN);
    if (!r) r = consumeToken(b, RIGHT_SHIFT_ASSIGN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PREDEFINED_MACROS
  //     |   declaration-statement SEMICOLON
  //     |   statement
  static boolean base(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PREDEFINED_MACROS);
    if (!r) r = base_1(b, l + 1);
    if (!r) r = statement(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // declaration-statement SEMICOLON
  private static boolean base_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = declaration_statement(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // single-declaration (COMMA variable-identifier array-specifier? (EQUALS initializer)?)*
  public static boolean declaration_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECLARATION_STATEMENT, "<declaration statement>");
    r = single_declaration(b, l + 1);
    r = r && declaration_statement_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (COMMA variable-identifier array-specifier? (EQUALS initializer)?)*
  private static boolean declaration_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!declaration_statement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "declaration_statement_1", c)) break;
    }
    return true;
  }

  // COMMA variable-identifier array-specifier? (EQUALS initializer)?
  private static boolean declaration_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && variable_identifier(b, l + 1);
    r = r && declaration_statement_1_0_2(b, l + 1);
    r = r && declaration_statement_1_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // array-specifier?
  private static boolean declaration_statement_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement_1_0_2")) return false;
    array_specifier(b, l + 1);
    return true;
  }

  // (EQUALS initializer)?
  private static boolean declaration_statement_1_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement_1_0_3")) return false;
    declaration_statement_1_0_3_0(b, l + 1);
    return true;
  }

  // EQUALS initializer
  private static boolean declaration_statement_1_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_statement_1_0_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUALS);
    r = r && initializer(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // expression-assignment (COMMA expression-assignment)*
  //     |   expression-no-assignment (COMMA expression-no-assignment)*
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION, "<expression>");
    r = expression_0(b, l + 1);
    if (!r) r = expression_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expression-assignment (COMMA expression-assignment)*
  private static boolean expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression_assignment(b, l + 1);
    r = r && expression_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA expression-assignment)*
  private static boolean expression_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expression_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expression_0_1", c)) break;
    }
    return true;
  }

  // COMMA expression-assignment
  private static boolean expression_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression_assignment(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression-no-assignment (COMMA expression-no-assignment)*
  private static boolean expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression_no_assignment(b, l + 1);
    r = r && expression_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA expression-no-assignment)*
  private static boolean expression_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expression_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expression_1_1", c)) break;
    }
    return true;
  }

  // COMMA expression-no-assignment
  private static boolean expression_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && expression_no_assignment(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // unary-expression assignment-operator expression-no-assignment
  public static boolean expression_assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_assignment")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_ASSIGNMENT, "<expression assignment>");
    r = unary_expression(b, l + 1);
    r = r && assignment_operator(b, l + 1);
    p = r; // pin = assignment-operator
    r = r && expression_no_assignment(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // unary-expression
  public static boolean expression_no_assignment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_no_assignment")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_NO_ASSIGNMENT, "<expression no assignment>");
    r = unary_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
  // type-specification
  public static boolean full_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FULL_TYPE, "<full type>");
    r = type_specification(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LEFT_BRACE expression* RIGHT_BRACE
  //     |   expression
  public static boolean initializer(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializer")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INITIALIZER, "<initializer>");
    r = initializer_0(b, l + 1);
    if (!r) r = expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT_BRACE expression* RIGHT_BRACE
  private static boolean initializer_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializer_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT_BRACE);
    r = r && initializer_0_1(b, l + 1);
    r = r && consumeToken(b, RIGHT_BRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression*
  private static boolean initializer_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "initializer_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expression(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "initializer_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // UINT_LITERAL
  //     |   INT_LITERAL
  //     |   FLOAT_LITERAL
  //     |   DOUBLE_LITERAL
  //     |   BOOL_LITERAL
  public static boolean literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL, "<literal>");
    r = consumeToken(b, UINT_LITERAL);
    if (!r) r = consumeToken(b, INT_LITERAL);
    if (!r) r = consumeToken(b, FLOAT_LITERAL);
    if (!r) r = consumeToken(b, DOUBLE_LITERAL);
    if (!r) r = consumeToken(b, BOOL_LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // primary-expression
  public static boolean postfix_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfix_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, POSTFIX_EXPRESSION, "<postfix expression>");
    r = primary_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // primary-expression-variable
  //     |   LEFT_PAREN expression RIGHT_PAREN
  //     |   literal
  public static boolean primary_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PRIMARY_EXPRESSION, "<primary expression>");
    r = primary_expression_variable(b, l + 1);
    if (!r) r = primary_expression_1(b, l + 1);
    if (!r) r = literal(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // LEFT_PAREN expression RIGHT_PAREN
  private static boolean primary_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary_expression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT_PAREN);
    r = r && expression(b, l + 1);
    r = r && consumeToken(b, RIGHT_PAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean primary_expression_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "primary_expression_variable")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, PRIMARY_EXPRESSION_VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // full-type variable-identifier array-specifier? EQUALS initializer
  //     |   full-type variable-identifier array-specifier
  //     |   full-type variable-identifier?
  public static boolean single_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_declaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SINGLE_DECLARATION, "<single declaration>");
    r = single_declaration_0(b, l + 1);
    if (!r) r = single_declaration_1(b, l + 1);
    if (!r) r = single_declaration_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // full-type variable-identifier array-specifier? EQUALS initializer
  private static boolean single_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_declaration_0")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = full_type(b, l + 1);
    r = r && variable_identifier(b, l + 1);
    r = r && single_declaration_0_2(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    p = r; // pin = 4
    r = r && initializer(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // array-specifier?
  private static boolean single_declaration_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_declaration_0_2")) return false;
    array_specifier(b, l + 1);
    return true;
  }

  // full-type variable-identifier array-specifier
  private static boolean single_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_declaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = full_type(b, l + 1);
    r = r && variable_identifier(b, l + 1);
    r = r && array_specifier(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // full-type variable-identifier?
  private static boolean single_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_declaration_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = full_type(b, l + 1);
    r = r && single_declaration_2_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // variable-identifier?
  private static boolean single_declaration_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_declaration_2_1")) return false;
    variable_identifier(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // expression SEMICOLON
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

  // expression SEMICOLON
  private static boolean statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1);
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

  /* ********************************************************** */
  // IDENTIFIER
  //     |   USER_TYPE_NAME
  public static boolean type_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_name")) return false;
    if (!nextTokenIs(b, "<type name>", IDENTIFIER, USER_TYPE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_NAME, "<type name>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, USER_TYPE_NAME);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VOID
  //     |   type-name
  public static boolean type_specification(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_specification")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_SPECIFICATION, "<type specification>");
    r = consumeToken(b, VOID);
    if (!r) r = type_name(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // postfix-expression
  public static boolean unary_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNARY_EXPRESSION, "<unary expression>");
    r = postfix_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean variable_identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_identifier")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, VARIABLE_IDENTIFIER, r);
    return r;
  }

}
