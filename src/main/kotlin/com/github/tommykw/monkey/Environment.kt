package com.github.tommykw.monkey

class Environment(val store: MutableMap<String, Any>, val outer: Environment?) {
    companion object {
        fun new(): Environment {
            return Environment(mutableMapOf(), null)
        }

        fun newEnclosed(outer: Environment): Environment {
            return Environment(mutableMapOf(), outer)
        }
    }

    operator fun set(name: String, value: Any) {
        store[name] = value
    }

    fun put(name: String, value: Any): Any {
        this[name] = value
        return value
    }

    operator fun get(name: String): Any? {
        val obj = store[name]
        if (obj == null && outer != null) {
            return outer[name]
        }
        return obj
    }
}