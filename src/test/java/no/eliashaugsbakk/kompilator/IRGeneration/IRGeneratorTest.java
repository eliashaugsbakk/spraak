package no.eliashaugsbakk.kompilator.IRGeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import org.junit.jupiter.api.Test;

class IRGeneratorTest {

  @Test
  void singlePrintStatementGeneratesCorrectIR() {
    AST ast = buildAST("skriv", "hello");
    List<Instr> ir = new IRGenerator(ast).generate();

    assertEquals(2, ir.size());
    assertEquals(new Alloc("t0", "string", false, "hello"), ir.get(0));
    assertEquals(new Call("skriv", List.of("t0")), ir.get(1));
  }

  @Test
  void multiplePrintStatementsGenerateSequentialTempVars() {
    Program program = new Program();
    program.addStatement(buildStatement("skriv", "hello"));
    program.addStatement(buildStatement("skriv", "world"));

    List<Instr> ir = new IRGenerator(new AST(program)).generate();

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
    Program program = new Program();
    program.addStatement(buildStatement(functionName, argument));
    return program;
  }

  private ExpressionStatement buildStatement(String functionName, String argument) {
    FunctionCall call = new FunctionCall(functionName, List.of(new StringLiteral(argument)));
    return new ExpressionStatement(call);
  }
}
