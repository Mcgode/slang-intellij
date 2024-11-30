package slang.plugin.psi;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;import com.intellij.psi.tree.IElementType;import com.intellij.psi.tree.TokenSet;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static slang.plugin.psi.SlangTypes.*;

%%

%{
    public boolean afterStruct = false;
    public boolean afterType = false;
    public List<String> userDefinedTypes = new ArrayList<>();
    public SlangLexer() {
      this((java.io.Reader)null);
    }

    private IElementType DebugPrint(IElementType elementType)
    {
        System.out.println("Tokenized '%s' to '%s', with state %s afterType=%b afterStruct=%b".formatted(
                yytext().toString(),
                elementType.getDebugName(),
                yystate(),
                afterType,
                afterStruct));
        return elementType;
    }
%}

%public
%class SlangLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state MULTILINE_COMMAND_STATE

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
INT_LITERAL={DIGITS}|{HEXA}
UINT_LITERAL={INT_LITERAL}{UNSIGNED}

FRACTIONAL=(({DIGITS}"."{DIGITS})|({DIGITS}".")|("."{DIGITS})){EXPONENT}?
FRACTIONAL2={DIGITS}{EXPONENT}
FLOAT_LITERAL=({FRACTIONAL}|{FRACTIONAL2}){FLOATING_SUFFIX_FLOAT}?
DOUBLE_LITERAL=({FRACTIONAL}|{FRACTIONAL2}){FLOATING_SUFFIX_DOUBLE}?

BOOL_LITERAL=false|true
STRING_LITERAL=(\"([^\"\\]|\\.)*\")
IDENTIFIER=[_a-zA-Z][_a-zA-Z\d]*

PREDEFINED_MACROS=(__cplusplus|__DATE__|__FILE__|__LINE__|__STDC__|__STDC_HOSTED__|__STDC_NO_COMPLEX__|__STDC_VERSION__|__STDCPP_THREADS__|__TIME__|NDEBUG|__OBJC__|__ASSEMBLER__|__ATOM__|__AVX__|__AVX2__|_CHAR_UNSIGNED|__CLR_VER|_CONTROL_FLOW_GUARD|__COUNTER__|__cplusplus_cli|__cplusplus_winrt|_CPPRTTI|_CPPUNWIND|_DEBUG|_DLL|__FUNCDNAME__|__FUNCSIG__|__FUNCTION__|_INTEGRAL_MAX_BITS|__INTELLISENSE__|_ISO_VOLATILE|_KERNEL_MODE|_M_AMD64|_M_ARM|_M_ARM_ARMV7VE|_M_ARM_FP|_M_ARM64|_M_CEE|_M_CEE_PURE|_M_CEE_SAFE|_M_FP_EXCEPT|_M_FP_FAST|_M_FP_PRECISE|_M_FP_STRICT|_M_IX86|_M_IX86_FP|_M_X64|_MANAGED|_MSC_BUILD|_MSC_EXTENSIONS|_MSC_FULL_VER|_MSC_VER|_MSVC_LANG|__MSVC_RUNTIME_CHECKS|_MT|_NATIVE_WCHAR_T_DEFINED|_OPENMP|_PREFAST|__TIMESTAMP__|_VC_NO_DEFAULTLIB|_WCHAR_T_DEFINED|_WIN32|_WIN64|_WINRT_DLL|_ATL_VER|_MFC_VER|__GFORTRAN__|__GNUC__|__GNUC_MINOR__|__GNUC_PATCHLEVEL__|__GNUG__|__STRICT_ANSI__|__BASE_FILE__|__INCLUDE_LEVEL__|__ELF__|__VERSION__|__OPTIMIZE__|__OPTIMIZE_SIZE__|__NO_INLINE__|__GNUC_STDC_INLINE__|__CHAR_UNSIGNED__|__WCHAR_UNSIGNED__|__REGISTER_PREFIX__|__REGISTER_PREFIX__|__SIZE_TYPE__|__PTRDIFF_TYPE__|__WCHAR_TYPE__|__WINT_TYPE__|__INTMAX_TYPE__|__UINTMAX_TYPE__|__SIG_ATOMIC_TYPE__|__INT8_TYPE__|__INT16_TYPE__|__INT32_TYPE__|__INT64_TYPE__|__UINT8_TYPE__|__UINT16_TYPE__|__UINT32_TYPE__|__UINT64_TYPE__|__INT_LEAST8_TYPE__|__INT_LEAST16_TYPE__|__INT_LEAST32_TYPE__|__INT_LEAST64_TYPE__|__UINT_LEAST8_TYPE__|__UINT_LEAST16_TYPE__|__UINT_LEAST32_TYPE__|__UINT_LEAST64_TYPE__|__INT_FAST8_TYPE__|__INT_FAST16_TYPE__|__INT_FAST32_TYPE__|__INT_FAST64_TYPE__|__UINT_FAST8_TYPE__|__UINT_FAST16_TYPE__|__UINT_FAST32_TYPE__|__UINT_FAST64_TYPE__|__INTPTR_TYPE__|__UINTPTR_TYPE__|__CHAR_BIT__|__SCHAR_MAX__|__WCHAR_MAX__|__SHRT_MAX__|__INT_MAX__|__LONG_MAX__|__LONG_LONG_MAX__|__WINT_MAX__|__SIZE_MAX__|__PTRDIFF_MAX__|__INTMAX_MAX__|__UINTMAX_MAX__|__SIG_ATOMIC_MAX__|__INT8_MAX__|__INT16_MAX__|__INT32_MAX__|__INT64_MAX__|__UINT8_MAX__|__UINT16_MAX__|__UINT32_MAX__|__UINT64_MAX__|__INT_LEAST8_MAX__|__INT_LEAST16_MAX__|__INT_LEAST32_MAX__|__INT_LEAST64_MAX__|__UINT_LEAST8_MAX__|__UINT_LEAST16_MAX__|__UINT_LEAST32_MAX__|__UINT_LEAST64_MAX__|__INT_FAST8_MAX__|__INT_FAST16_MAX__|__INT_FAST32_MAX__|__INT_FAST64_MAX__|__UINT_FAST8_MAX__|__UINT_FAST16_MAX__|__UINT_FAST32_MAX__|__UINT_FAST64_MAX__|__INTPTR_MAX__|__UINTPTR_MAX__|__WCHAR_MIN__|__WINT_MIN__|__SIG_ATOMIC_MIN__|__SCHAR_WIDTH__|__SHRT_WIDTH__|__INT_WIDTH__|__LONG_WIDTH__|__LONG_LONG_WIDTH__|__PTRDIFF_WIDTH__|__SIG_ATOMIC_WIDTH__|__SIZE_WIDTH__|__WCHAR_WIDTH__|__WINT_WIDTH__|__INT_LEAST8_WIDTH__|__INT_LEAST16_WIDTH__|__INT_LEAST32_WIDTH__|__INT_LEAST64_WIDTH__|__INT_FAST8_WIDTH__|__INT_FAST16_WIDTH__|__INT_FAST32_WIDTH__|__INT_FAST64_WIDTH__|__INTPTR_WIDTH__|__INTMAX_WIDTH__|__SIZEOF_INT__|__SIZEOF_LONG__|__SIZEOF_LONG_LONG__|__SIZEOF_SHORT__|__SIZEOF_POINTER__|__SIZEOF_FLOAT__|__SIZEOF_DOUBLE__|__SIZEOF_LONG_DOUBLE__|__SIZEOF_SIZE_T__|__SIZEOF_WCHAR_T__|__SIZEOF_WINT_T__|__SIZEOF_PTRDIFF_T__|__BYTE_ORDER__|__ORDER_LITTLE_ENDIAN__|__ORDER_BIG_ENDIAN__|__ORDER_PDP_ENDIAN__|__FLOAT_WORD_ORDER__|__DEPRECATED|__EXCEPTIONS|__GXX_RTTI|__USING_SJLJ_EXCEPTIONS__|__GXX_EXPERIMENTAL_CXX0X__|__GXX_WEAK__|__NEXT_RUNTIME__|__LP64__|_LP64|__SSP__|__SSP_ALL__|__SSP_STRONG__|__SSP_EXPLICIT__|__SANITIZE_ADDRESS__|__SANITIZE_THREAD__|__GCC_HAVE_SYNC_COMPARE_AND_SWAP_1|__GCC_HAVE_SYNC_COMPARE_AND_SWAP_2|__GCC_HAVE_SYNC_COMPARE_AND_SWAP_4|__GCC_HAVE_SYNC_COMPARE_AND_SWAP_8|__GCC_HAVE_SYNC_COMPARE_AND_SWAP_16|__HAVE_SPECULATION_SAFE_VALUE|__GCC_HAVE_DWARF2_CFI_ASM|__FP_FAST_FMA|__FP_FAST_FMAF|__FP_FAST_FMAL|__FP_FAST_FMAF16|__FP_FAST_FMAF32|__FP_FAST_FMAF64|__FP_FAST_FMAF128|__FP_FAST_FMAF32X|__FP_FAST_FMAF64X|__FP_FAST_FMAF128X|__GCC_IEC_559|__GCC_IEC_559_COMPLEX|__NO_MATH_ERRNO__|__has_builtin|__has_feature|__has_extension|__has_cpp_attribute|__has_c_attribute|__has_attribute|__has_declspec_attribute|__is_identifier|__has_include|__has_include_next|__has_warning|__BASE_FILE__|__FILE_NAME__|__clang__|__clang_major__|__clang_minor__|__clang_patchlevel__|__clang_version__|__fp16|_Float16)

%%

<MULTILINE_COMMAND_STATE> {
    "*/"                { yybegin(YYINITIAL); return MULTILINE_COMMENT; }
    [^*\n]+             { return MULTILINE_COMMENT; }
    "*"                 { return MULTILINE_COMMENT; }
    {NEW_LINE}          { return MULTILINE_COMMENT; }
}

<YYINITIAL> {
    {WHITE_SPACE}       { return WHITE_SPACE; }
    {NEW_LINE}          { return NEW_LINE; }
    {BACK_SLASH}        { return WHITE_SPACE; }

    {LINE_COMMENT}      { return LINE_COMMENT; }
    "/*"                { yybegin(MULTILINE_COMMAND_STATE); return MULTILINE_COMMENT; }

    "{"                 { afterStruct = false; return LEFT_BRACE; }
    "}"                 { return RIGHT_BRACE; }
    "("                 { afterType = false; return LEFT_PAREN; }
    ")"                 { afterType = false; return RIGHT_PAREN; }
    "["                 { return LEFT_BRACKET; }
    "]"                 { return RIGHT_BRACKET; }
    ";"                 { afterType = false; return SEMICOLON; }
    ","                 { afterType = false; return COMMA; }
    "="                 { afterType = false; return EQUALS; }

    "+="                { return ADD_ASSIGN; }
    "-="                { return SUB_ASSIGN; }
    "*="                { return MUL_ASSIGN; }
    "/="                { return DIV_ASSIGN; }
    "%="                { return MOD_ASSIGN; }
    "&="                { return AND_ASSIGN; }
    "|="                { return OR_ASSIGN; }
    "^="                { return XOR_ASSIGN; }
    "<<="               { return LEFT_SHIFT_ASSIGN; }
    ">>="               { return RIGHT_SHIFT_ASSIGN; }

    "void"              { afterType = true; return VOID; }

    {PREDEFINED_MACROS} { return PREDEFINED_MACROS; }

    {INT_LITERAL}       { return INT_LITERAL; }
    {UINT_LITERAL}      { return UINT_LITERAL; }
    {FLOAT_LITERAL}     { return FLOAT_LITERAL; }
    {DOUBLE_LITERAL}    { return DOUBLE_LITERAL; }
    {BOOL_LITERAL}      { return BOOL_LITERAL; }
    {IDENTIFIER}        {
        if (!afterType && !afterStruct && userDefinedTypes.contains(yytext().toString()))
        {
            afterType = true;
            return USER_TYPE_NAME;
        }
        else if (afterStruct)
        {
            afterStruct = false;
            userDefinedTypes.add(yytext().toString());
        }
        return IDENTIFIER;
    }
}

[^] { return BAD_CHARACTER; }
