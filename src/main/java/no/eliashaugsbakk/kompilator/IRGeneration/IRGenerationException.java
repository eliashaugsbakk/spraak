package no.eliashaugsbakk.kompilator.IRGeneration;

import no.eliashaugsbakk.kompilator.tokenization.Position;

public class IRGenerationException extends RuntimeException {
  public IRGenerationException(Position position, String message) {
    String pos;
    if (position == null) {
      pos = "ukjent posisjon";
    } else {
      pos = position.line() + ":" + position.column();
    }
    super(pos + ", " + message);
  }
}
