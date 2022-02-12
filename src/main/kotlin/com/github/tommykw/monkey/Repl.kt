package com.github.tommykw.monkey

import java.util.*

fun runRepl() {
    val input = System.`in`
    val scanner = Scanner(input)

    while (scanner.hasNext()) {
        val code = scanner.nextLine()
        val lexer = Lexer(code)
        val parser = Parser(lexer)
        val program = parser.parseProgram()

        end@ while (true) {
            val token = lexer.nextToken()
            if (token.type == TokenType.EOF) {
                break@end
            } else {
                println(token)
            }
        }

        println(program.toString())
    }
}