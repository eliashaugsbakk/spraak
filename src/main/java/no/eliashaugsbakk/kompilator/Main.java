package no.eliashaugsbakk.kompilator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
  public static final String programFileExtension = "spå";


  private record InputProgram(String programName, String programContent) {
  }

  static void main(String[] args) {
    if (args.length != 1) {
      IO.println("err: Only accepts one argument: The filename of the input program");
      System.exit(1);
    }
    InputProgram inputProgram = readInputProgram(args[0]);

    List<String> IR = List.of();

    String assembly = new AssemblyBuilder().createAssembly(IR);

    assemblingAndLInking(inputProgram.programName(), assembly);

    System.exit(0);
  }

  private static void assemblingAndLInking(String programName, String assembly) {
    String assemblyFileName = programName + ".s";
    String assembledFileName = programName + ".o";

    try {
      FileWriter fw = new FileWriter(assemblyFileName);
      fw.write(assembly);
      fw.close();
    } catch (IOException e) {
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
      Files.delete(Path.of(assemblyFileName));
    } catch (IOException e) {
      IO.println("err: Could not delete assembly file: " + e.getMessage());
    }
    try {
      Files.delete(Path.of(assembledFileName));
    } catch (IOException e) {
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

  private static InputProgram readInputProgram(String inputFilePath) {
    if (!inputFilePath.endsWith("." + programFileExtension)) {
      IO.println("err: Input file must use the file extension: " + programFileExtension);
      System.exit(1);
    }

    String programName =
        inputFilePath.substring(inputFilePath.lastIndexOf("/") + 1, inputFilePath.lastIndexOf("."));

    String programContent = null;

    try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append(System.lineSeparator());
        line = br.readLine();
      }
      programContent = sb.toString();
    } catch (FileNotFoundException e) {
      IO.println("err: File not found");
    } catch (IOException e) {
      IO.println(e.getMessage());
    }

    if (programContent == null) {
      IO.println("err: Could not read input file");
      System.exit(1);
    }

    return new InputProgram(programName, programContent);
  }
}
