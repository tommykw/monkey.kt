package com.github.tommykw.monkey

enum class ObjectType {
    INTEGER,
    BOOLEAN,
    NULL,
    RETURN_VALUE,
    ERROR,
    FUNCTION,
    STRING
}

interface MonkeyObject {
    fun getType(): ObjectType
    fun inspect(): String
}

interface MonkeyValue<T> : MonkeyObject {
    val value: T

    override fun inspect(): String {
        return value.toString()
    }
}

class MonkeyInt(override val value: Long) : MonkeyValue<Long> {
    override fun getType(): ObjectType {
        return ObjectType.INTEGER
    }

    operator fun compareTo(other: MonkeyInt): Int {
        return value.compareTo(other.value)
    }

    operator fun plus(other: MonkeyInt): MonkeyInt {
        return MonkeyInt(value + other.value)
    }

    operator fun minus(other: MonkeyInt): MonkeyInt {
        return MonkeyInt(value - other.value)
    }

    operator fun times(other: MonkeyInt): MonkeyInt {
        return MonkeyInt(value * other.value)
    }

    operator fun div(other: MonkeyInt): MonkeyInt {
        return MonkeyInt(value / other.value)
    }

    operator fun unaryMinus(): MonkeyInt {
        return MonkeyInt(-value)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is MonkeyInt) {
            value == other.value
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class MonkeyBoolean(override val value: Boolean) : MonkeyValue<Boolean> {
    override fun getType(): ObjectType {
        return ObjectType.BOOLEAN
    }
}

class MonkeyReturnValue(val value: MonkeyObject) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.RETURN_VALUE
    }

    override fun inspect(): String {
        return value.inspect()
    }
}

class MonkeyError(val message: String) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.ERROR
    }

    override fun inspect(): String {
        return "ERROR: $message"
    }

    override fun toString(): String {
        return "MonkeyError($message)"
    }
}

object MonkeyNull : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.NULL
    }

    override fun inspect(): String {
        return "null"
    }
}

class MonkeyFunction(val parameters: List<Identifier>?, val body: BlockStatement, val env: Environment) : MonkeyObject {
    override fun getType(): ObjectType {
        return ObjectType.FUNCTION
    }

    override fun inspect(): String {
        return "fn(${parameters.joinToString(", ")})"
    }
}