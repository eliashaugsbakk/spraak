package no.eliashaugsbakk.kompilator.IRGeneration;

import java.util.ArrayList;
import java.util.List;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Alloc;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Assign;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Call;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Instruction;
import no.eliashaugsbakk.kompilator.Main;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Identifier;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.NumberLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Assignment;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.parsing.node.statement.IdentifierDeclaration;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Statement;

public class IRGenerator {
  private final AST ast;
  private final List<Instruction> ir = new ArrayList<>();
  private int tempCounter = 0;


  public IRGenerator(AST ast) {
    this.ast = ast;
  }

  public List<Instruction> generate() throws IRGenerationException {
    Program program = (Program) ast.root();

    for (Statement stmt : program.statements) {
      generateStatement(stmt);
    }
    if (Main.VERBOSE) {
      IO.println("======= IR-generator =======");
      ir.forEach(IO::println);
      IO.println("\n\n\n\n");
    }
    return ir;
  }

  private void generateStatement(Statement stmt) throws IRGenerationException {
    if (stmt instanceof ExpressionStatement exprStmt) {
      generateExpression(exprStmt.expression);
    } else if (stmt instanceof Assignment assignment) {
      generateAssignment(assignment);
    } else if (stmt instanceof IdentifierDeclaration identifierDecl) {
      generateIdentifierDecl(identifierDecl);
    }
  }

  private void generateAssignment(Assignment assignment) throws IRGenerationException {
    String value;
    if (assignment.expression instanceof StringLiteral stringLiteral) {
      value = stringLiteral.value;
    } else {
      throw new IRGenerationException(assignment.position, "Bare strenger er implementert");
    }
    ir.add(new Assign(assignment.identifier, value));
  }

  private void generateIdentifierDecl(IdentifierDeclaration identifierDecl) throws IRGenerationException {
    String value;
    if (identifierDecl.initializer instanceof StringLiteral stringLiteral) {
      value = stringLiteral.value;
    } else if (identifierDecl.initializer instanceof NumberLiteral numberLiteral) {
        value = numberLiteral.value;
    } else {
      throw new IRGenerationException(identifierDecl.position, "Ukjent funksjon: " + identifierDecl.initializer);
    }
    ir.add(new Alloc(identifierDecl.identifier, identifierDecl.type.name(), identifierDecl.mutable,
        value));
  }

  private void generateExpression(Expression expr) throws IRGenerationException {
    if (expr instanceof FunctionCall call) {
      generateFunctionCall(call);
    } else {
      throw new IRGenerationException(expr.position, "Bare funksjoner kan stå alene som uttrykk");
    }
  }


  private void generateFunctionCall(FunctionCall fnCall) throws IRGenerationException {
    List<String> arguments = new ArrayList<>();

    for (Expression arg : fnCall.arguments) {
      if (arg instanceof StringLiteral stringLiteral) {
        String temp = "t" + tempCounter++;
        ir.add(new Alloc(temp, "streng", false, stringLiteral.value));
        arguments.add(temp);
      } else if (arg instanceof Identifier identifier) {
        arguments.add(identifier.name);
      } else {
        throw new IRGenerationException(fnCall.position, "Ukjent funksjon: " + arg);
      }
    }

    ir.add(new Call(fnCall.functionName, arguments));
  }
}
