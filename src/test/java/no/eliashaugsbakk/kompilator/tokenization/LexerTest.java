package no.eliashaugsbakk.kompilator.tokenization;

import static no.eliashaugsbakk.kompilator.tokenization.TokenType.EOF;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.KEYWORD;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.LPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.RPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.SEMICOLON;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.STRING;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class LexerTest {

  @Test
  void tokenizeTokenizesCorrectly() {
    String input = """
        print("Hello, World");
        """;

    Lexer lexer = new Lexer(input);
    List<Token> tokens = lexer.tokenize();

    // tokens.forEach(t-> IO.println(t.type + " : " + t.value));

    assertSame(KEYWORD, tokens.getFirst().type());
    assertEquals(1, tokens.get(0).line());
    assertEquals(1, tokens.get(0).colum());

    assertSame(LPAREN, tokens.get(1).type());

    assertSame(STRING, tokens.get(2).type());
    assertTrue(tokens.get(2).value().contains("Hello, World"));

    assertSame(RPAREN, tokens.get(3).type());
    assertSame(SEMICOLON, tokens.get(4).type());
    assertSame(EOF, tokens.get(5).type());
  }
}
