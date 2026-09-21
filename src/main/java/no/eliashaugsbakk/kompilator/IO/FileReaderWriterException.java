package no.eliashaugsbakk.kompilator.IO;

import no.eliashaugsbakk.kompilator.CompilationException;

public class FileReaderWriterException extends CompilationException {
  public FileReaderWriterException(String message, Exception e) {
    super("Filhåndtering: " + message, e);
  }
}
