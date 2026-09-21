package no.eliashaugsbakk.kompilator.parsing;

import no.eliashaugsbakk.kompilator.CompilationException;
import no.eliashaugsbakk.kompilator.tokenization.Position;

public class ParserException extends CompilationException {
  public ParserException(Position position, String message) {
    super(format(position, message));
  }

  private static String format(Position position, String message) {
    String pos = position == null ? "ukjent posisjon" : position.line() + ":" + position.column();
    return pos + ", " + message;
  }
}
