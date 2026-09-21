package no.eliashaugsbakk.kompilator.tokenization;

public enum LexerState {
  NORMAL,           // reading regular tokens
  IN_STRING,        // inside a string (after ")
  IN_WORD,          // reading a keyword or identifier
  IN_TYPE,          // reading a type (i32, streng, ...)
  IN_SINGLE_LINE_COMMENT, // reading a single line comment
  IN_MULTI_LINE_COMMENT, // reading a multi line comment
}
