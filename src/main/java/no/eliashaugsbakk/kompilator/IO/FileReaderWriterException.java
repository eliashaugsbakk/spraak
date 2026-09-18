package no.eliashaugsbakk.kompilator.IO;

public class FileReaderWriterException extends Exception {
  public FileReaderWriterException(String message, Exception e) {
    super("FileReaderWriter: " + message, e);
  }
}
