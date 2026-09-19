package no.eliashaugsbakk.kompilator.tokenization;

public enum TokenType {
  KEYWORD,          // print, var, if, while, function, etc.
  IDENTIFIER,       // variable_1
  STRING_LITERAL,   // "Hello, World!"
  COLON,            // : (x[:] int = ...)
  TYPE,             // string, i32, i16?, my_type, ... (? makes nullable)
  ASSIGN,           // =
  NULLABLE,    // ? (x = string?;)
  LPAREN,           // (
  RPAREN,           // )
  SEMICOLON,        // ;
  COMMA,            // ,
  EOF               // End of File
}
