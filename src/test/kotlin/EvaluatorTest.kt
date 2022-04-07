import com.github.tommykw.monkey.Environment
import com.github.tommykw.monkey.Evaluator
import com.github.tommykw.monkey.Lexer
import com.github.tommykw.monkey.Parser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EvaluatorTest {
    @Test
    fun `evaluate integer literal`() {
//        data class TestData(val input: String, val expected: Int)
//
//        listOf(
//            TestData("1", 1),
//            TestData("-1", -1),
//            TestData("123", 123),
//            TestData("-123", -123),
//            TestData("12 + 12;", 24),
//            TestData("12 - 12", 0),
//            TestData("123 * 123", 15129),
//            TestData("123 / 123", 1)
//        ).forEach { (input, expected) ->
//            val lexer = Lexer(input)
//            val parser = Parser(lexer)
//            val program = parser.parseProgram()
//            //println(program.statements)
//            val evaluator = Evaluator()
//            val evaluated = evaluator.eval(program, Environment.new())
//            Assertions.assertEquals(evaluated?.inspect()?.toInt(), expected)
//        }
    }
}