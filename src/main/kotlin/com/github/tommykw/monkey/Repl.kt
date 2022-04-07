package com.github.tommykw.monkey

import java.util.*

const val PROMPT = ">>> "

fun runRepl() {
    val input = System.`in`
    println(PROMPT)
    val scanner = Scanner(input)
    val environment = Environment.new()
    val evaluator = Evaluator()

    while (scanner.hasNext()) {
        val code = scanner.nextLine()
        val lexer = Lexer(code)
        val parser = Parser(lexer)
        val program = parser.parseProgram()
        //println(program)
        val evaluated = evaluator.eval(program, environment)

        if (evaluated != null) {
            //println(evaluated.inspect())
        }

        println(PROMPT)
    }
}