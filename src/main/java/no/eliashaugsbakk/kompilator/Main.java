package no.eliashaugsbakk.kompilator;

import java.util.List;
import no.eliashaugsbakk.kompilator.IO.File;
import no.eliashaugsbakk.kompilator.IO.FileReaderWriter;
import no.eliashaugsbakk.kompilator.IO.FileReaderWriterException;
import no.eliashaugsbakk.kompilator.IRGeneration.IRGenerator;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Instruction;
import no.eliashaugsbakk.kompilator.asmGeneration.AssemblyBuilder;
import no.eliashaugsbakk.kompilator.assembleAndLink.AssemblerAndLinker;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.Parser;
import no.eliashaugsbakk.kompilator.parsing.ParserException;
import no.eliashaugsbakk.kompilator.semanticAnalysis.Analyzer;
import no.eliashaugsbakk.kompilator.semanticAnalysis.SemanticException;
import no.eliashaugsbakk.kompilator.tokenization.Lexer;
import no.eliashaugsbakk.kompilator.tokenization.Token;

public class Main {
  public static final String programFileExtension = "spå";


  static void main(String[] args) {
    if (args.length != 1) {
      IO.println("err: Only accepts one argument: The filename of the input program");
      System.exit(1);
    }

    String inputFileName = args[0];

    if (!inputFileName.endsWith("." + programFileExtension)) {
      IO.println("err: Input file must use the file extension: " + programFileExtension);
      System.exit(1);
    }

    File inputProgram = null;
    FileReaderWriter fileReaderWriter = new FileReaderWriter();
    try {
      inputProgram = fileReaderWriter.readFile(inputFileName);
    } catch (FileReaderWriterException e) {
      IO.println("Could not read input file: " + inputFileName + "\n\n" + e.getMessage());
      System.exit(1);
    }

    List<Token> tokens = new Lexer(inputProgram.fileBody()).tokenize();
    AST ast = null;
    try {
      ast = new Parser(tokens).parse();
    } catch (ParserException e) {
      IO.println("Error while parsing: " + e.getMessage());
      System.exit(1);
    }

    try {
      new Analyzer(ast).analyze();
    } catch (SemanticException e) {
      IO.println("Semantic error: " + e.getMessage());
      System.exit(1);
    }

    List<Instruction> IR = new IRGenerator(ast).generate();

    String assembly = new AssemblyBuilder().createAssembly(IR);

    new AssemblerAndLinker().assembleAndLink(inputProgram.fileName(), assembly);

    System.exit(0);
  }
}
