package com.github.tommykw.monkey

enum class TokenType(val value: String) {
    // Single character tokens
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]"),
    COMMA(","),
    COLON(":"),
    DOT("."),
    SEMICOLON(";"),
    PLUS("+"),
    MINUS("-"),
    SLASH("/"),
    ASTERISK("*"),

    // One or two or three character tokens
    EQ("="),
    EQ_EQ("=="),
    EQ_EQ_EQ("==="),
    BANG("!"),
    BANG_EQ("!="),
    BANG_EQ_EQ("!=="),
    LT("<"),
    LT_EQ("<="),
    GT(">"),
    GT_EQ(">="),
    AND("&&"),

    // Literals
    IDENTIFIER("IDENTIFIER"),
    STRING("STRING"),
    NUMBER("NUMBER"),

    // Keywords
    FUNCTION("FUNCTION"),
    CLASS("CLASS"),
    LET("LET"),
    VAR("VAR"),
    CONST("CONST"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    IF("IF"),
    ELSE("ELSE"),
    RETURN("RETURN"),
    WHILE("WHILE"),
    FOR("FOR"),
    NULL("NULL"),
    UNDEFINED("UNDEFINED"),
    INTERFACE("INTERFACE"),
    THIS("THIS"),
    AS("AS"),
    TYPE("TYPE"),

    ILLEGAL("ILLEGAL"),
    EOF("EOF");

    companion object {
        val keywords = mapOf(
            "function" to FUNCTION,
            "class" to CLASS,
            "let" to LET,
            "var" to VAR,
            "const" to CONST,
            "true" to TRUE,
            "false" to FALSE,
            "if" to IF,
            "else" to ELSE,
            "return" to RETURN,
            "while" to WHILE,
            "for" to FOR,
            "null" to NULL,
            "undefined" to UNDEFINED,
            "interface" to INTERFACE,
            "this" to THIS,
            "as" to AS,
            "type" to TYPE,
        )
    }
}

fun String.lookupIdentifier(): TokenType {
    return TokenType.keywords[this] ?: TokenType.IDENTIFIER
}

data class Token(val type: TokenType, val literal: String) {
    constructor(type: TokenType, literal: Char) : this(type, literal.toString())
}
