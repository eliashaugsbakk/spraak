package no.eliashaugsbakk.kompilator.semanticAnalysis;

import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Statement;

public class Analyzer {
  private final AST ast;

  public Analyzer(AST ast) {
    this.ast = ast;
  }

  public void analyze() throws SemanticException {
    Program program = (Program) ast.getRoot();

    for (Statement stmt : program.statements) {
      analyzeStatement(stmt);
    }
  }

  private void analyzeStatement(Statement stmt) throws SemanticException {
    if (stmt instanceof ExpressionStatement exprStmt) {
      analyzeExpression(exprStmt.expression);
    } else {
      // Analyze the statement
      // NO other statements implemented
    }
  }

  private void analyzeExpression(Expression expr) throws SemanticException {
    if (expr instanceof FunctionCall call) {
      checkFunctionCall(call);
    } else {
      // analyze the expression
    }
  }

  private void checkFunctionCall(FunctionCall call) throws SemanticException {
    if (!call.functionName.equals("print")) {
      throw new SemanticException("unknown function: " + call.functionName);
    }

    if (call.arguments.size() != 1) {
      throw new SemanticException("print expects 1 argument, got " + call.arguments.size());
    }

    if (!(call.arguments.getFirst() instanceof StringLiteral)) {
      throw new SemanticException("print expects String argument only");
    }
  }
}
