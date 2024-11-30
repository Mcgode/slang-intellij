package slang.plugin.psi;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;import com.intellij.psi.tree.IElementType;import com.intellij.psi.tree.TokenSet;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static slang.plugin.psi.SlangTypes.*;

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

%state WAITING_VALUE

NEW_LINE=[\n\r]
WHITE_SPACE=\s+
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
INTCONSTANT={DIGITS}|{HEXA}
UINTCONSTANT={INTCONSTANT}{UNSIGNED}

FRACTIONAL=(({DIGITS}"."{DIGITS})|({DIGITS}".")|("."{DIGITS})){EXPONENT}?
FRACTIONAL2={DIGITS}{EXPONENT}
FLOATCONSTANT=({FRACTIONAL}|{FRACTIONAL2}){FLOATING_SUFFIX_FLOAT}?
DOUBLECONSTANT=({FRACTIONAL}|{FRACTIONAL2}){FLOATING_SUFFIX_DOUBLE}?

BOOLCONSTANT=false|true
STRING_LITERAL=(\"([^\"\\]|\\.)*\")
IDENTIFIER=[a-zA-Z_]+\w*

%%
<YYINITIAL> {
    {WHITE_SPACE}       { return TokenType.WHITE_SPACE; }
    {NEW_LINE}          { return SlangTypes.NEW_LINE; }
    {IDENTIFIER}        { return SlangTypes.IDENTIFIER; }
}

[^] { return BAD_CHARACTER; }
