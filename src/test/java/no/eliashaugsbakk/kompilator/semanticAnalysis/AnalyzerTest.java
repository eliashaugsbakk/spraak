package no.eliashaugsbakk.kompilator.semanticAnalysis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import org.junit.jupiter.api.Test;

class AnalyzerTest {

  @Test
  void validPrintStatementDoesNotThrow() {
    AST ast = buildAST("skriv", "hello");
    assertDoesNotThrow(() -> new Analyzer(ast).analyze());
  }

  @Test
  void unknownFunctionThrows() {
    AST ast = buildAST("unknown", "hello");
    assertThrows(SemanticException.class, () -> new Analyzer(ast).analyze());
  }

  @Test
  void missingArgumentThrows() {
    AST ast = buildASTNoArgs("print");
    assertThrows(SemanticException.class, () -> new Analyzer(ast).analyze());
  }

  private AST buildAST(String functionName, String argument) {
    FunctionCall call = new FunctionCall(null, functionName, List.of(new StringLiteral(null, argument)));
    ExpressionStatement stmt = new ExpressionStatement(null, call);
    Program program = new Program(null);
    program.addStatement(stmt);
    return new AST(program);
  }

  private AST buildASTNoArgs(String functionName) {
    FunctionCall call = new FunctionCall(null, functionName, List.of());
    ExpressionStatement stmt = new ExpressionStatement(null, call);
    Program program = new Program(null);
    program.addStatement(stmt);
    return new AST(program);
  }
}
