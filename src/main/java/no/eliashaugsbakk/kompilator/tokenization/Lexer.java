package no.eliashaugsbakk.kompilator.tokenization;

import static java.lang.Character.isLetterOrDigit;
import static no.eliashaugsbakk.kompilator.tokenization.LexerState.IN_STRING;
import static no.eliashaugsbakk.kompilator.tokenization.LexerState.IN_WORD;
import static no.eliashaugsbakk.kompilator.tokenization.LexerState.NORMAL;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.EOF;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.IDENTIFIER;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.KEYWORD;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.LPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.RPAREN;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.SEMICOLON;
import static no.eliashaugsbakk.kompilator.tokenization.TokenType.STRING;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
  private int line = 1;
  private int column = 0;

  private final String input;
  private int position = 0;
  private final List<Token> tokens = new ArrayList<>();
  private final StringBuilder wordBuffer = new StringBuilder();
  LexerState state = NORMAL;


  public Lexer(String input) {
    this.input = input;
  }

  public List<Token> tokenize() {

    while (position < input.length()) {
      char current = input.charAt(position);

      if (current == '\n') {
        line++;
        column = 0;
      } else {
        column++;
      }

      if (state == IN_WORD) {
        if (!isLetterOrDigit(current) || Character.isWhitespace(current)) {
          state = NORMAL;
          characterizeWord();
        } else {
          wordBuffer.append(current);
        }
      }
      if (state == NORMAL) {
        if (!Character.isWhitespace(current)) {
          if (current == '"') {
            state = IN_STRING;
          } else if (isLetterOrDigit(current)) {
            state = IN_WORD;
            wordBuffer.append(current);

          } else if (current == '(') {
            tokens.add(new Token(LPAREN, Character.toString(current), line, column));
          } else if (current == ')') {
            tokens.add(new Token(RPAREN, Character.toString(current), line, column));
          } else if (current == ';') {
            tokens.add(new Token(SEMICOLON, Character.toString(current), line, column));
          }
        }

      } else if (state == IN_STRING) {
        if (current == '"') {
          state = NORMAL;
          tokens.add(new Token(STRING, wordBuffer.toString(), line, column));
          wordBuffer.delete(0, wordBuffer.length());
        } else {
          wordBuffer.append(current);
        }
      }
      position++;
    }
    tokens.add(new Token(EOF, "End of File", line, column));
    return tokens;
  }

  private void characterizeWord() {
    if (Keywords.KEYWORDS.contains(wordBuffer.toString())) {
      tokens.add(new Token(KEYWORD, wordBuffer.toString(), line, column - wordBuffer.length()));
    } else {
      tokens.add(new Token(IDENTIFIER, wordBuffer.toString(), line, column - wordBuffer.length()));
    }
    wordBuffer.delete(0, wordBuffer.length());
  }
}
