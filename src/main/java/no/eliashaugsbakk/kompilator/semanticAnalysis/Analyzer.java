package no.eliashaugsbakk.kompilator.semanticAnalysis;

import java.util.HashMap;
import java.util.Map;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.Type;
import no.eliashaugsbakk.kompilator.parsing.node.Program;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.parsing.node.expression.FunctionCall;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Identifier;
import no.eliashaugsbakk.kompilator.parsing.node.expression.literal.StringLiteral;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Assignment;
import no.eliashaugsbakk.kompilator.parsing.node.statement.ExpressionStatement;
import no.eliashaugsbakk.kompilator.parsing.node.statement.IdentifierDeclaration;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Statement;

public class Analyzer {
  private final AST ast;
  private final Map<String, Symbol> symbolTable = new HashMap<>();

  public Analyzer(AST ast) {
    this.ast = ast;
  }

  public void analyze() throws SemanticException {
    Program program = (Program) ast.root();

    for (Statement stmt : program.statements) {
      analyzeStatement(stmt);
    }
  }

  private void analyzeStatement(Statement stmt) throws SemanticException {
    switch (stmt) {
      case ExpressionStatement exprStmt -> typeOf(exprStmt.expression);
      case Assignment assignment -> analyzeAssignment(assignment);
      case IdentifierDeclaration decl -> analyzeIdentifierDeclaration(decl);
      default -> throw new SemanticException(stmt.position, "Ukjent programkonstruksjon");
    }
  }

  /**
   * Analyzes identifier declaration: set/mut x: type [= value];
   */
  private void analyzeIdentifierDeclaration(IdentifierDeclaration decl) throws SemanticException {
    boolean initialized = decl.initializer != null;
    boolean nullable = decl.type.nullable();
    Type declaredType = decl.type;

    // Ensure the type exists
    // string is the only type implemented, but should look in a type table or something in the future
    if (!declaredType.name().equals("streng")) {
      throw new SemanticException(decl.position, "Ukjent type: " + declaredType.name());
    }

    // Immutable variables must always be initialized on declaration
    if (!decl.mutable && !initialized) {
      throw new SemanticException(decl.position,
          "Uforanderlig variabel " + decl.identifier + " må initialiseres ved deklarasjon.");
    }

    // If no initializer, ensure type is nullable
    if (!initialized && !nullable) {
      throw new SemanticException(decl.position,
          "En type som ikke kan være null, må initialiseres");
    }

    // If initializer exists, validate type matches declared type
    if (initialized) {
      Type implementedType = typeOf(decl.initializer);
      checkAssignable(declaredType, implementedType);
    }

    // Shadowing is allowed; do not check to see if the symbol already exists
    symbolTable.put(decl.identifier,
        new Symbol(decl.identifier, declaredType, decl.mutable, initialized));
  }

  /**
   * Analyzes assignment: x = value;
   */
  private void analyzeAssignment(Assignment assignment) throws SemanticException {
    Symbol symbol = symbolTable.get(assignment.identifier);

    // Check existence
    if (symbol == null) {
      throw new SemanticException(assignment.position,
          "Variabelen er ikke deklarert: " + assignment.identifier);
    }

    // Check mutability
    if (!symbol.mutable) {
      throw new SemanticException(assignment.position,
          "Kan ikke tilordne en uforanderlig verdi: " + assignment.identifier);
    }

    // Type and Nullability Check
    Type assignedType = typeOf(assignment.expression);
    checkAssignable(symbol.type, assignedType);

    // Update symbol state
    symbol.initialized = true;
  }

  /**
   * Analyzes function call.
   */
  private void analyzeFunctionCall(FunctionCall call) throws SemanticException {
    // Validate function exists
    // skriv() is the only implemented function
    // Should ref. function table in the future
    if (!call.functionName.equals("skriv")) {
      throw new SemanticException(call.position,
          "Funksjonskallet støttes ikke: " + call.functionName);
    }

    // Validate argument count
    if (call.arguments.size() != 1) {
      throw new SemanticException(call.position,
          "skriv() støtter bare ett argument");
    }

    // Validate argument types
    for (Expression argument : call.arguments) {
      Type argType = typeOf(argument);

      if (!argType.name().equals("streng")) {
        throw new SemanticException(call.position,
            "skriv() støtter bare strengliteraler");
      }

      if (argType.nullable()) {
        throw new SemanticException(call.position,
            "Kan ikke skrive ut en nullbar streng: " + argType.name() + "?.");
      }
    }
  }

  private void checkAssignable(Type target, Type value) throws SemanticException {
    if (!target.name().equals(value.name())) {
      throw new SemanticException(null,
          "Typekonflikt: forventet " + target.name() + ", fant " + value.name());
    }
    if (value.nullable() && !target.nullable()) {
      throw new SemanticException(null,
          "Kan ikke tilordne nullbar " + value.name() + " til ikke-nullbar " + target.name());
    }
  }

  /**
   * Analyzes an expression, ensures all identifiers are declared and initialized, recursively
   * validates sub-expressions, and returns the resulting Type.
   */
  private Type typeOf(Expression expr) throws SemanticException {
    switch (expr) {
      case null -> throw new SemanticException(null, "Uttrykket kan ikke være null");
      case StringLiteral _ -> {
        return new Type("streng", false);
      }
      case Identifier id -> {
        Symbol symbol = symbolTable.get(id.name);

        if (symbol == null) {
          throw new SemanticException(expr.position,           "Udeklarert identifikator: " + id.name);
        }
        if (!symbol.initialized) {
          throw new SemanticException(expr.position, "Identifikatoren er ikke initialisert: " + id.name);
        }

        return symbol.type;
      }
      case FunctionCall call -> {
        analyzeFunctionCall(call);
        return call.getReturnType();
      }
      default -> {
      }
    }

    throw new SemanticException(expr.position, "Ukjent uttrykkstype: " + expr.getClass().getSimpleName());
  }
}
