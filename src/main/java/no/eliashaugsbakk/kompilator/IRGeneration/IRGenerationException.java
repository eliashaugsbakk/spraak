package no.eliashaugsbakk.kompilator.IRGeneration;

import no.eliashaugsbakk.kompilator.CompilationException;
import no.eliashaugsbakk.kompilator.tokenization.Position;

public class IRGenerationException extends CompilationException {
  public IRGenerationException(Position position, String message) {
    super(format(position, message));
  }

  private static String format(Position position, String message) {
    String pos = position == null ? "ukjent posisjon" : position.line() + ":" + position.column();
    return pos + ", " + message;
  }
}
