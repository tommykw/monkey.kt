package com.github.tommykw.monkey

class Lexer(private val input: String) {

    companion object {
        val whiteSpaces = listOf(' ', '\n')
    }

    private var position: Int = 0
    private var readPosition: Int = 0
    private var char: Char = 0.toChar()

    init {
        readChar()
    }

    fun nextToken(): Token {
        skipWhiteSpace()

        var readNextChar = true
        val token = when (char) {
            '=' -> TokenType.EQ.token()
            ';' -> TokenType.SEMICOLON.token()
            ',' -> TokenType.COMMA.token()
            ':' -> TokenType.COLON.token()
            '.' -> TokenType.DOT.token()
            '[' -> TokenType.LEFT_BRACKET.token()
            ']' -> TokenType.RIGHT_BRACKET.token()
            '(' -> TokenType.LEFT_PAREN.token()
            ')' -> TokenType.RIGHT_PAREN.token()
            '{' -> TokenType.LEFT_BRACE.token()
            '}' -> TokenType.RIGHT_BRACE.token()
            '+' -> TokenType.PLUS.token()
            '-' -> TokenType.MINUS.token()
            '*' -> TokenType.ASTERISK.token()
            '/' -> TokenType.SLASH.token()
            '<' -> TokenType.LT.token()
            '>' -> TokenType.GT.token()
            '!' -> TokenType.BANG.token()
            0.toChar() -> Token(TokenType.EOF, "")
            else -> {
                when {
                    char.isIdentifier() -> {
                        val identifier = readIdentifier()
                        readNextChar = false
                        Token(identifier.lookupIdentifier(), identifier)
                    }
                    char.isDigit() -> {
                        readNextChar = false
                        Token(TokenType.NUMBER, readNumber())
                    }
                    else -> {
                        Token(TokenType.ILLEGAL, char.toString())
                    }
                }
            }
        }

        if (readNextChar) {
            readChar()
        }

        return token
    }

    private fun readChar() {
        char = peakChar()
        position = readPosition
        readPosition++
    }

    private fun peakChar(): Char {
        return if (readPosition >= input.length) {
            0.toChar()
        } else {
            input[readPosition]
        }
    }

    private fun skipWhiteSpace() {
        while (whiteSpaces.contains(char)) {
            readChar()
        }
    }

    private fun readNumber(): String = readValue { c -> c.isDigit() }

    private fun readIdentifier(): String = readValue { c -> c.isIdentifier() }

    private fun readValue(predicate: (Char) -> Boolean): String {
        val currentPosition = position
        while (predicate(char)) {
            readChar()
        }
        return input.substring(currentPosition, position)
    }

    private fun TokenType.token() = Token(this, char)

    private fun Char.isIdentifier() = this.isLetter() || this == '_'

    override fun toString(): String {
        return "Lexer(input=$input, char=$char, position=$position, readPosition=$readPosition)"
    }
}