package no.eliashaugsbakk.kompilator.IRGeneration;

import no.eliashaugsbakk.kompilator.tokenization.Position;

public class IRGenerationException extends RuntimeException {
  public IRGenerationException(Position position, String message) {
    String pos;
    if (position == null) {
      pos = "unknown position";
    } else {
      pos = position.line() + ":" + position.column();
    }
    super(pos + ", " + message);
  }
}
