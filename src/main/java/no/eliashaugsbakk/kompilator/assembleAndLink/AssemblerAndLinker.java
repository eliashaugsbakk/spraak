package no.eliashaugsbakk.kompilator.assembleAndLink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import no.eliashaugsbakk.kompilator.IO.File;
import no.eliashaugsbakk.kompilator.IO.FileReaderWriter;
import no.eliashaugsbakk.kompilator.IO.FileReaderWriterException;

public class AssemblerAndLinker {
  private final FileReaderWriter fileReaderWriter;

  public AssemblerAndLinker() {
    fileReaderWriter = new FileReaderWriter();
  }

  public void assembleAndLink(String programName, String assembly) {
    String assemblyFileName = programName + ".s";
    String assembledFileName = programName + ".o";

    try {
      fileReaderWriter.writeFile(new File(assemblyFileName, assembly));
    } catch (FileReaderWriterException e) {
      IO.println("err: Could not write file: " + e.getMessage());
    }

    try {
      // Assembling using: as -o program.o program.s
      ProcessBuilder asPB = new ProcessBuilder("as", "-o", assembledFileName, assemblyFileName);
      runProcess(asPB);
    } catch (IOException e) {
      IO.println("err: Failed to invoke GCC\n\n" + e);
    } catch (InterruptedException e) {
      IO.println("err: Failed to wait for gcc output\n\n" + e);
    }

    try {
      // Linking using GNU linker: ld -o program program.o
      ProcessBuilder ldPB = new ProcessBuilder("ld", "-o", programName, assembledFileName);
      runProcess(ldPB);
    } catch (IOException e) {
      IO.println("err: Failed to invoke GCC\n\n" + e);
    } catch (InterruptedException e) {
      IO.println("err: Failed to wait for gcc output\n\n" + e);
    }

    // Clean up
    try {
      fileReaderWriter.deleteFile(assemblyFileName);
    } catch (FileReaderWriterException e) {
      IO.println("err: Could not delete assembly file: " + e.getMessage());
    }
    try {
      fileReaderWriter.deleteFile(assembledFileName);
    } catch (FileReaderWriterException e) {
      IO.println("err: Could not delete assembled file: " + e.getMessage());
    }
  }

  private static void runProcess(ProcessBuilder processBuilder)
      throws IOException, InterruptedException {
    Process process = processBuilder.start();
    process.waitFor();
    if (process.exitValue() != 0) {
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(process.getErrorStream()));

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        System.out.println(line);
      }
      bufferedReader.close();
    }
  }
}
