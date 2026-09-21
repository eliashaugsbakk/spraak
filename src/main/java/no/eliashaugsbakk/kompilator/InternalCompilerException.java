package no.eliashaugsbakk.kompilator;

public class InternalCompilerException extends RuntimeException {
  public InternalCompilerException(String message) {
    super(message);
  }

  public InternalCompilerException(String message, Throwable cause) {
    super(message, cause);
  }
}
