package no.eliashaugsbakk.kompilator.tokenization;

public enum TokenType {
  // Keywords
  KEYWORD,          // var, if, while, etc.

  // Identifiers & Built-in Literals
  IDENTIFIER,       // skriv, my_function, my_variable (resolved in semantic analysis)
  STRING_LITERAL,   // "Hello, World!"
  NUMBER_LITERAL,   // 3, -8, 3.14

  // Operators & Symbols
  ASSIGN,           // =
  COLON,            // :
  QUESTION,         // ?
  SEMICOLON,        // ;
  COMMA,            // ,
  LPAREN,           // (
  RPAREN,           // )
  PLUS,             // +
  MINUS,            // -

  // Special
  EOF               // End of File
}
