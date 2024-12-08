package slang.plugin.language.parser

import org.intellij.markdown.lexer.push

class SlangIdentifierLookup {

    enum class IdentifierStyle {
        None, ///< It's not an identifier

        Identifier, ///< Just an identifier

        PreDeclare, ///< Declare a type (not visible in C++ code)
        TypeSet,    ///< TypeSet

        TypeModifier, ///< const, volatile etc
        Keyword,      ///< A keyword C/C++ keyword that is not another type

        Class,     ///< class
        Struct,    ///< struct
        Namespace, ///< namespace
        Enum,      ///< enum

        TypeDef, ///< typedef

        Access, ///< public, protected, private

        Reflected,
        Unreflected,

        CallingConvention, ///< Used on a method
        Virtual,           ///<

        Template,

        Static,

        IntegerModifier,

        Extern,

        CallableMisc, ///< For SLANG_NO_THROW etc

        IntegerType, ///< Built in integer type

        Default, /// default
    }

    enum class IdentifierFlags(val value: Int) {
        StartScope(0x1), ///< namespace, struct or class
        ClassLike(0x2), ///< Struct or class
        Keyword(0x4),
        Reflection(0x8),
    }

    data object Pair {
        var name = ""
        var style = IdentifierStyle.None
    }

    data object LookupResult {
        var identifier = IdentifierStyle.Identifier
    }

    fun set(name: String, style: IdentifierStyle) {
        if (pool.containsKey(name)) {
            styles[pool[name]!!] = style
        } else {
            val index = styles.size
            styles.push(style)
            pool[name] = index
        }
    }

    fun set(pairs: Iterable<Pair>) {
        for (pair in pairs) {
            set(pair.name, pair.style)
        }
    }

    fun set(names: Iterable<String>, style: IdentifierStyle) {
        for (name in names) {
            set(name, style)
        }
    }

    fun reset() {
        pool.clear()
        styles.clear()
    }

    fun initDefault(markPrefix: String) {
        reset()

        // Some keywords
        let {
            val names = arrayListOf(
                "continue",
                "if",
                "case",
                "break",
                "catch",
                "delete",
                "do",
                "else",
                "for",
                "new",
                "goto",
                "return",
                "switch",
                "throw",
                "using",
                "while",
                "operator",
                "explicit"
            )
            set(names, IdentifierStyle.Keyword)
        }

        // Some type modifiers
        let {
            val names = arrayListOf("const", "volatile")
            set(names, IdentifierStyle.TypeModifier)
        }

        // Special markers
        let {
            set("${markPrefix}PRE_DECLARE", IdentifierStyle.PreDeclare)
            set("${markPrefix}TYPE_SET", IdentifierStyle.TypeSet)
            set("${markPrefix}REFLECTED", IdentifierStyle.Reflected)
            set("${markPrefix}UNREFLECTED", IdentifierStyle.Unreflected)
        }

        let {
            set("virtual", IdentifierStyle.Virtual)
            set("template", IdentifierStyle.Template)
            set("static", IdentifierStyle.Static)
            set("extern", IdentifierStyle.Extern)
            set("default", IdentifierStyle.Default)
        }

        let {
            val names = arrayListOf("char", "short", "int", "long")
            set(names, IdentifierStyle.IntegerType)
        }

        let {
            set("SLANG_MCALL", IdentifierStyle.CallingConvention)
        }

        let {
            val names = arrayListOf("SLANG_NO_THROW", "inline")
            set(names, IdentifierStyle.CallableMisc)
        }

        // Keywords which introduce types/scopes
        let {
            set("struct", IdentifierStyle.Struct)
            set("class", IdentifierStyle.Class)
            set("namespace", IdentifierStyle.Namespace)
            set("enum", IdentifierStyle.Enum)
            set("typedef", IdentifierStyle.TypeDef)
        }

        // Keywords that control access
        let {
            val names = arrayListOf("private", "protected", "public")
            set(names, IdentifierStyle.Access)
        }
        let {
            val names = arrayListOf("signed", "unsigned")
            set(names, IdentifierStyle.IntegerModifier)
        }
    }

    fun lookUp(name: String): LookupResult? {
        if (!pool.containsKey(name)) {
            return null
        }

        val result = LookupResult
        result.identifier = styles[pool[name]!!]
        return result
    }

    private val pool = HashMap<String, Int>()
    private val styles = ArrayList<IdentifierStyle>()
    
    private val flags = arrayOf<Int>(
        0,                       /// None
        0,                       /// Identifier
        0,                       /// Declare type
        0,                       /// Type set
        IdentifierFlags.Keyword.value, /// TypeModifier
        IdentifierFlags.Keyword.value, /// Keyword

        IdentifierFlags.Keyword.value or IdentifierFlags.StartScope.value or IdentifierFlags.ClassLike.value, /// Class
        IdentifierFlags.Keyword.value or IdentifierFlags.StartScope.value or IdentifierFlags.ClassLike.value, /// Struct
        IdentifierFlags.Keyword.value or IdentifierFlags.StartScope.value, /// Namespace
        IdentifierFlags.Keyword.value or IdentifierFlags.StartScope.value, /// Enum
    
        IdentifierFlags.Keyword.value, /// Typedef
    
        IdentifierFlags.Keyword.value,    /// Access
        IdentifierFlags.Reflection.value, /// Reflected
        IdentifierFlags.Reflection.value, /// Unreflected
    
        IdentifierFlags.Keyword.value, /// virtual
        0,                       /// Calling convention
        IdentifierFlags.Keyword.value, /// template
        IdentifierFlags.Keyword.value, /// static
    
        IdentifierFlags.Keyword.value, /// unsigned/signed
    
        IdentifierFlags.Keyword.value, /// extern
    
        0, /// Callable misc
        0, /// IntegerType int, short, char, long
    
        IdentifierFlags.Keyword.value, /// default
    )
}