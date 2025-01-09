package slang.plugin.language.parser.data

data class MacroExpansion(var content: ArrayList<TokenData> = arrayListOf()) {

    enum class Type { Default, FunctionLike }

    var type: Type = Type.Default

    val arguments: ArrayList<MacroArgument> = arrayListOf()
}
