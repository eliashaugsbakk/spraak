package no.eliashaugsbakk.kompilator.semanticAnalysis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.Type;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Identifier;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.NumberLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.parsing.node.statement.IdentifierDeclaration;
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
    AST ast = buildASTNoArgs("skriv");
    assertThrows(SemanticException.class, () -> new Analyzer(ast).analyze());
  }

  @Test
  void integerLiteralCanBePrinted() {
    FunctionCall call = new FunctionCall(null, "skriv",
        List.of(new NumberLiteral(null, "5")));
    Program program = new Program(null);
    program.addStatement(new ExpressionStatement(null, call));

    assertDoesNotThrow(() -> new Analyzer(new AST(program)).analyze());
  }

  @Test
  void floatingPointLiteralCannotBePrinted() {
    FunctionCall call = new FunctionCall(null, "skriv",
        List.of(new NumberLiteral(null, "5.0")));
    Program program = new Program(null);
    program.addStatement(new ExpressionStatement(null, call));

    SemanticException exception = assertThrows(SemanticException.class,
        () -> new Analyzer(new AST(program)).analyze());
    assertTrue(exception.getMessage().contains("Desimaltall støttes ikke ennå"));
  }

  @Test
  void nullableStringCannotBePrinted() {
    Program program = new Program(null);
    program.addStatement(new IdentifierDeclaration(null, "x", new Type("streng", true),
        new StringLiteral(null, "hello"), false));
    program.addStatement(new ExpressionStatement(null,
        new FunctionCall(null, "skriv", List.of(new Identifier(null, "x")))));

    assertThrows(SemanticException.class, () -> new Analyzer(new AST(program)).analyze());
  }

  @Test
  void integerIdentifierCanBePrinted() {
    Program program = new Program(null);
    program.addStatement(new IdentifierDeclaration(null, "x", new Type("i64", false),
        new NumberLiteral(null, "5"), false));
    program.addStatement(new ExpressionStatement(null,
        new FunctionCall(null, "skriv", List.of(new Identifier(null, "x")))));

    assertDoesNotThrow(() -> new Analyzer(new AST(program)).analyze());
  }

  @Test
  void integerLiteralCanBeAssignedToIntegerType() {
    Program program = new Program(null);
    program.addStatement(new IdentifierDeclaration(null, "x", new Type("i64", false),
        new NumberLiteral(null, "5"), false));

    assertDoesNotThrow(() -> new Analyzer(new AST(program)).analyze());
  }

  @Test
  void floatingPointLiteralCannotBeAssignedToIntegerType() {
    Program program = new Program(null);
    program.addStatement(new IdentifierDeclaration(null, "x", new Type("i64", false),
        new NumberLiteral(null, "5.0"), false));

    SemanticException exception = assertThrows(SemanticException.class,
        () -> new Analyzer(new AST(program)).analyze());
    assertTrue(exception.getMessage().contains("Typekonflikt"));
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
