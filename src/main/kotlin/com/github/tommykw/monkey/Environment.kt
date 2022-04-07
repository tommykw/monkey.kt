package com.github.tommykw.monkey

class Environment(val store: MutableMap<String, MonkeyObject>, val outer: Environment?) {
    companion object {
        fun new(): Environment {
            return Environment(mutableMapOf(), null)
        }

        fun newEnclosed(outer: Environment): Environment {
            return Environment(mutableMapOf(), outer)
        }
    }

    operator fun set(name: String, value: MonkeyObject) {
        store[name] = value
    }

    fun put(name: String, value: MonkeyObject): MonkeyObject {
        this[name] = value
        return value
    }

    operator fun get(name: String): MonkeyObject? {
        val obj = store[name]
        if (obj == null && outer != null) {
            return outer[name]
        }
        return obj
    }
}