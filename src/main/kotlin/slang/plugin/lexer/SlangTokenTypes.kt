package slang.plugin.lexer

import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import slang.plugin.language.SlangLanguage

class SlangTokenType(debugName: String) : IElementType(debugName, SlangLanguage) {
    override fun toString(): String = "SlangTokenType." + super.toString()
}

object SlangTokenTypes {
    @JvmField val LINE_COMMENT = SlangTokenType("LINE_COMMENT")
    @JvmField val BLOCK_COMMENT = SlangTokenType("BLOCK_COMMENT")

    @JvmField val IDENTIFIER = SlangTokenType("IDENTIFIER")
    @JvmField val KEYWORD = SlangTokenType("KEYWORD")
    @JvmField val BUILTIN_TYPE = SlangTokenType("BUILTIN_TYPE")

    @JvmField val NUMBER = SlangTokenType("NUMBER")
    @JvmField val STRING = SlangTokenType("STRING")

    @JvmField val PREPROCESSOR_DIRECTIVE = SlangTokenType("PREPROCESSOR_DIRECTIVE")

    @JvmField val LBRACE = SlangTokenType("{")
    @JvmField val RBRACE = SlangTokenType("}")
    @JvmField val LPAREN = SlangTokenType("(")
    @JvmField val RPAREN = SlangTokenType(")")
    @JvmField val LBRACKET = SlangTokenType("[")
    @JvmField val RBRACKET = SlangTokenType("]")
    @JvmField val SEMICOLON = SlangTokenType(";")
    @JvmField val COMMA = SlangTokenType(",")
    @JvmField val DOT = SlangTokenType(".")
    @JvmField val OPERATOR = SlangTokenType("OPERATOR")

    @JvmField val COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT)
    @JvmField val STRINGS = TokenSet.create(STRING)
    @JvmField val BRACES = TokenSet.create(LBRACE, RBRACE)
    @JvmField val PARENS = TokenSet.create(LPAREN, RPAREN)
    @JvmField val BRACKETS = TokenSet.create(LBRACKET, RBRACKET)

    /** Control-flow and declaration keywords. Type-name keywords are in [BUILTIN_TYPES]. */
    @JvmField
    val KEYWORDS: Set<String> = hashSetOf(
        "if", "else", "for", "while", "do", "switch", "case", "default", "break", "continue",
        "return", "discard", "defer",
        "struct", "class", "interface", "enum", "namespace", "typedef", "typealias", "associatedtype",
        "extension", "__generic", "where", "this", "This", "super",
        "public", "private", "internal", "protected", "in", "out", "inout", "ref", "const", "static",
        "uniform", "groupshared", "extern", "export", "import", "module", "implementing", "__include",
        "property", "get", "set", "func", "let", "var", "new", "nullptr", "null", "none",
        "true", "false", "sizeof", "alignof", "countof", "as", "is", "__subscript", "__init",
        "no_diff", "fwd_diff", "bwd_diff", "dyn", "each", "expand",
        "row_major", "column_major", "precise", "nointerpolation", "linear", "centroid", "sample",
        "shader", "numthreads", "register", "packoffset",
    )

    /** Built-in scalar / container type keywords, highlighted as types. */
    @JvmField
    val BUILTIN_TYPES: Set<String> = hashSetOf(
        "void", "bool", "int", "uint", "float", "double", "half",
        "int8_t", "int16_t", "int32_t", "int64_t",
        "uint8_t", "uint16_t", "uint32_t", "uint64_t",
        "float16_t", "float32_t", "float64_t",
        "bool1", "bool2", "bool3", "bool4",
        "int1", "int2", "int3", "int4",
        "uint1", "uint2", "uint3", "uint4",
        "float1", "float2", "float3", "float4",
        "double1", "double2", "double3", "double4",
        "half1", "half2", "half3", "half4",
        "float2x2", "float3x3", "float4x4", "float3x4", "float4x3",
        "matrix", "vector",
        "Texture1D", "Texture2D", "Texture3D", "TextureCube", "Texture2DArray", "TextureCubeArray",
        "RWTexture1D", "RWTexture2D", "RWTexture3D",
        "SamplerState", "SamplerComparisonState",
        "Buffer", "RWBuffer", "ByteAddressBuffer", "RWByteAddressBuffer",
        "StructuredBuffer", "RWStructuredBuffer", "AppendStructuredBuffer", "ConsumeStructuredBuffer",
        "ConstantBuffer", "ParameterBlock", "cbuffer", "tbuffer",
        "Array", "Optional", "Ptr", "NativeRef", "Atomic", "DiffPair",
    )
}
