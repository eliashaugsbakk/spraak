package no.eliashaugsbakk.kompilator.tokenization;

public enum TokenType {
  COMMENT,          // // this is a comment
  KEYWORD,          // print, var, if, while, function, etc.
  IDENTIFIER,       // variable_1
  STRING_LITERAL,   // "Hello, World!"
  COLON,            // : (set x[:] int = ...)
  TYPE,             // string, i32, i16?, my_type, ... (? makes nullable)
  ASSIGN,           // =
  NULLABLE,         // ? (x = string?;)
  LPAREN,           // (
  RPAREN,           // )
  SEMICOLON,        // ;
  COMMA,            // ,
  EOF               // End of File
}
