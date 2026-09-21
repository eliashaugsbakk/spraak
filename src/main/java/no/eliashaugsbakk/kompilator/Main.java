package no.eliashaugsbakk.kompilator;

import java.util.Arrays;
import java.util.List;
import no.eliashaugsbakk.kompilator.IO.File;
import no.eliashaugsbakk.kompilator.IO.FileReaderWriter;
import no.eliashaugsbakk.kompilator.IRGeneration.IRGenerator;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Instruction;
import no.eliashaugsbakk.kompilator.asmGeneration.AssemblyBuilder;
import no.eliashaugsbakk.kompilator.assembleAndLink.AssemblerAndLinker;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.Parser;
import no.eliashaugsbakk.kompilator.semanticAnalysis.Analyzer;
import no.eliashaugsbakk.kompilator.tokenization.Lexer;
import no.eliashaugsbakk.kompilator.tokenization.Token;

public class Main {
  public static boolean VERBOSE = false;
  static final String programFileExtension = "spå";


  static void main(String[] args) {
    try {
      compile(args);
    } catch (CompilationException e) {
      IO.println("feil: " + e.getMessage());
      System.exit(1);
    }
  }

  private static void compile(String[] args) throws CompilationException {
    if (args.length == 0) {
      throw new CompilationException("Filsti må angis.");
    }

    VERBOSE = Arrays.asList(args).contains("--verbose") || Arrays.asList(args).contains("-v");

    String inputFileName = args[0];
    if (!inputFileName.endsWith("." + programFileExtension)) {
      throw new CompilationException("Inndatafilen må ha filendelsen: " + programFileExtension);
    }

    File inputProgram = new FileReaderWriter().readFile(inputFileName);
    List<Token> tokens = new Lexer(inputProgram.fileBody()).tokenize();
    AST ast = new Parser(tokens).parse();
    new Analyzer(ast).analyze();
    List<Instruction> IR = new IRGenerator(ast).generate();
    String assembly = new AssemblyBuilder().createAssembly(IR);
    new AssemblerAndLinker().assembleAndLink(inputProgram.fileName(), assembly);
  }
}
