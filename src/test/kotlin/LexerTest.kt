import com.github.tommykw.monkey.Lexer
import com.github.tommykw.monkey.TokenType.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class LexerTest {
    @Test
    fun `validate lexer`() {
        val code = """
const six = 6;
const seven = 7;
        """.trimIndent()

        val lexer = Lexer(code)

        val expected = listOf(
            CONST to "const",
            IDENTIFIER to "six",
            EQ to "=",
            NUMBER to "6",
            SEMICOLON to ";",
            CONST to "const",
            IDENTIFIER to "seven",
            EQ to "=",
            NUMBER to "7",
            SEMICOLON to ";",
            EOF to ""
        )

        expected.forEach { (type, literal) ->
            val token = lexer.nextToken()
            Assertions.assertEquals(token.type, type)
            Assertions.assertEquals(token.literal, literal)
        }
    }
}