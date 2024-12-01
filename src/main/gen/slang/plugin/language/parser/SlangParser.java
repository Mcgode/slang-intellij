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
  // declaration-statement SEMICOLON
  //     |   storage-type-declaration
  public static boolean declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DECLARATION, "<declaration>");
    r = declaration_0(b, l + 1);
    if (!r) r = storage_type_declaration(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // declaration-statement SEMICOLON
  private static boolean declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_0")) return false;
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
  // (ENUM|ENUM_CLASS) type-name LEFT_BRACE enum-members? RIGHT_BRACE SEMICOLON
  public static boolean enum_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration")) return false;
    if (!nextTokenIs(b, "<enum declaration>", ENUM, ENUM_CLASS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ENUM_DECLARATION, "<enum declaration>");
    r = enum_declaration_0(b, l + 1);
    r = r && type_name(b, l + 1);
    r = r && consumeToken(b, LEFT_BRACE);
    r = r && enum_declaration_3(b, l + 1);
    r = r && consumeTokens(b, 0, RIGHT_BRACE, SEMICOLON);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ENUM|ENUM_CLASS
  private static boolean enum_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_0")) return false;
    boolean r;
    r = consumeToken(b, ENUM);
    if (!r) r = consumeToken(b, ENUM_CLASS);
    return r;
  }

  // enum-members?
  private static boolean enum_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_declaration_3")) return false;
    enum_members(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER (EQUALS expression)?
  public static boolean enum_member(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_member")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && enum_member_1(b, l + 1);
    exit_section_(b, m, ENUM_MEMBER, r);
    return r;
  }

  // (EQUALS expression)?
  private static boolean enum_member_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_member_1")) return false;
    enum_member_1_0(b, l + 1);
    return true;
  }

  // EQUALS expression
  private static boolean enum_member_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_member_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQUALS);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // enum-member (COMMA enum-member)* COMMA?
  static boolean enum_members(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_members")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = enum_member(b, l + 1);
    r = r && enum_members_1(b, l + 1);
    r = r && enum_members_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA enum-member)*
  private static boolean enum_members_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_members_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!enum_members_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "enum_members_1", c)) break;
    }
    return true;
  }

  // COMMA enum-member
  private static boolean enum_members_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_members_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && enum_member(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean enum_members_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "enum_members_2")) return false;
    consumeToken(b, COMMA);
    return true;
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
  // type-qualifiers? type-specification
  public static boolean full_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FULL_TYPE, "<full type>");
    r = full_type_0(b, l + 1);
    r = r && type_specification(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // type-qualifiers?
  private static boolean full_type_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "full_type_0")) return false;
    type_qualifiers(b, l + 1);
    return true;
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
  // full-type variable-identifier (COLON semantic)? SEMICOLON
  public static boolean member_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member_declaration")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, MEMBER_DECLARATION, "<member declaration>");
    r = full_type(b, l + 1);
    r = r && variable_identifier(b, l + 1);
    p = r; // pin = 2
    r = r && report_error_(b, member_declaration_2(b, l + 1));
    r = p && consumeToken(b, SEMICOLON) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (COLON semantic)?
  private static boolean member_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member_declaration_2")) return false;
    member_declaration_2_0(b, l + 1);
    return true;
  }

  // COLON semantic
  private static boolean member_declaration_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "member_declaration_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && semantic(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NAMESPACE type-name? LEFT_BRACE base* RIGHT_BRACE
  public static boolean namespace_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_declaration")) return false;
    if (!nextTokenIs(b, NAMESPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAMESPACE);
    r = r && namespace_declaration_1(b, l + 1);
    r = r && consumeToken(b, LEFT_BRACE);
    r = r && namespace_declaration_3(b, l + 1);
    r = r && consumeToken(b, RIGHT_BRACE);
    exit_section_(b, m, NAMESPACE_DECLARATION, r);
    return r;
  }

  // type-name?
  private static boolean namespace_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_declaration_1")) return false;
    type_name(b, l + 1);
    return true;
  }

  // base*
  private static boolean namespace_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_declaration_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!base(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namespace_declaration_3", c)) break;
    }
    return true;
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
  // VOID
  //     |   BOOL
  //     |   INT8
  //     |   INT16
  //     |   INT32
  //     |   INT64
  //     |   UINT8
  //     |   UINT16
  //     |   UINT32
  //     |   UINT64
  //     |   HALF
  //     |   FLOAT
  //     |   DOUBLE
  public static boolean scalar_type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_type")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SCALAR_TYPE, "<scalar type>");
    r = consumeToken(b, VOID);
    if (!r) r = consumeToken(b, BOOL);
    if (!r) r = consumeToken(b, INT8);
    if (!r) r = consumeToken(b, INT16);
    if (!r) r = consumeToken(b, INT32);
    if (!r) r = consumeToken(b, INT64);
    if (!r) r = consumeToken(b, UINT8);
    if (!r) r = consumeToken(b, UINT16);
    if (!r) r = consumeToken(b, UINT32);
    if (!r) r = consumeToken(b, UINT64);
    if (!r) r = consumeToken(b, HALF);
    if (!r) r = consumeToken(b, FLOAT);
    if (!r) r = consumeToken(b, DOUBLE);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean semantic(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "semantic")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, SEMANTIC, r);
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
  // storage-qualifier
  static boolean single_type_qualifier(PsiBuilder b, int l) {
    return storage_qualifier(b, l + 1);
  }

  /* ********************************************************** */
  // expression SEMICOLON
  //     |   SEMICOLON
  public static boolean statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STATEMENT, "<statement>");
    r = statement_0(b, l + 1);
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

  /* ********************************************************** */
  // CONST | IN | OUT
  public static boolean storage_qualifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "storage_qualifier")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STORAGE_QUALIFIER, "<storage qualifier>");
    r = consumeToken(b, CONST);
    if (!r) r = consumeToken(b, IN);
    if (!r) r = consumeToken(b, OUT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // namespace-declaration
  //     |   enum-declaration
  //     |   struct-declaration
  static boolean storage_type_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "storage_type_declaration")) return false;
    boolean r;
    r = namespace_declaration(b, l + 1);
    if (!r) r = enum_declaration(b, l + 1);
    if (!r) r = struct_declaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // STRUCT type-name? LEFT_BRACE member-declaration* RIGHT_BRACE SEMICOLON
  public static boolean struct_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration")) return false;
    if (!nextTokenIs(b, STRUCT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRUCT);
    r = r && struct_declaration_1(b, l + 1);
    r = r && consumeToken(b, LEFT_BRACE);
    r = r && struct_declaration_3(b, l + 1);
    r = r && consumeTokens(b, 0, RIGHT_BRACE, SEMICOLON);
    exit_section_(b, m, STRUCT_DECLARATION, r);
    return r;
  }

  // type-name?
  private static boolean struct_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_1")) return false;
    type_name(b, l + 1);
    return true;
  }

  // member-declaration*
  private static boolean struct_declaration_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!member_declaration(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "struct_declaration_3", c)) break;
    }
    return true;
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
  // single-type-qualifier+
  public static boolean type_qualifiers(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_qualifiers")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_QUALIFIERS, "<type qualifiers>");
    r = single_type_qualifier(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!single_type_qualifier(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "type_qualifiers", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // scalar-type
  //     |   type-name
  public static boolean type_specification(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_specification")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_SPECIFICATION, "<type specification>");
    r = scalar_type(b, l + 1);
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
