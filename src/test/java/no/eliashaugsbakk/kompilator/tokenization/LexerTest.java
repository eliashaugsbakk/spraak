package no.eliashaugsbakk.kompilator.tokenization;

import static no.eliashaugsbakk.kompilator.tokenization.TokenType.EOF;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.COMMA;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.IDENTIFIER;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.LPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.NUMBER_LITERAL;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.RPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.SEMICOLON;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.STRING_LITERAL;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class LexerTest {

  @Test
  void tokenizeTokenizesCorrectly() {
    String input = """
        skriv("Hello, World");
        """;

    Lexer lexer = new Lexer(input);
    List<Token> tokens = lexer.tokenize();

    assertSame(IDENTIFIER, tokens.getFirst().type());
    assertEquals(1, tokens.get(0).position().line());
    assertEquals(1, tokens.get(0).position().column());

    assertSame(LPAREN, tokens.get(1).type());

    assertSame(STRING_LITERAL, tokens.get(2).type());
    assertTrue(tokens.get(2).value().contains("Hello, World"));

    assertSame(RPAREN, tokens.get(3).type());
    assertSame(SEMICOLON, tokens.get(4).type());
    assertSame(EOF, tokens.get(5).type());
  }

  @Test
  void commaSeparatesNumbersAndPeriodMarksDecimal() {
    List<Token> tokens = new Lexer("skriv(8,8); skriv(10.000_65);").tokenize();

    assertSame(NUMBER_LITERAL, tokens.get(2).type());
    assertEquals("8", tokens.get(2).value());
    assertSame(COMMA, tokens.get(3).type());
    assertSame(NUMBER_LITERAL, tokens.get(4).type());
    assertEquals("8", tokens.get(4).value());

    assertSame(NUMBER_LITERAL, tokens.get(9).type());
    assertEquals("10.00065", tokens.get(9).value());
  }
}
