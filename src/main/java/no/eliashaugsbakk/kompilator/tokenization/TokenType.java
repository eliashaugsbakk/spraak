package no.eliashaugsbakk.kompilator.tokenization;

public enum TokenType {
  KEYWORD,          // print, var, if, while, function, etc.
  IDENTIFIER,       // variable_1
  STRING,           // "Hello, World!"
  TYPE_DECLARATION, // : (x[:] int = ...)
  TYPE,             // String, i32, i16?, my_type, ... (? makes nullable)
  ASSIGN,           // =
  LPAREN,           // (
  RPAREN,           // )
  SEMICOLON,        // ;
  EOF               // End of File
}
