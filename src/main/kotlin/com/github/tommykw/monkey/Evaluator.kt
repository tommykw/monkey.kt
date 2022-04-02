package com.github.tommykw.monkey

class Evaluator {

    fun eval(node: Node?, environment: Environment): MonkeyObject? {
        return when (node) {
            is Program -> evalProgram(node.statements, environment)
            is ExpressionStatement -> eval(node.expression, environment)
            is NumberLiteral -> MonkeyInt(node.value)
            is PrefixExpression -> {
                val result = eval(node.right, environment)
                if (result != null) {
                    if (result.getType() == ObjectType.ERROR) {
                        result
                    } else {
                        evalPrefixExpression(node.operator, result)
                    }
                } else {
                    result
                }
            }
            is InfixExpression -> {
                val left = eval(node.left, environment)
                val right = eval(node.right, environment)
                if (left != null && right != null) {
                    if (left.getType() == ObjectType.ERROR || right.getType() == ObjectType.ERROR) {
                        left
                    } else {
                        evalInfixExpression(node.operator, left, right)
                    }
                } else {
                    null
                }
            }
            is BooleanLiteral -> {
                val result = node.value
                if (result) {
                    MonkeyBoolean(true)
                } else {
                    MonkeyBoolean(false)
                }
            }
            is IfExpression -> {
                evalIfExpression(node, environment)
            }
            is BlockStatement -> {
                evalBlockStatement(node, environment)
            }
            is ReturnStatement -> {
                val result = eval(node.returnValue, environment)
                if (result != null) {
                    MonkeyReturnValue(result)
                } else {
                    null
                }
            }
            is ConstStatement -> {
                val result = eval(node.value, environment)
                if (result != null) {
                    environment.put(node.literal, result)
                } else {
                    null
                }
            }
            is Identifier -> evalIdentifier(node, environment)
            is FunctionLiteral -> MonkeyFunction(node.parameters, node.body, environment)
            is CallExpression -> {
                val evaluated = eval(node.function, environment)
                if (evaluated != null) {
                    if (evaluated.getType() == ObjectType.ERROR) {
                        evaluated
                    } else {
                        val args = evalExpressions(node.arguments, environment)
                        if (args.size == 1 && args.first()?.getType() == ObjectType.ERROR) {
                            args.first()
                        } else {
                            applyFunction(evaluated, args)
                        }
                    }
                } else {
                    null
                }
            }
            else -> null
        }

    }

    private fun evalProgram(statements: List<Statement>, environment: Environment): MonkeyObject? {
        var result: MonkeyObject? = null

        for (statement in statements) {
            result = eval(statement, environment)

            when (result) {
                is MonkeyReturnValue -> return result.value
                is MonkeyError -> return result
            }
        }

        return result
    }

    private fun evalPrefixExpression(operator: String, right: MonkeyObject): MonkeyObject? {
        return when (operator) {
            "!" -> evalBangOperatorExpression(right)
            "-" -> evalMinusPrefixOperatorExpression(right)
            else -> MonkeyError("unknown operator: $operator ${right.getType()}")
        }
    }

    private fun evalBangOperatorExpression(right: MonkeyObject): MonkeyObject {
        return when (right) {
            MonkeyBoolean(true) -> MonkeyBoolean(false)
            MonkeyBoolean(false) -> MonkeyBoolean(true)
            MonkeyNull -> MonkeyBoolean(true)
            else -> MonkeyBoolean(false)
        }
    }

    private fun evalMinusPrefixOperatorExpression(right: MonkeyObject?): MonkeyObject? {
        return if (right != null) {
            if (right.getType() != ObjectType.INTEGER) {
                MonkeyError("unknown operator: -${right.getType()}")
            } else {
                return -(right as MonkeyInt)
            }
        } else {
            null
        }
    }

    private fun evalInfixExpression(operator: String, left: MonkeyObject, right: MonkeyObject): MonkeyObject {
        return when {
            left.getType() == ObjectType.INTEGER && right.getType() == ObjectType.INTEGER -> evalIntegerInfixExpression(operator, left, right)
            operator == "==" -> {
                val result = left == right
                if (result) {
                    MonkeyBoolean(true)
                } else {
                    MonkeyBoolean(false)
                }
            }
            operator == "!=" -> {
                val result = left != right
                if (result) {
                    MonkeyBoolean(true)
                } else {
                    MonkeyBoolean(false)
                }
            }
            else -> MonkeyError("unknown operator: $operator ${left.getType()} ${right.getType()}")
        }
    }

    private fun evalIntegerInfixExpression(operator: String, left: MonkeyObject, right: MonkeyObject): MonkeyObject {
        val leftInt = left as MonkeyInt
        val rightInt = right as MonkeyInt

        return when (operator) {
            "+" -> leftInt + rightInt
            "-" -> leftInt - rightInt
            "*" -> leftInt * rightInt
            "/" -> leftInt / rightInt
            "<" -> {
                val result = leftInt < rightInt
                if (result) {
                    MonkeyBoolean(true)
                } else {
                    MonkeyBoolean(false)
                }
            }
            ">" -> {
                val result = leftInt > rightInt
                if (result) {
                    MonkeyBoolean(true)
                } else {
                    MonkeyBoolean(false)
                }
            }
            "==" -> {
                val result = leftInt == rightInt
                if (result) {
                    MonkeyBoolean(true)
                } else {
                    MonkeyBoolean(false)
                }
            }
            "!=" -> {
                val result = leftInt != rightInt
                if (result) {
                    MonkeyBoolean(true)
                } else {
                    MonkeyBoolean(false)
                }
            }
            else -> MonkeyError("unknown operator: $operator ${leftInt.getType()} ${rightInt.getType()}")
        }
    }

    private fun evalIfExpression(ifExpression: IfExpression, environment: Environment): MonkeyObject? {
        val condition = eval(ifExpression.condition, environment)

        fun isTruthy(obj: MonkeyObject?): Boolean {
            return when (obj) {
                MonkeyBoolean(true) -> true
                MonkeyBoolean(false) -> false
                MonkeyNull -> false
                else -> true
            }
        }

        return when {
            isTruthy(condition) -> eval(ifExpression.consequence, environment)
            ifExpression.alternative != null -> eval(ifExpression.alternative, environment)
            else -> null
        }
    }

    private fun evalBlockStatement(node: BlockStatement, environment: Environment): MonkeyObject? {
        var result: MonkeyObject? = null
        for (statement in node.statements) {
            result = eval(statement, environment)
            if (result != null && result.getType() == ObjectType.RETURN_VALUE) {
                return result
            }
        }
        return result
    }

    private fun evalIdentifier(node: Identifier, environment: Environment): MonkeyObject {
        val value = environment[node.literal]
        if (value == null) {
            MonkeyError("identifier not found: ${node.literal}")
        }
        return value!!
    }

    private fun evalExpressions(arguments: List<Expression?>?, environment: Environment): List<MonkeyObject?> {
        return arguments!!.map {
            val evaluated = eval(it, environment)
            evaluated
        }
    }

    private fun applyFunction(function: MonkeyObject, arguments: List<MonkeyObject?>): MonkeyObject? {
        return when (function) {
            is MonkeyFunction -> {
                val extendedEnvironment = extendFunctionEnvironment(function, arguments)
                val evaluated = eval(function.body, extendedEnvironment)
                unwrapReturnValue(evaluated)
            }
            else -> MonkeyError("not a function: ${function.getType()}")
        }
    }

    private fun extendFunctionEnvironment(function: MonkeyFunction, arguments: List<MonkeyObject?>): Environment {
        val environment = Environment.newEnclosed(function.env)
        function.parameters?.forEachIndexed { i, identifier ->
            environment[identifier.literal] = arguments[i]!!
        }
        return environment
    }

    private fun unwrapReturnValue(obj: MonkeyObject?): MonkeyObject? {
        return when (obj) {
            is MonkeyReturnValue -> obj.value
            else -> obj
       	}
    }
}