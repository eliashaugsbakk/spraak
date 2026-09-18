package no.eliashaugsbakk.kompilator.tokenization;

enum TokenType {
  KEYWORD,      // print, var, if, while, function, etc.
  IDENTIFIER,   // variable_1
  STRING,       // "Hello, World!"
  LPAREN,       // (
  RPAREN,       // )
  SEMICOLON,    // ;
  EOF           // End of File
}
