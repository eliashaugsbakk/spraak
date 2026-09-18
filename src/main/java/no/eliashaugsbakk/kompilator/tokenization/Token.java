package no.eliashaugsbakk.kompilator.tokenization;

class Token {
  TokenType type;
  String value;
  int line;
  int colum;

  Token(TokenType type, String value, int line, int colum) {
    this.type = type;
    this.value = value;
    this.line = line;
    this.colum = colum;
  }
}
