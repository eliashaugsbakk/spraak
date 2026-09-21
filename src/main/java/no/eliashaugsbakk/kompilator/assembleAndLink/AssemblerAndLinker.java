package no.eliashaugsbakk.kompilator.assembleAndLink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import no.eliashaugsbakk.kompilator.IO.File;
import no.eliashaugsbakk.kompilator.IO.FileReaderWriter;
import no.eliashaugsbakk.kompilator.IO.FileReaderWriterException;
import no.eliashaugsbakk.kompilator.CompilationException;
import no.eliashaugsbakk.kompilator.Main;

public class AssemblerAndLinker {
  private final FileReaderWriter fileReaderWriter;

  public AssemblerAndLinker() {
    fileReaderWriter = new FileReaderWriter();
  }

  public void assembleAndLink(String programName, String assembly) throws CompilationException {
    String assemblyFileName = programName + ".s";
    String assembledFileName = programName + ".o";

    fileReaderWriter.writeFile(new File(assemblyFileName, assembly));

    try {
      // Assembling using: as -o program.o program.s
      ProcessBuilder asPB = new ProcessBuilder("as", "-o", assembledFileName, assemblyFileName);
      runProcess(asPB);
    } catch (IOException e) {
      throw new CompilationException("Kunne ikke starte assembleren", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new CompilationException("Ventingen på assembleren ble avbrutt", e);
    }

    try {
      // Linking using GNU linker: ld -o program program.o
      ProcessBuilder ldPB = new ProcessBuilder("ld", "-o", programName, assembledFileName);
      runProcess(ldPB);
    } catch (IOException e) {
      throw new CompilationException("Kunne ikke starte lenkeren", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new CompilationException("Ventingen på lenkeren ble avbrutt", e);
    }

    // Clean up
    if (!Main.VERBOSE) {
      fileReaderWriter.deleteFile(assemblyFileName);
      fileReaderWriter.deleteFile(assembledFileName);
    }
  }

  private static void runProcess(ProcessBuilder processBuilder)
      throws IOException, InterruptedException, CompilationException {
    Process process = processBuilder.start();
    process.waitFor();
    if (process.exitValue() != 0) {
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(process.getErrorStream()));

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        throw new CompilationException("Prosessen mislyktes:\n" + line);
      }
      bufferedReader.close();
    }
  }
}
