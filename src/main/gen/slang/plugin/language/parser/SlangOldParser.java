// This is a generated file. Not intended for manual editing.
package slang.plugin.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static slang.plugin.psi.SlangOldTypes.*;
import static slang.plugin.psi.SlangPsiUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class SlangOldParser implements PsiParser, LightPsiParser {

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
  // LEFT_BRACKET expression ? RIGHT_BRACKET
  public static boolean array_declarator_tt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_declarator_tt")) return false;
    if (!nextTokenIs(b, LEFT_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _LEFT_, ARRAY_DECLARATOR_TT, null);
    r = consumeToken(b, LEFT_BRACKET);
    r = r && array_declarator_tt_1(b, l + 1);
    r = r && consumeToken(b, RIGHT_BRACKET);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // expression ?
  private static boolean array_declarator_tt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_declarator_tt_1")) return false;
    expression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LEFT_BRACKET expression? RIGHT_BRACKET
  static boolean array_specifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_specifier")) return false;
    if (!nextTokenIs(b, LEFT_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT_BRACKET);
    r = r && array_specifier_1(b, l + 1);
    r = r && consumeToken(b, RIGHT_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression?
  private static boolean array_specifier_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_specifier_1")) return false;
    expression(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // type-specification
  static boolean atomic_type_expression(PsiBuilder b, int l) {
    return type_specification(b, l + 1);
  }

  /* ********************************************************** */
  // attribute-identifier ( LEFT_PAREN attribute-parameters RIGHT_PAREN )?
  static boolean attribute(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute")) return false;
    if (!nextTokenIs(b, "", IDENTIFIER, SCOPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = attribute_identifier(b, l + 1);
    r = r && attribute_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ( LEFT_PAREN attribute-parameters RIGHT_PAREN )?
  private static boolean attribute_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_1")) return false;
    attribute_1_0(b, l + 1);
    return true;
  }

  // LEFT_PAREN attribute-parameters RIGHT_PAREN
  private static boolean attribute_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT_PAREN);
    r = r && attribute_parameters(b, l + 1);
    r = r && consumeToken(b, RIGHT_PAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LEFT_BRACKET LEFT_BRACKET attributes RIGHT_BRACKET RIGHT_BRACKET
  //     |   LEFT_BRACKET attributes RIGHT_BRACKET
  static boolean attribute_container(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_container")) return false;
    if (!nextTokenIs(b, LEFT_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = attribute_container_0(b, l + 1);
    if (!r) r = attribute_container_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LEFT_BRACKET LEFT_BRACKET attributes RIGHT_BRACKET RIGHT_BRACKET
  private static boolean attribute_container_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_container_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LEFT_BRACKET, LEFT_BRACKET);
    r = r && attributes(b, l + 1);
    r = r && consumeTokens(b, 0, RIGHT_BRACKET, RIGHT_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // LEFT_BRACKET attributes RIGHT_BRACKET
  private static boolean attribute_container_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_container_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT_BRACKET);
    r = r && attributes(b, l + 1);
    r = r && consumeToken(b, RIGHT_BRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SCOPE? IDENTIFIER (SCOPE IDENTIFIER)*
  static boolean attribute_identifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_identifier")) return false;
    if (!nextTokenIs(b, "", IDENTIFIER, SCOPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = attribute_identifier_0(b, l + 1);
    r = r && consumeToken(b, IDENTIFIER);
    r = r && attribute_identifier_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SCOPE?
  private static boolean attribute_identifier_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_identifier_0")) return false;
    consumeToken(b, SCOPE);
    return true;
  }

  // (SCOPE IDENTIFIER)*
  private static boolean attribute_identifier_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_identifier_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!attribute_identifier_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "attribute_identifier_2", c)) break;
    }
    return true;
  }

  // SCOPE IDENTIFIER
  private static boolean attribute_identifier_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_identifier_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SCOPE, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TODO
  static boolean attribute_parameter(PsiBuilder b, int l) {
    return consumeToken(b, TODO);
  }

  /* ********************************************************** */
  // attribute-parameter (COMMA attribute-parameter)*
  static boolean attribute_parameters(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_parameters")) return false;
    if (!nextTokenIs(b, TODO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = attribute_parameter(b, l + 1);
    r = r && attribute_parameters_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA attribute-parameter)*
  private static boolean attribute_parameters_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_parameters_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!attribute_parameters_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "attribute_parameters_1", c)) break;
    }
    return true;
  }

  // COMMA attribute-parameter
  private static boolean attribute_parameters_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attribute_parameters_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && attribute_parameter(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // attribute (COMMA attribute)*
  static boolean attributes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attributes")) return false;
    if (!nextTokenIs(b, "", IDENTIFIER, SCOPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = attribute(b, l + 1);
    r = r && attributes_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA attribute)*
  private static boolean attributes_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attributes_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!attributes_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "attributes_1", c)) break;
    }
    return true;
  }

  // COMMA attribute
  private static boolean attributes_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "attributes_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && attribute(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // glsl-global-declaration
  //     |   declaration
  static boolean base(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base")) return false;
    boolean r;
    r = glsl_global_declaration(b, l + 1);
    if (!r) r = declaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TODO
  static boolean class_declaration(PsiBuilder b, int l) {
    return consumeToken(b, TODO);
  }

  /* ********************************************************** */
  // declaration-modifier* declaration-content
  static boolean declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = declaration_0(b, l + 1);
    r = r && declaration_content(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // declaration-modifier*
  private static boolean declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!declaration_modifier(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "declaration_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LEFT_BRACE declaration* RIGHT_BRACE
  static boolean declaration_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_body")) return false;
    if (!nextTokenIs(b, LEFT_BRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LEFT_BRACE);
    r = r && declaration_body_1(b, l + 1);
    r = r && consumeToken(b, RIGHT_BRACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // declaration*
  private static boolean declaration_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_body_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!declaration(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "declaration_body_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TODO_IDENTIFIER // TODO: (see slang/slang-parser.cpp:4706)
  //     |   SEMICOLON
  //     |   empty-declaration
  //     |   declarator-declaration
  static boolean declaration_content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_content")) return false;
    boolean r;
    r = consumeToken(b, TODO_IDENTIFIER);
    if (!r) r = consumeToken(b, SEMICOLON);
    if (!r) r = empty_declaration(b, l + 1);
    if (!r) r = declarator_declaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // NO_DIFF
  //     |   FLAT
  //     |   attribute-container
  static boolean declaration_modifier(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declaration_modifier")) return false;
    boolean r;
    r = consumeToken(b, NO_DIFF);
    if (!r) r = consumeToken(b, FLAT);
    if (!r) r = attribute_container(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // type-specification array-specifier* SEMICOLON?
  static boolean declarator_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declarator_declaration")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = type_specification(b, l + 1);
    r = r && declarator_declaration_1(b, l + 1);
    r = r && declarator_declaration_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // array-specifier*
  private static boolean declarator_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declarator_declaration_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!array_specifier(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "declarator_declaration_1", c)) break;
    }
    return true;
  }

  // SEMICOLON?
  private static boolean declarator_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declarator_declaration_2")) return false;
    consumeToken(b, SEMICOLON);
    return true;
  }

  /* ********************************************************** */
  // name-declarator-tt array-declarator-tt*
  public static boolean declarator_tt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declarator_tt")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = name_declarator_tt(b, l + 1);
    r = r && declarator_tt_1(b, l + 1);
    exit_section_(b, m, DECLARATOR_TT, r);
    return r;
  }

  // array-declarator-tt*
  private static boolean declarator_tt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "declarator_tt_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!array_declarator_tt(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "declarator_tt_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LEFT_BRACE TODO RIGHT_BRACE // TODO: Set up a safe skip method (see slang/slang-parser.cpp:345)
  //     |   LEFT_PAREN TODO RIGHT_PAREN
  static boolean empty_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "empty_declaration")) return false;
    if (!nextTokenIs(b, "", LEFT_BRACE, LEFT_PAREN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parseTokens(b, 0, LEFT_BRACE, TODO, RIGHT_BRACE);
    if (!r) r = parseTokens(b, 0, LEFT_PAREN, TODO, RIGHT_PAREN);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // TODO
  static boolean enum_declaration(PsiBuilder b, int l) {
    return consumeToken(b, TODO);
  }

  /* ********************************************************** */
  // TODO
  static boolean expression(PsiBuilder b, int l) {
    return consumeToken(b, TODO);
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
  // FUNCTYPE TODO
  static boolean functype_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "functype_declaration")) return false;
    if (!nextTokenIs(b, FUNCTYPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, FUNCTYPE, TODO);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // WHERE TODO
  static boolean generic_constraints(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_constraints")) return false;
    if (!nextTokenIs(b, WHERE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, WHERE, TODO);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PRECISION TODO TODO SEMICOLON
  static boolean glsl_global_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glsl_global_declaration")) return false;
    if (!nextTokenIs(b, PRECISION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PRECISION, TODO, TODO, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON type-expression (COMMA type-expression)*
  static boolean inheritance_clause(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inheritance_clause")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type_expression(b, l + 1);
    r = r && inheritance_clause_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA type-expression)*
  private static boolean inheritance_clause_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inheritance_clause_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!inheritance_clause_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "inheritance_clause_2", c)) break;
    }
    return true;
  }

  // COMMA type-expression
  private static boolean inheritance_clause_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inheritance_clause_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && type_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // struct-declaration
  //     |   class-declaration
  //     |   enum-declaration
  //     |   prefix-expression-declaration
  //     |   functype-declaration
  static boolean inline_type_specification(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inline_type_specification")) return false;
    boolean r;
    r = struct_declaration(b, l + 1);
    if (!r) r = class_declaration(b, l + 1);
    if (!r) r = enum_declaration(b, l + 1);
    if (!r) r = prefix_expression_declaration(b, l + 1);
    if (!r) r = functype_declaration(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean name_declarator_tt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "name_declarator_tt")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, NAME_DECLARATOR_TT, r);
    return r;
  }

  /* ********************************************************** */
  // MUL_OP
  static boolean pointer_specifier(PsiBuilder b, int l) {
    return consumeToken(b, MUL_OP);
  }

  /* ********************************************************** */
  // atomic-type-expression postfix-type-expression-suffix
  static boolean postfix_type_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfix_type_expression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = atomic_type_expression(b, l + 1);
    r = r && postfix_type_expression_suffix(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (array-specifier | pointer-specifier)*
  static boolean postfix_type_expression_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfix_type_expression_suffix")) return false;
    while (true) {
      int c = current_position_(b);
      if (!postfix_type_expression_suffix_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "postfix_type_expression_suffix", c)) break;
    }
    return true;
  }

  // array-specifier | pointer-specifier
  private static boolean postfix_type_expression_suffix_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "postfix_type_expression_suffix_0")) return false;
    boolean r;
    r = array_specifier(b, l + 1);
    if (!r) r = pointer_specifier(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TODO
  static boolean prefix_expression(PsiBuilder b, int l) {
    return consumeToken(b, TODO);
  }

  /* ********************************************************** */
  // (EXPAND | EACH) prefix-expression
  static boolean prefix_expression_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prefix_expression_declaration")) return false;
    if (!nextTokenIs(b, "", EACH, EXPAND)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = prefix_expression_declaration_0(b, l + 1);
    r = r && prefix_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EXPAND | EACH
  private static boolean prefix_expression_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prefix_expression_declaration_0")) return false;
    boolean r;
    r = consumeToken(b, EXPAND);
    if (!r) r = consumeToken(b, EACH);
    return r;
  }

  /* ********************************************************** */
  // inline-type-specification
  //     |   TODO
  static boolean simple_type_specification(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_type_specification")) return false;
    boolean r;
    r = inline_type_specification(b, l + 1);
    if (!r) r = consumeToken(b, TODO);
    return r;
  }

  /* ********************************************************** */
  // struct-header ASSIGN type-expression SEMICOLON
  //     |   struct-header SEMICOLON
  //     |   struct-header generic-constraints? declaration-body
  static boolean struct_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration")) return false;
    if (!nextTokenIs(b, STRUCT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = struct_declaration_0(b, l + 1);
    if (!r) r = struct_declaration_1(b, l + 1);
    if (!r) r = struct_declaration_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // struct-header ASSIGN type-expression SEMICOLON
  private static boolean struct_declaration_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = struct_header(b, l + 1);
    r = r && consumeToken(b, ASSIGN);
    r = r && type_expression(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // struct-header SEMICOLON
  private static boolean struct_declaration_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = struct_header(b, l + 1);
    r = r && consumeToken(b, SEMICOLON);
    exit_section_(b, m, null, r);
    return r;
  }

  // struct-header generic-constraints? declaration-body
  private static boolean struct_declaration_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = struct_header(b, l + 1);
    r = r && struct_declaration_2_1(b, l + 1);
    r = r && declaration_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // generic-constraints?
  private static boolean struct_declaration_2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_declaration_2_1")) return false;
    generic_constraints(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // STRUCT attribute-container? COMPLETION_REQUEST? struct-name? template-specialization? inheritance-clause?
  static boolean struct_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_header")) return false;
    if (!nextTokenIs(b, STRUCT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRUCT);
    r = r && struct_header_1(b, l + 1);
    r = r && struct_header_2(b, l + 1);
    r = r && struct_header_3(b, l + 1);
    r = r && struct_header_4(b, l + 1);
    r = r && struct_header_5(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // attribute-container?
  private static boolean struct_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_header_1")) return false;
    attribute_container(b, l + 1);
    return true;
  }

  // COMPLETION_REQUEST?
  private static boolean struct_header_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_header_2")) return false;
    consumeToken(b, COMPLETION_REQUEST);
    return true;
  }

  // struct-name?
  private static boolean struct_header_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_header_3")) return false;
    struct_name(b, l + 1);
    return true;
  }

  // template-specialization?
  private static boolean struct_header_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_header_4")) return false;
    template_specialization(b, l + 1);
    return true;
  }

  // inheritance-clause?
  private static boolean struct_header_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "struct_header_5")) return false;
    inheritance_clause(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER
  static boolean struct_name(PsiBuilder b, int l) {
    return consumeToken(b, IDENTIFIER);
  }

  /* ********************************************************** */
  // LESS_OP TODO GREATER_OP
  static boolean template_specialization(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_specialization")) return false;
    if (!nextTokenIs(b, LESS_OP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LESS_OP, TODO, GREATER_OP);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // postfix-type-expression type-expression-suffix
  static boolean type_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expression")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = postfix_type_expression(b, l + 1);
    r = r && type_expression_suffix(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (BITWISE_AND_OP postfix-type-expression)*
  static boolean type_expression_suffix(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expression_suffix")) return false;
    while (true) {
      int c = current_position_(b);
      if (!type_expression_suffix_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "type_expression_suffix", c)) break;
    }
    return true;
  }

  // BITWISE_AND_OP postfix-type-expression
  private static boolean type_expression_suffix_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expression_suffix_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BITWISE_AND_OP);
    r = r && postfix_type_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // simple-type-specification
  static boolean type_specification(PsiBuilder b, int l) {
    return simple_type_specification(b, l + 1);
  }

}
