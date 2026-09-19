package no.eliashaugsbakk.kompilator.IRGeneration;

import java.util.ArrayList;
import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Statement;

public class IRGenerator {
  private final AST ast;
  private final List<String> ir = new ArrayList<>();
  private int tempVarCounter = 0;


  public IRGenerator(AST ast) {
    this.ast = ast;
  }

  public List<String> generate() {
    Program program = (Program) ast.getRoot();

    for (Statement stmt : program.statements) {
      generateStatement(stmt);
    }

    return ir;
  }

  private void generateStatement(Statement stmt) {
    if (stmt instanceof ExpressionStatement exprStmt) {
      generateExpression(exprStmt.expression);
    }
  }

  private void generateExpression(Expression expr) {
    if (expr instanceof FunctionCall call) {
      generateFunctionCall(call);
    }
  }

  private void generateFunctionCall(FunctionCall call) {
    for (Expression arg : call.arguments) {
      if (arg instanceof StringLiteral stringLit) {
        String tempVar = "t" + (tempVarCounter++);
        ir.add(tempVar + ": string = \"" + stringLit.value + "\"");
        ir.add(String.format("print(%s)", tempVar));
      }
    }
  }
}
