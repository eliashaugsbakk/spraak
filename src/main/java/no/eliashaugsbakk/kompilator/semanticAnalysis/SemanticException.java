package no.eliashaugsbakk.kompilator.semanticAnalysis;

import no.eliashaugsbakk.kompilator.tokenization.Position;

public class SemanticException extends Exception {
  public SemanticException(Position position, String message) {
    String pos;
    if (position == null) {
      pos = "unknown position";
    } else {
      pos = position.line() + ":" + position.column();
    }
    super(pos + ", " + message);
  }
}
