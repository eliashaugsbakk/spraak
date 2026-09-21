package no.eliashaugsbakk.kompilator.parsing;

import static no.eliashaugsbakk.kompilator.tokenization.TokenType.ASSIGN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.COLON;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.COMMA;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.EOF;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.IDENTIFIER;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.KEYWORD;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.LPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.NULLABLE;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.RPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.SEMICOLON;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.STRING_LITERAL;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.TYPE;

import java.util.ArrayList;
import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Identifier;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Assignment;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.parsing.node.statement.IdentifierDeclaration;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Statement;
import no.eliashaugsbakk.kompilator.tokenization.Position;
import no.eliashaugsbakk.kompilator.tokenization.Token;

/*
  The goal of parsing is to create the AST.
  The root node is the program itself, which can only hold statements.

  Statements:
  - Is an action which executes something
  - Has no return or implicit value
  - Must end with a semicolon
  - A statement may contain expressions
  - An expression cannot contain a statement

  Expressions:
  - Returns or produces a value
  - Does not end with a semicolon
  - Can be nested instide other expressions or a statement
  - Examples:
    - Literals
    - Identifiers
    - Binary Operations
    - Function calls
 */


public class Parser {
  private final List<Token> tokens;
  private final Program rootNode;
  private int current = 0;

  public Parser(List<Token> tokens) {
    this.tokens = tokens;
    this.rootNode = new Program(null);
  }

  public AST parse() throws ParserException {
    while (current < tokens.size()) {
      Token token = tokens.get(current);

      // End of File
      if (token.type() == EOF) {
        break;
      }

      // root node must contain only statements
      Statement stmt = parseStatement();
      rootNode.addStatement(stmt);
    }
    return new AST(rootNode);
  }

  private Statement parseStatement() throws ParserException {
    // multiple statements are supported for v0.0.2;
    // identifier declaration: x: string = "hello"; (with expression)
    // identifier declaration: x: string; (without expression)
    // assignment: x = "hello"; (identifier gets assigned an expression)
    // expression statements: print(x); (function without a return value)


    // each statement must either start with a keyword or an identifier
    Token token = tokens.get(current);

    // implemented keywords:
    // skriv()
    // set
    // mut
    if (token.type() == KEYWORD) {
      return parseKeyword();
    }

    // must be identifier declaration or an assignment
    else if (token.type() == IDENTIFIER) {
      return parseIdentifierStatement();
    }

    // must be some other token which is not a statement
    else {
      throw new ParserException(token.position(), "Unexpected token: " + token.value());
    }
  }

  private Statement parseKeyword() throws ParserException {
    Token token = tokens.get(current);
    if (token.value().contentEquals("set")) {
      current++; // consume "set"
      return parseDeclaration(false);
    } else if (token.value().contentEquals("mut")) {
      current++; // consume "mut"
      return parseDeclaration(true);
    } else if (token.value().contentEquals("skriv")) {
      return parseExpressionStatement();
    } else {
      throw new ParserException(token.position(), "unknown keyword: " + token.value());
    }
  }

  private ExpressionStatement parseExpressionStatement() throws ParserException {
    // this can be any expression used as a statement
    // print(x);
    // 5 + 6;
    // "hello";


    Expression expression = parseExpression();
    expectSemicolon();
    return new ExpressionStatement(expression.position, expression);
  }

  private Statement parseIdentifierStatement() throws ParserException {
    String identifier = tokens.get(current).value();
    current++; // consume identifier

    Token next = tokens.get(current);

    if (next.type() == ASSIGN) {
      return parseAssignment(identifier);
    } else if (next.type() == LPAREN) {
      // Future expansion: standalone function calls like `doSomething();`
      current--; // rewind to identifier
      Expression expr = parseFunctionCall();
      expectSemicolon();
      return new ExpressionStatement(expr.position, expr);
    } else {
      throw new ParserException(next.position(), "Unexpected token after identifier: " + next.value());
    }
  }

  private Statement parseDeclaration(boolean isMutable) throws ParserException {
    Token nameToken = tokens.get(current);
    if (nameToken.type() != IDENTIFIER) {
      throw new ParserException(nameToken.position(),
          "Expected identifier after declaration keyword, got: " + nameToken.value());
    }
    String identifier = nameToken.value();
    current++; // consume identifier

    if (tokens.get(current).type() != COLON) {
      throw new ParserException(tokens.get(current).position(),
          "Expected : after identifier in declaration");
    }
    current++; // skip :

    Type type = parseType();
    Expression initializer = null;

    if (tokens.get(current).type() == ASSIGN) {
      current++; // skip =
      initializer = parseExpression();
    }

    expectSemicolon();
    return new IdentifierDeclaration(nameToken.position(), identifier, type, initializer, isMutable);
  }

  private Statement parseAssignment(String identifier) throws ParserException {
    // x = value;
    current++; // skip =

    Expression value = parseExpression();
    expectSemicolon();

    return new Assignment(value.position, identifier, value);
  }

  private Type parseType() throws ParserException {
    Token token = tokens.get(current);
    if (token.type() != TYPE) {
      throw new ParserException(token.position(),
          "Expected type, got: " + token.value());
    }
    current++;

    String typeName = token.value();
    boolean nullable = false;

    if (current < tokens.size() && tokens.get(current).type() == NULLABLE) {
      nullable = true;
      current++;
    }

    return new Type(typeName, nullable);
  }

  private Expression parseExpression() throws ParserException {
    // an expression produces a value and may contain other expressions
    //  implemented for v0.0.2 are:
    //  "string"    - STRING_LITERAL
    //  my_var      - IDENTIFIER

    Token token = tokens.get(current);
    current++;

    if (token.type() == STRING_LITERAL) {
      return new StringLiteral(token.position(), token.value());
    } else if (token.type() == IDENTIFIER || token.type() == KEYWORD) {
      // could be function call or just identifier reference
      if (current < tokens.size() && tokens.get(current).type() == LPAREN) {
        current--;
        return parseFunctionCall();
      }
      return new Identifier(token.position(), token.value());
    } else {
      throw new ParserException(token.position(),
          "Unexpected token: " + token.value() + ". Expected an expression");
    }
  }

  private FunctionCall parseFunctionCall() throws ParserException {
    Token token = tokens.get(current);
    String name = token.value();
    current++;
    List<Expression> arguments = parseFunctionArguments();
    return new FunctionCall(token.position(), name, arguments);
  }

  private List<Expression> parseFunctionArguments() throws ParserException {
    List<Expression> arguments = new ArrayList<>();

    if (tokens.get(current).type() != LPAREN) {
      throw new ParserException(tokens.get(current).position(),
          "Expected (");
    }
    current++;  // skip (

    while (current < tokens.size() && tokens.get(current).type() != RPAREN) {
      arguments.add(parseExpression());  // parseExpression() increments current

      if (tokens.get(current).type() == COMMA) {
        current++;  // skip comma
      }
    }

    if (current >= tokens.size() || tokens.get(current).type() != RPAREN) {
      throw new ParserException(tokens.get(current).position(),
          "Expected )");
    }
    current++;  // skip )

    return arguments;
  }

  private void expectSemicolon() throws ParserException {
    if (current >= tokens.size()) {
      throw new ParserException(null, "Expected ;");
    } else if (tokens.get(current).type() != SEMICOLON) {
      throw new ParserException(tokens.get(current).position(),
          "Expected: ;");
    }
    current++;
  }
}
