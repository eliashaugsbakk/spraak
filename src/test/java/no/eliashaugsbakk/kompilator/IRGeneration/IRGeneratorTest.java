package no.eliashaugsbakk.kompilator.IRGeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Alloc;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Call;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Instruction;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.tokenization.Position;
import org.junit.jupiter.api.Test;

class IRGeneratorTest {

  @Test
  void singlePrintStatementGeneratesCorrectIR() {
    AST ast = buildAST("skriv", "hello");
    List<Instruction> ir = new IRGenerator(ast).generate();

    assertEquals(2, ir.size());
    assertEquals(new Alloc("t0", "string", false, "hello"), ir.get(0));
    assertEquals(new Call("skriv", List.of("t0")), ir.get(1));
  }

  @Test
  void multiplePrintStatementsGenerateSequentialTempVars() {
    Program program = new Program(null);
    program.addStatement(buildStatement("skriv", "hello"));
    program.addStatement(buildStatement("skriv", "world"));

    List<Instruction> ir = new IRGenerator(new AST(program)).generate();

    assertEquals(4, ir.size());
    assertEquals(new Alloc("t0", "string", false, "hello"), ir.get(0));
    assertEquals(new Call("skriv", List.of("t0")), ir.get(1));
    assertEquals(new Alloc("t1", "string", false, "world"), ir.get(2));
    assertEquals(new Call("skriv", List.of("t1")), ir.get(3));
  }

  private AST buildAST(String functionName, String argument) {
    return new AST(buildProgram(functionName, argument));
  }

  private Program buildProgram(String functionName, String argument) {
    Program program = new Program(null);
    program.addStatement(buildStatement(functionName, argument));
    return program;
  }

  private ExpressionStatement buildStatement(String functionName, String argument) {
    FunctionCall call = new FunctionCall(null, functionName,  List.of(new StringLiteral(null, argument)));
    return new ExpressionStatement(null, call);
  }
}
