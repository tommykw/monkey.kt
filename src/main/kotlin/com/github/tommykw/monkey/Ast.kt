package com.github.tommykw.monkey

interface Node {
    val literal: String
    override fun toString(): String
}

interface Statement : Node
interface Expression : Node

class Program(val statements: List<Statement>) : Node {
    override val literal: String
        get() = if (statements.isEmpty()) "" else statements.first().literal

    override fun toString(): String {
        return statements.joinToString("")
    }
}

interface ExpressionToken : Expression {
    val token: Token
    override val literal: String
        get() = token.literal
}

interface StatementToken : Statement {
    val token: Token
    override val literal: String
        get() = token.literal
}

class Identifier(
    override val token: Token,
    override val literal: String
) : ExpressionToken {

    override fun toString(): String {
        return literal
    }
}

class ConstStatement(
    override val token: Token,
    val name: Identifier,
    val value: Expression?
) : StatementToken {

    override fun toString(): String {
        return "$literal $name = ${value?.toString() ?: ""};"
    }
}

abstract class LiteralExpression<T>(
    override val token: Token,
    val value: T
) : ExpressionToken

class NumberLiteral(
    token: Token,
    value: Long
) : LiteralExpression<Long>(token, value) {

    override fun toString(): String {
        return token.literal
    }
}

class StringLiteral(
    token: Token,
    value: String
) : LiteralExpression<String>(token, value) {

    override fun toString(): String {
        return token.literal
    }
}

class BooleanLiteral(
    token: Token,
    value: Boolean
) : LiteralExpression<Boolean>(token, value) {

    override fun toString(): String {
        return token.literal
    }
}

class ReturnStatement(
    override val token: Token,
    val returnValue: Expression?
) : StatementToken {

    override fun toString(): String {
        return "$literal $returnValue"
    }
}

class ExpressionStatement(
    override val token: Token,
    val expression: Expression?
) : StatementToken {

    override fun toString(): String {
        return expression?.toString() ?: ""
    }
}

class PrefixExpression(
    override val token: Token,
    val operator: String,
    val right: Expression?
) : ExpressionToken {

    override fun toString(): String {
        return "{$operator$right}"
    }
}

class InfixExpression(
    override val token: Token,
    val left: Expression?,
    val operator: String,
    val right: Expression?
) : ExpressionToken {

    override fun toString(): String {
        return "{$left $operator $right}"
    }
}

class CallExpression(
    override val token: Token,
    val function: Expression?,
    val arguments: List<Expression?>?
) : ExpressionToken {

    override fun toString(): String {
        return "$function ${arguments?.joinToString()}"
    }
}

class BlockStatement(
    override val token: Token,
    val statements: List<Statement?>
) : StatementToken {

    override fun toString(): String {
        return statements.joinToString("") ?: ""
    }
}

class IfExpression(
    override val token: Token,
    val condition: Expression?,
    val consequence: BlockStatement,
    val alternative: BlockStatement?
) : ExpressionToken {

    override fun toString(): String {
        return "if $condition $consequence ${if (alternative != null) "else $alternative" else ""}"
    }
}

class FunctionLiteral(
    override val token: Token,
    val parameters: List<Identifier>?,
    val body: BlockStatement
) : ExpressionToken {

    override fun toString(): String {
        return "${literal} ${parameters?.joinToString()} $body"
    }
}