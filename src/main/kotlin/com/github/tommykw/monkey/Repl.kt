package com.github.tommykw.monkey

import java.io.InputStream
import java.io.PrintStream
import java.util.*

fun start(input: InputStream, output: PrintStream) {
    val scanner = Scanner(input)
    output.print(">> ")
    val constants = mutableListOf<MObject>()
    val globals = mutableListOf<MObject>()
    val symbolTable = SymbolTable()

    //val symbolTable = Symbol
}