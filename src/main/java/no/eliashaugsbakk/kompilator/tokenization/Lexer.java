package no.eliashaugsbakk.kompilator.tokenization;

import static java.lang.Character.isLetterOrDigit;
import static no.eliashaugsbakk.kompilator.tokenization.LexerState.IN_STRING;
import static no.eliashaugsbakk.kompilator.tokenization.LexerState.IN_TYPE;
import static no.eliashaugsbakk.kompilator.tokenization.LexerState.IN_WORD;
import static no.eliashaugsbakk.kompilator.tokenization.LexerState.NORMAL;
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

public class Lexer {
  private char current;
  private int line = 1;
  private int column = 0;

  private final String input;
  private int position = 0;
  private final List<Token> tokens = new ArrayList<>();
  private final StringBuilder wordBuffer = new StringBuilder();
  LexerState state = NORMAL;


  public Lexer(String input) {
    this.input = input;
    this.current = input.charAt(position);
  }

  public List<Token> tokenize() {

    while (position < input.length()) {
      current = input.charAt(position);

      if (current == '\n') {
        line++;
        column = 0;
      } else {
        column++;
      }

      if (state == IN_WORD) {
        inWord();
      } else if (state == IN_TYPE) {
        inType();
      } else if (state == IN_STRING) {
        inString();
      }

      if (state == NORMAL) {
        normal();
      }

      position++;
    }

    tokens.add(new Token(EOF, "End of File", line, column));
    //tokens.forEach(token -> IO.println(token.type().toString() + ": " + token.value()));
    return tokens;
  }

  private void normal() {
    if (Character.isWhitespace(current)) {
      return;
    }

    if (current == '"') {
      state = IN_STRING;
    } else if (isWordCharacter(current)) {
      state = IN_WORD;
      wordBuffer.append(current);
    } else if (current == ':') {
      tokens.add(new Token(COLON, ":", line, column));
      state = IN_TYPE;
      position++; // Skip whitespace

    } else if (current == '=') {
      tokens.add(new Token(ASSIGN, "=", line, column));
    } else if (current == '(') {
      tokens.add(new Token(LPAREN, "(", line, column));
    } else if (current == ')') {
      tokens.add(new Token(RPAREN, ")", line, column));
    } else if (current == ';') {
      tokens.add(new Token(SEMICOLON, ";", line, column));
    } else if (current == ',') {
      tokens.add(new Token(COMMA, ",", line, column));
    }
  }

  private boolean isWordCharacter(char current) {
    return isLetterOrDigit(current) || current == '_';
  }

  private void inType() {

    if (current == '?') {
      state = NORMAL;
      tokens.add(new Token(TYPE, wordBuffer.toString(), line, column - wordBuffer.length()));
      tokens.add(new Token(NULLABLE, "?", line, column));
      clearWordBuffer();
    } else if (Character.isWhitespace(current) || current == '=' || current == ';') {
      state = NORMAL;
      tokens.add(new Token(TYPE, wordBuffer.toString(), line, column - wordBuffer.length()));
      clearWordBuffer();
    } else {
      wordBuffer.append(current);
    }
  }

  private void inString() {
    if (current == '"') {
      tokens.add(new Token(STRING_LITERAL, wordBuffer.toString(), line, column));
      wordBuffer.delete(0, wordBuffer.length());
      state = NORMAL;
      position++; // skip closing "
      current = input.charAt(position);
    } else if (current == '\\' && position + 1 < input.length()) {
      // The source file contains \n as two characters: '\' and 'n'.
      // We intercept the backslash and emit the character it represents.
      position++;
      current = input.charAt(position);
      switch (current) {
        case 'n' -> wordBuffer.append('\n');
        case 't' -> wordBuffer.append('\t');
        case 'r' -> wordBuffer.append('\r');
        case '\\' -> wordBuffer.append('\\');
        default -> wordBuffer.append(current);
      }
    } else {
      wordBuffer.append(current);
    }
  }

  private void inWord() {
    if (!isWordCharacter(current)) {
      state = NORMAL;
      characterizeWord();
    } else {
      wordBuffer.append(current);
    }
  }

  private void characterizeWord() {
    if (Keywords.KEYWORDS.contains(wordBuffer.toString())) {
      if (wordBuffer.toString().contentEquals("set")) {
        tokens.add(new Token(KEYWORD, "set", line, column));
      } else if (wordBuffer.toString().contentEquals("mut")) {
        tokens.add(new Token(KEYWORD, "mut", line, column));
      } else {
        // this is a function call
        tokens.add(new Token(KEYWORD, wordBuffer.toString(), line, column - wordBuffer.length()));
      }
    } else {
      tokens.add(new Token(IDENTIFIER, wordBuffer.toString(), line, column - wordBuffer.length()));
    }
    clearWordBuffer();
  }

  private void clearWordBuffer() {
    wordBuffer.delete(0, wordBuffer.length());
  }
}
