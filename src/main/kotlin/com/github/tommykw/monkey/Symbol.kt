package com.github.tommykw.monkey

enum class SymbolScope {
    GLOBAL,
    LOCAL,
}

data class Symbol(val name: String, val scope: SymbolScope, val index: Int)

class SymbolTable(
    val store: MutableMap<String, Symbol> = mutableMapOf(),
    val outer: SymbolTable? = null
) {
    var numDefinitions: Int = 0
    val freeSymbols = mutableListOf<Symbol>()

    fun define(name: String): Symbol {
        val scope = if (outer == null) {
            SymbolScope.GLOBAL
        } else {
            SymbolScope.LOCAL
        }
        val symbol = Symbol(name, scope, numDefinitions)
        store[name] = symbol
        numDefinitions++
        return symbol
    }

}

class SymbolException(message: String) : Exception(message)
