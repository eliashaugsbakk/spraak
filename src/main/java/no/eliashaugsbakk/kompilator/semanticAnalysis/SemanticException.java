package no.eliashaugsbakk.kompilator.semanticAnalysis;

import no.eliashaugsbakk.kompilator.tokenization.Position;

public class SemanticException extends Exception {
  public SemanticException(Position position, String message) {
    String pos;
    if (position == null) {
      pos = "ukjent posisjon";
    } else {
      pos = position.line() + ":" + position.column();
    }
    super(pos + ", " + message);
  }
}
