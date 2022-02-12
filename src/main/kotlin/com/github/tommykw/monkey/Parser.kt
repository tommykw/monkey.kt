package com.github.tommykw.monkey

class Parser(val lexer: Lexer) {

    enum class Precedence {
        LOWEST,
        EQUALS,
        LESS_GREATER,
        SUM,
        PRODUCT,
        PREFIX,
        CALL
    }

    private val errors: MutableList<String> = mutableListOf()

    private lateinit var currentToken: Token
    private lateinit var peekToken: Token

    private val prefixParsers = mutableMapOf<TokenType, () -> Expression?>()
    private val infixParsers = mutableMapOf<TokenType, (Expression?) -> Expression?>()

    private val precedences = mapOf(
        TokenType.EQ to Precedence.EQUALS,
        TokenType.BANG_EQ to Precedence.EQUALS,
        TokenType.LT to Precedence.LESS_GREATER,
        TokenType.GT to Precedence.LESS_GREATER,
        TokenType.PLUS to Precedence.SUM,
        TokenType.MINUS to Precedence.SUM,
        TokenType.SLASH to Precedence.PRODUCT,
        TokenType.ASTERISK to Precedence.PRODUCT,
        TokenType.LEFT_PAREN to Precedence.CALL
    )

    init {
        nextToken()
        nextToken()

        prefixParsers[TokenType.NUMBER] = ::parseNumberLiteral
        prefixParsers[TokenType.TRUE] = ::parseBooleanLiteral
        prefixParsers[TokenType.FALSE] = ::parseBooleanLiteral
        prefixParsers[TokenType.IDENTIFIER] = ::parseIdentifier
        prefixParsers[TokenType.BANG] = ::parsePrefixExpression
        prefixParsers[TokenType.MINUS] = ::parsePrefixExpression
        prefixParsers[TokenType.LEFT_PAREN] = ::parseIfExpression
        prefixParsers[TokenType.IF] = ::parseIfExpression
        prefixParsers[TokenType.FUNCTION] = ::parseFunctionLiteral

        infixParsers[TokenType.PLUS] = ::parseInfixExpression
        infixParsers[TokenType.MINUS] = ::parseInfixExpression
        infixParsers[TokenType.SLASH] = ::parseInfixExpression
        infixParsers[TokenType.ASTERISK] = ::parseInfixExpression
        infixParsers[TokenType.EQ] = ::parseInfixExpression
        infixParsers[TokenType.BANG_EQ] = ::parseInfixExpression
        infixParsers[TokenType.LT] = ::parseInfixExpression
        infixParsers[TokenType.GT] = ::parseInfixExpression
        infixParsers[TokenType.LEFT_PAREN] = ::parseInfixExpression
    }

    private fun nextToken() {
        if (this::peekToken.isInitialized) {
            currentToken = peekToken
        }

        peekToken = lexer.nextToken()
    }

    fun parseProgram(): Program {
        val statements = mutableListOf<Statement>()
        while (currentToken.type != TokenType.EOF) {
            val statement = parseStatement()
            if (statement != null) {
                statements.add(statement)
            }
            nextToken()
        }
        return Program(statements)
    }

    private fun parseStatement(): Statement? {
        return when (currentToken.type) {
            TokenType.CONST -> parseConstStatement()
            TokenType.RETURN -> parseReturnStatement()
            else -> parseExpressionStatement()
        }
    }

    private fun parseReturnStatement(): Statement {
        val token = currentToken
        nextToken()

        val returnValue = parseExpression(Precedence.LOWEST)

        while (currentToken.type != TokenType.SEMICOLON) {
            nextToken()
        }

        return ReturnStatement(token, returnValue)
    }

    private fun parseExpressionStatement(): Statement {
        val token = currentToken
        val expression = parseExpression(Precedence.LOWEST)

        if (peekToken.type == TokenType.SEMICOLON) {
            nextToken()
        }

        return ExpressionStatement(token, expression)
    }

    private fun parseConstStatement(): Statement? {
        val token = currentToken
        if (!expectPeek(TokenType.IDENTIFIER)) {
            return null
        }

        val name = Identifier(currentToken, currentToken.literal)

        if (!expectPeek(TokenType.EQ)) {
            return null
        }

        nextToken()

        val value = parseExpression(Precedence.LOWEST)

        if (peekToken.type == TokenType.SEMICOLON) {
            nextToken()
        }

        return ConstStatement(token, name, value)
    }

    private fun parseExpression(precedence: Precedence): Expression? {
        val prefix = prefixParsers[currentToken.type]
        if (prefix == null) {
            errors.add("No prefix parser for ${currentToken.type} function")
            return null
        }

        var left = prefix()

        while (peekToken.type != TokenType.SEMICOLON && precedence < peekPrecedence()) {
            val infix = infixParsers[peekToken.type] ?: return left

            nextToken()

            left = infix(left)
        }

        return left
    }

    private fun peekPrecedence(): Precedence {
        return findPrecedence(peekToken.type)
    }

    private fun findPrecedence(type: TokenType): Precedence {
        return precedences[type] ?: Precedence.LOWEST
    }


    private fun expectPeek(type: TokenType): Boolean {
        return if (peekToken.type == type) {
            nextToken()
            true
        } else {
            errors.add("expected next token to be $type")
            false
        }
    }

    private fun parseNumberLiteral(): Expression {
        val token = currentToken
        val value = token.literal.toLong()
        return NumberLiteral(token, value)
    }

    private fun parseBooleanLiteral(): Expression? {
        return BooleanLiteral(currentToken, currentToken.type == TokenType.TRUE)
    }

    private fun parseIdentifier(): Expression {
        return Identifier(currentToken, currentToken.literal)
    }

    private fun parsePrefixExpression(): Expression {
        val token = currentToken
        val literal = token.literal

        nextToken()

        val right = parseExpression(Precedence.PREFIX)
        return PrefixExpression(token, literal, right)
    }

    private fun parseInfixExpression(left: Expression?): Expression {
        val token = currentToken
        val literal = token.literal
        val precedence = findPrecedence(currentToken.type)
        val right = parseExpression(precedence)
        return InfixExpression(token, left, literal, right)
    }

    private fun parseGroupExpression(): Expression? {
        nextToken()

        val expression = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RIGHT_PAREN)) {
            return null
        }

        return expression
    }

    private fun parseIfExpression(): Expression? {
        val token = currentToken

        if (!expectPeek(TokenType.LEFT_PAREN)) {
            return null
        }

        nextToken()

        val condition = parseExpression(Precedence.LOWEST)

        if (!expectPeek(TokenType.RIGHT_PAREN)) {
            return null
        }

        if (!expectPeek(TokenType.LEFT_BRACE)) {
            return null
        }

        val consequence = parseBlockStatement()

        val alternative = if (peekToken.type == TokenType.ELSE) {
            nextToken()

            if (!expectPeek(TokenType.LEFT_BRACE)) {
                return null
            }

            parseBlockStatement()
        } else {
            null
        }

        return IfExpression(token, condition, consequence, alternative)
    }

    private fun parseBlockStatement(): BlockStatement {
        val token = currentToken
        val statements = mutableListOf<Statement?>()

        nextToken()

        while (currentToken.type != TokenType.RIGHT_BRACE && currentToken.type != TokenType.EOF) {
            val statement = parseStatement()
            if (statement != null) {
                statements.add(statement)
            }
            nextToken()
        }

        return BlockStatement(token, statements)
    }

    private fun parseCallExpression(expression: Expression?): Expression {
        val token = currentToken
        val arguments = parseCallArguments()
        return CallExpression(token, expression, arguments)
    }

    private fun parseCallArguments(): List<Expression?>? {
        val arguments = mutableListOf<Expression?>()

        if (peekToken.type == TokenType.RIGHT_PAREN) {
            nextToken()
            return arguments
        }

        nextToken()
        arguments.add(parseExpression(Precedence.LOWEST))

        while (peekToken.type == TokenType.COMMA) {
            nextToken()
            nextToken()
            arguments.add(parseExpression(Precedence.LOWEST))
        }

        if (!expectPeek(TokenType.RIGHT_PAREN)) {
            return null
        }

        return arguments
    }

    private fun parseFunctionLiteral(): Expression? {
        val token = currentToken

        if (!expectPeek(TokenType.LEFT_PAREN)) {
            return null
        }

        val parameters = parseFunctionParameters()

        if (!expectPeek(TokenType.LEFT_BRACE)) {
            return null
        }

        val body = parseBlockStatement()
        return FunctionLiteral(token, parameters, body)
    }

    private fun parseFunctionParameters(): List<Identifier>? {
        val parameters = mutableListOf<Identifier>()

        if (peekToken.type == TokenType.RIGHT_PAREN) {
            nextToken()
            return parameters
        }

        nextToken()

        val token = currentToken

        parameters.add(Identifier(token, token.literal))

        while (peekToken.type == TokenType.COMMA) {
            nextToken()
            nextToken()

            val currentToken = currentToken
            parameters.add(Identifier(currentToken, currentToken.literal))
        }

        if (!expectPeek(TokenType.RIGHT_PAREN)) {
            return null
        }

        return parameters
    }

}