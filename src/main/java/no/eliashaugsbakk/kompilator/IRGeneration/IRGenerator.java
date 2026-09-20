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

  public List<Instruction> generate() {
    Program program = (Program) ast.root();

    for (Statement stmt : program.statements) {
      generateStatement(stmt);
    }
    if (Main.VERBOSE) {
      IO.println("======= IRGenerator =======");
      ir.forEach(IO::println);
      IO.println("\n\n\n\n");
    }
    return ir;
  }

  private void generateStatement(Statement stmt) {
    if (stmt instanceof ExpressionStatement exprStmt) {
      generateExpression(exprStmt.expression);
    } else if (stmt instanceof Assignment assignment) {
      generateAssignment(assignment);
    } else if (stmt instanceof IdentifierDeclaration identifierDecl) {
      generateIdentifierDecl(identifierDecl);
    }
  }

  private void generateAssignment(Assignment assignment) {
    String value;
    if (assignment.expression instanceof StringLiteral stringLiteral) {
      value = stringLiteral.value;
    } else {
      throw new IRGenerationException("Only strings are implemented");
    }
    ir.add(new Assign(assignment.identifier, value));
  }

  private void generateIdentifierDecl(IdentifierDeclaration identifierDecl) {
    String value;
    if (identifierDecl.initializer instanceof StringLiteral stringLiteral) {
      value = stringLiteral.value;
    } else {
      throw new IRGenerationException("Unknown function: " + identifierDecl.initializer);
    }
    ir.add(new Alloc(identifierDecl.identifier, identifierDecl.type.name(), identifierDecl.mutable,
        value));
  }

  private void generateExpression(Expression expr) {
    if (expr instanceof FunctionCall call) {
      generateFunctionCall(call);
    } else {
      throw new IRGenerationException("Only expression which can stand alone are functions");
    }
  }


  private void generateFunctionCall(FunctionCall fnCall) {
    List<String> arguments = new ArrayList<>();

    fnCall.arguments.forEach(arg -> {
      if (arg instanceof StringLiteral stringLiteral) {
        String temp = "t" + tempCounter++;
        ir.add(new Alloc(temp, "string", false, stringLiteral.value));
        arguments.add(temp);
      } else if (arg instanceof Identifier identifier) {
        arguments.add(identifier.name);
      } else {
        throw new IRGenerationException("Unknown function: " + arg);
      }
    });

    ir.add(new Call(fnCall.functionName, arguments));
  }
}
