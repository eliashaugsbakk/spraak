package no.eliashaugsbakk.kompilator.semanticAnalysis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import org.junit.jupiter.api.Test;

class AnalyzerTest {

  @Test
  void validPrintStatementDoesNotThrow() {
    AST ast = buildAST("print", "hello");
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
    FunctionCall call = new FunctionCall(functionName, List.of(new StringLiteral(argument)));
    ExpressionStatement stmt = new ExpressionStatement(call);
    Program program = new Program();
    program.addStatement(stmt);
    return new AST(program);
  }

  private AST buildASTNoArgs(String functionName) {
    FunctionCall call = new FunctionCall(functionName, List.of());
    ExpressionStatement stmt = new ExpressionStatement(call);
    Program program = new Program();
    program.addStatement(stmt);
    return new AST(program);
  }
}
