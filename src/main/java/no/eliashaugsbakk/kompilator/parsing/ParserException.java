package no.eliashaugsbakk.kompilator.parsing;

import no.eliashaugsbakk.kompilator.tokenization.Position;

public class ParserException extends Exception {
  public ParserException(Position position, String message) {
    String pos;
    if (position == null) {
      pos = "ukjent posisjon";
    } else {
      pos = position.line() + ":" + position.column();
    }
    super(pos + ", " + message);
  }
}
