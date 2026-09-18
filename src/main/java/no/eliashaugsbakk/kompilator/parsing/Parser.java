package no.eliashaugsbakk.kompilator.parsing;

import static no.eliashaugsbakk.kompilator.tokenization.TokenType.EOF;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.KEYWORD;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.LPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.RPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.SEMICOLON;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.STRING;

import java.util.ArrayList;
import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.tokenization.Token;

public class Parser {
  private final List<Token> tokens;
  private int position = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  public AST parse() throws ParserException {
    Program rootNode = new Program();

    while (position < tokens.size()) {
      Token token = tokens.get(position);

      if (token.type() == KEYWORD) {
        String functionName = token.value();
        position++;

        List<Expression> arguments = parseArguments();

        FunctionCall functionCall = new FunctionCall(functionName, arguments);
        rootNode.addStatement(new ExpressionStatement(functionCall));
      } else if (token.type() == EOF) {
        break;
      } else {
        throw new ParserException(token.line(), token.colum(),
            "Unexpected token: " + token.value());
      }
    }

    return new AST(rootNode);
  }

  List<Expression> parseArguments() throws ParserException {
    List<Expression> arguments = new ArrayList<>();

    if (position >= tokens.size() || tokens.get(position).type() != LPAREN) {
      throw new ParserException(tokens.get(position).line(), tokens.get(position).colum(),
          "Unexpected token:" + tokens.get(position).value() + "\n Expected: (");
    }
    position++;

    while (position < tokens.size() && tokens.get(position).type() != RPAREN) {
      if (tokens.get(position).type() == STRING) {
        arguments.add(new StringLiteral(tokens.get(position).value()));
      }
      position++;
    }

    if (position >= tokens.size()) {
      throw new ParserException(-1, -1, "Unexpected end of file, expected )");
    }
    position++;

    if (position >= tokens.size() || tokens.get(position).type() != SEMICOLON) {
      throw new ParserException(position < tokens.size() ? tokens.get(position).line() : -1,
          position < tokens.size() ? tokens.get(position).colum() : -1,
          "Unexpected token: " + (position < tokens.size() ? tokens.get(position).value() : "EOF") +
              "\n" + "Expected: ;");
    }
    position++;

    return arguments;
  }
}
