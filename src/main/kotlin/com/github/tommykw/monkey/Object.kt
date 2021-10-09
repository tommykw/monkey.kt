package com.github.tommykw.monkey

interface MObject {
    fun inspect(): String
}

interface MValue<T> : MObject {
    val value: T

    override fun inspect(): String {
        return value.toString()
    }
}

class MInt(override val value: Long) : MValue<Long>, Hashable<Long> {

    override fun equals(other: Any?): Boolean {
        return if (other is MInt) {
            value == other.value
        } else {
            false
        }
    }

    override fun toString(): String {
        return "MInt(value=${value})"
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun hashType(): HashType {
        return HashType.INT
    }
}

class MBoolean(override val value: Boolean) : MValue<Boolean>, Hashable<Boolean> {
    override fun hashType(): HashType {
        return HashType.BOOLEAN
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MBoolean) return false
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class MNull : MObject {
    override fun inspect(): String {
        return "MNull"
    }
}

class MString(override val value: String) : MValue<String>, Hashable<String> {
    operator fun plus(other: MString): MString {
        return MString(value + other.value)
    }

    override fun hashType(): HashType {
        return HashType.STRING
    }
}

class MArray(val elements: List<MObject>) : MObject {
    override fun inspect(): String {
        return "[${elements.joinToString(separator = ", ")}]"
    }
}


interface Hashable<T> : MValue<T> {
    fun hashKey(): HashKey = HashKey(hashType(), value.hashCode())

    fun hashType(): HashType
}

enum class HashType {
    INT,
    BOOLEAN,
    STRING
}

data class HashKey(val type: HashType, val value: Int)