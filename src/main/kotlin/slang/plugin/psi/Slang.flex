package slang.plugin.psi;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static slang.plugin.psi.types.SlangTypes.*;

%%

%{
    public SlangLexer() {
      this((java.io.Reader)null);
    }
%}

%public
%class SlangLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state MULTILINE_COMMENT_STATE
%state PREPROCESSOR
%state INCLUDE_PREPROCESSOR

NEW_LINE=[\n\r]
WHITE_SPACE=[\ \t\f]+
BACK_SLASH = \\{WHITE_SPACE}*{NEW_LINE}
LINE_COMMENT = "//"+.*

DIGITS=\d+
HEXA_DIGIT=[\da-fA-F]
UNSIGNED="u"|"U"
HEXA_PREFIX="0"("x"|"X")
EXPONENT=("e"|"E")("+"|"-")?{DIGITS}
FLOATING_SUFFIX_FLOAT="f"|"F"
FLOATING_SUFFIX_DOUBLE="lf"|"LF"

HEXA={HEXA_PREFIX}{HEXA_DIGIT}+
INT_LITERAL={DIGITS}|{HEXA}
INTEGER_LITERAL={INT_LITERAL}{UNSIGNED}?

FRACTIONAL=(({DIGITS}"."{DIGITS})|({DIGITS}".")|("."{DIGITS})){EXPONENT}?
FRACTIONAL2={DIGITS}{EXPONENT}
FLOAT_LITERAL=({FRACTIONAL}|{FRACTIONAL2})({FLOATING_SUFFIX_FLOAT}|{FLOATING_SUFFIX_DOUBLE})?

STRING_LITERAL=(\"([^\"\\]|\\.)*\")
INCLUDE_STRING_LITERAL=(<([^\"\\]|\\.)*>)

IDENTIFIER=[_a-zA-Z][_a-zA-Z\d]*

PP_DIRECTIVE_TYPE="if"|"ifdef"|"ifndef"|"else"|"elif"|"endif"|"define"|"undef"|"warning"|"error"|"line"|"pragma"|"version"|"extension"
PREPROCESSOR_DIRECTIVE={WHITE_SPACE}*#{WHITE_SPACE}*{PP_DIRECTIVE_TYPE}
INCLUDE_PREPROCESSOR_DIRECTIVE={WHITE_SPACE}*#{WHITE_SPACE}*"include"

%%

<MULTILINE_COMMENT_STATE> {
    "*/"                { yybegin(YYINITIAL); return INSTANCE.getMULTILINE_COMMENT(); }
    [^*\n]+             { return INSTANCE.getMULTILINE_COMMENT(); }
    "*"                 { return INSTANCE.getMULTILINE_COMMENT(); }
    {NEW_LINE}          { return INSTANCE.getMULTILINE_COMMENT(); }
}

<INCLUDE_PREPROCESSOR> {
    {INCLUDE_STRING_LITERAL} { return INSTANCE.getSTRING_LITERAL(); }
}

<YYINITIAL, PREPROCESSOR, INCLUDE_PREPROCESSOR> {
    ^{PREPROCESSOR_DIRECTIVE}          { yybegin(PREPROCESSOR); return INSTANCE.getPREPROCESSOR_DIRECTIVE(); }
    {NEW_LINE}{PREPROCESSOR_DIRECTIVE} { yybegin(PREPROCESSOR); return INSTANCE.getPREPROCESSOR_DIRECTIVE(); }
    ^{INCLUDE_PREPROCESSOR_DIRECTIVE}          { yybegin(INCLUDE_PREPROCESSOR); return INSTANCE.getPREPROCESSOR_DIRECTIVE(); }
    {NEW_LINE}{INCLUDE_PREPROCESSOR_DIRECTIVE} { yybegin(INCLUDE_PREPROCESSOR); return INSTANCE.getPREPROCESSOR_DIRECTIVE(); }

    {WHITE_SPACE}       { return WHITE_SPACE; }
    {NEW_LINE}          {
          if (yystate() == PREPROCESSOR || yystate() == INCLUDE_PREPROCESSOR) {
              yybegin(YYINITIAL);
              return INSTANCE.getPREPROCESSOR_DIRECTIVE_END();
          }
          else
              return INSTANCE.getNEW_LINE();
    }
    {BACK_SLASH}        { return WHITE_SPACE; }

    {LINE_COMMENT}      { return INSTANCE.getLINE_COMMENT(); }
    "/*"                { yybegin(MULTILINE_COMMENT_STATE); return INSTANCE.getMULTILINE_COMMENT(); }

    "{"                 { return INSTANCE.getLEFT_BRACE(); }
    "}"                 { return INSTANCE.getRIGHT_BRACE(); }
    "("                 { return INSTANCE.getLEFT_PAREN(); }
    ")"                 { return INSTANCE.getRIGHT_PAREN(); }
    "["                 { return INSTANCE.getLEFT_BRACKET(); }
    "]"                 { return INSTANCE.getRIGHT_BRACKET(); }
    ";"                 { return INSTANCE.getSEMICOLON(); }
    ":"                 { return INSTANCE.getCOLON(); }
    ","                 { return INSTANCE.getCOMMA(); }
    "::"                { return INSTANCE.getSCOPE(); }
    "#?"                { return INSTANCE.getCOMPLETION_REQUEST(); }
    "."                 { return INSTANCE.getDOT(); }
    "->"                { return INSTANCE.getRIGHT_ARROW(); }
    "?"                 { return INSTANCE.getQUESTION_MARK(); }
    "$"                 { return INSTANCE.getDOLLAR(); }

    "+"                 { return INSTANCE.getADD_OP(); }
    "-"                 { return INSTANCE.getSUB_OP(); }
    "*"                 { return INSTANCE.getMUL_OP(); }
    "/"                 { return INSTANCE.getDIV_OP(); }
    "%"                 { return INSTANCE.getMOD_OP(); }
    "!"                 { return INSTANCE.getNOT_OP(); }
    "~"                 { return INSTANCE.getBIT_NOT_OP(); }
    "<<"                { return INSTANCE.getSHL_OP(); }
    ">>"                { return INSTANCE.getSHR_OP(); }
    "=="                { return INSTANCE.getEQL_OP(); }
    "!="                { return INSTANCE.getNEQ_OP(); }
    ">"                 { return INSTANCE.getGREATER_OP(); }
    "<"                 { return INSTANCE.getLESS_OP(); }
    ">="                { return INSTANCE.getGEQ_OP(); }
    "<="                { return INSTANCE.getLEQ_OP(); }
    "&&"                { return INSTANCE.getAND_OP(); }
    "||"                { return INSTANCE.getOR_OP(); }
    "&"                 { return INSTANCE.getBIT_AND_OP(); }
    "|"                 { return INSTANCE.getBIT_OR_OP(); }
    "^"                 { return INSTANCE.getBIT_XOR_OP(); }
    "++"                { return INSTANCE.getINC_OP(); }
    "--"                { return INSTANCE.getDEC_OP(); }

    "="                 { return INSTANCE.getASSIGN_OP(); }
    "+="                { return INSTANCE.getADD_ASSIGN_OP(); }
    "-="                { return INSTANCE.getSUB_ASSIGN_OP(); }
    "*="                { return INSTANCE.getMUL_ASSIGN_OP(); }
    "/="                { return INSTANCE.getDIV_ASSIGN_OP(); }
    "%="                { return INSTANCE.getMOD_ASSIGN_OP(); }
    "<<="               { return INSTANCE.getSHL_ASSIGN_OP(); }
    ">>="               { return INSTANCE.getSHR_ASSIGN_OP(); }
    "|="                { return INSTANCE.getOR_ASSIGN_OP(); }
    "&="                { return INSTANCE.getAND_ASSIGN_OP(); }
    "^="                { return INSTANCE.getXOR_ASSIGN_OP(); }

    {INTEGER_LITERAL}   { return INSTANCE.getINTEGER_LITERAL(); }
    {FLOAT_LITERAL}     { return INSTANCE.getFLOAT_LITERAL(); }
    {STRING_LITERAL}    { return INSTANCE.getSTRING_LITERAL(); }
    {IDENTIFIER}        { return INSTANCE.getIDENTIFIER(); }
}

[^] { return BAD_CHARACTER; }
