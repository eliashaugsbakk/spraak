package no.eliashaugsbakk.kompilator.parsing;

public class ParserException extends Exception {
  public ParserException(int line, int column, String message) {
    super(line + ":" + column + ", " + message);
  }
}
