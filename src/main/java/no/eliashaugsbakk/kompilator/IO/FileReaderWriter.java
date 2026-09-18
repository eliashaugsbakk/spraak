package no.eliashaugsbakk.kompilator.IO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReaderWriter {

  public File readFile(String inputFilePath) throws FileReaderWriterException {
    Path path = Path.of(inputFilePath);

    String rawName = path.getFileName().toString();
    int dotIndex = rawName.lastIndexOf('.');
    String fileName = (dotIndex == -1) ? rawName : rawName.substring(0, dotIndex);

    try {
      String fileBody = Files.readString(path);
      return new File(fileName, fileBody);
    } catch (IOException e) {
      throw new FileReaderWriterException("Failed to read file: " + inputFilePath, e);
    }
  }

  public void writeFile(File file) throws FileReaderWriterException {
    try {
      Files.writeString(Path.of(file.fileName()), file.fileBody());
    } catch (IOException e) {
      throw new FileReaderWriterException("Failed to write file: " + file.fileName(), e);
    }
  }

  public void deleteFile(String filePath) throws FileReaderWriterException {
    try {
      Files.delete(Path.of(filePath));
    } catch (IOException e) {
      throw new FileReaderWriterException("Could not delete file: " + filePath, e);
    }
  }
}
