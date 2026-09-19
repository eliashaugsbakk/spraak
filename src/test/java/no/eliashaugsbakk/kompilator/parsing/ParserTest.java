package no.eliashaugsbakk.kompilator.parsing;

import static no.eliashaugsbakk.kompilator.tokenization.TokenType.KEYWORD;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.LPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.RPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.SEMICOLON;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.STRING_LITERAL;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.tokenization.Token;
import org.junit.jupiter.api.Test;

class ParserTest {

  @Test
  void validSyntaxBuildsCorrectTree() throws ParserException {
    List<Token> tokens = List.of(
        new Token(KEYWORD, "print", 1, 0),
        new Token(LPAREN, "(", 1, 5),
        new Token(STRING_LITERAL, "Hello", 1, 6),
        new Token(RPAREN, ")", 1, 13),
        new Token(SEMICOLON, ";", 1, 14)
    );

    AST ast = new Parser(tokens).parse();

    // Verify tree structure
    assertNotNull(ast.getRoot());
    assertInstanceOf(Program.class, ast.getRoot());
    Program program = (Program) ast.getRoot();
    assertEquals(1, program.statements.size());

    ExpressionStatement stmt = (ExpressionStatement) program.statements.getFirst();
    assertInstanceOf(FunctionCall.class, stmt.expression);

    FunctionCall call = (FunctionCall) stmt.expression;
    assertEquals("print", call.functionName);
    assertEquals(1, call.arguments.size());
    assertEquals("Hello", ((StringLiteral) call.arguments.getFirst()).value);
  }

  @Test
  void missingSemicolonThrows() {
    List<Token> tokens = List.of(
        new Token(KEYWORD, "print", 1, 0),
        new Token(LPAREN, "(", 1, 5),
        new Token(STRING_LITERAL, "Hello", 1, 6),
        new Token(RPAREN, ")", 1, 13)
    );

    assertThrows(ParserException.class, () -> new Parser(tokens).parse());
  }

  @Test
  void missingParenthesisThrows() {
    List<Token> tokens = List.of(
        new Token(KEYWORD, "print", 1, 0),
        new Token(STRING_LITERAL, "Hello", 1, 5),
        new Token(RPAREN, ")", 1, 12),
        new Token(SEMICOLON, ";", 1, 13)
    );

    assertThrows(ParserException.class, () -> new Parser(tokens).parse());
  }
}
