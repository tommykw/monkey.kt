import com.github.tommykw.monkey.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ParserTest {

    @Test
    fun `const statements`() {

        data class TestData(val input: String)

        val tests = listOf(
            TestData(input = "const i = 1;"),
            TestData(input = "const bool = true;"),
            TestData(input = "const letter = 'a';")
        )

        tests.forEach { (input) ->
            val lexer = Lexer(input)
            val parser = Parser(lexer)
            val program = parser.parseProgram()
            val statement = program.statements[0]
            Assertions.assertEquals(statement::class, ConstStatement::class)
        }
    }

    @Test
    fun `return statements`() {

        data class TestData(val input: String)

        val tests = listOf(
            TestData(input = "return 2;"),
            TestData(input = "return false;"),
            TestData(input = "return 'b';"),
        )

        tests.forEach { (input) ->
            val lexer = Lexer(input)
            val parser = Parser(lexer)
            val program = parser.parseProgram()
            val statement = program.statements[0]
            Assertions.assertEquals(statement::class, ReturnStatement::class)
        }
    }
}