package no.eliashaugsbakk.kompilator.semanticAnalysis;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.eliashaugsbakk.kompilator.parsing.AST;
import no.eliashaugsbakk.kompilator.parsing.BuiltInFunctions;
import no.eliashaugsbakk.kompilator.parsing.DataTypes;
import no.eliashaugsbakk.kompilator.parsing.Type;
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
import no.eliashaugsbakk.kompilator.tokenization.Position;

public class Analyzer {
  private static final Type DEFAULT_INTEGER_TYPE = new Type("i64", false);

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

    // Ensure the type exists
    if (!DataTypes.contains(decl.type.name())) {
      throw new SemanticException(decl.position, "Ukjent type: " + decl.type.name());
    }

    // Immutable variables must always be initialized on declaration
    if (!decl.mutable && !initialized) {
      throw new SemanticException(decl.position,
          "Uforanderlig variabel " + decl.identifier + " må initialiseres ved deklarasjon.");
    }

    // If no initializer, ensure type is nullable
    if (!initialized && !decl.type.nullable()) {
      throw new SemanticException(decl.position,
          "En type som ikke kan være null, må initialiseres");
    }

    // If initializer exists, validate type matches declared type
    if (initialized) {
      checkValue(decl.initializer, decl.type);
    }

    // Shadowing is allowed; do not check to see if the symbol already exists
    symbolTable.put(decl.identifier,
        new Symbol(decl.identifier, decl.type, decl.mutable, initialized));
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
    checkValue(assignment.expression, symbol.type);

    // Update symbol state
    symbol.initialized = true;
  }


  private Type analyzeFunctionCall(FunctionCall call) throws SemanticException {
    BuiltInFunctions function = BuiltInFunctions.fromName(call.functionName)
        .orElseThrow(() -> new SemanticException(call.position,
            "Ukjent funksjon: " + call.functionName));

    List<BuiltInFunctions.Param> expected = function.signature().parameters();
    if (call.arguments.size() != expected.size()) {
      throw new SemanticException(call.position,
          call.functionName + "() forventer " + expected.size()
              + " argument(er), fikk " + call.arguments.size());
    }

    for (int i = 0; i < expected.size(); i++) {
      checkFunctionArgument(call, call.arguments.get(i), expected.get(i), i);
    }

    return function.signature().returnType();
  }

  private void checkFunctionArgument(FunctionCall call, Expression argument,
      BuiltInFunctions.Param parameter, int index) throws SemanticException {
    Type actual = typeOf(argument);
    if (argument instanceof NumberLiteral number) {
      checkNumberFits(number, DEFAULT_INTEGER_TYPE);
    }
    if (parameter.accepts().test(actual)) {
      return;
    }

    throw new SemanticException(argument.position,
        call.functionName + "() argument " + (index + 1)
            + " forventet " + parameter.description() + ", fant " + describe(actual));
  }

  private String describe(Type type) {
    return type.nullable() ? type.name() + "?" : type.name();
  }

  private void checkAssignable(Type target, Type value, Position pos) throws SemanticException {
    if (value.isAssignableTo(target)) {
      return;
    }
    if (!value.name().equals(target.name())) {
      throw new SemanticException(pos,
          "Typekonflikt: forventet " + target.name() + ", fant " + value.name());
    }
    throw new SemanticException(pos,
        "Kan ikke tilordne nullbar " + value.name() + " til ikke-nullbar " + target.name());
  }

  private void checkNumberFits(NumberLiteral number, Type target) throws SemanticException {
    DataTypes type = DataTypes.fromName(target.name())
        .filter(DataTypes::isInteger)
        .orElseThrow(() -> new SemanticException(number.position,
            "Typekonflikt: forventet " + target.name() + ", fant tall"));

    if (number.isFloat()) {
      throw new SemanticException(number.position,
          "Typekonflikt: forventet " + target.name() + ", fant desimaltall");
    }

    BigInteger value = new BigInteger(number.value);

    if (value.signum() < 0 && !type.isSigned()) {
      throw new SemanticException(number.position,
          "Kan ikke tilordne negativt tall '" + value + "' til '" + type.typeName() + "'");
    }
    if (value.compareTo(type.minValue()) < 0 || value.compareTo(type.maxValue()) > 0) {
      throw new SemanticException(number.position,
          "Tallet '" + value + "' er utenfor området for '" + type.typeName() + "'"
              + " (" + type.minValue() + " til " + type.maxValue() + ")");
    }
  }

  private void checkValue(Expression expr, Type target) throws SemanticException {
    if (expr instanceof NumberLiteral number) {
      checkNumberFits(number, target);
    } else {
      checkAssignable(target, typeOf(expr), expr.position);
    }
  }

  /**
   * Analyzes an expression, ensures all identifiers are declared and initialized, recursively
   * validates sub-expressions, and returns the resulting Type.
   */
  private Type typeOf(Expression expr) throws SemanticException {
    return switch (expr) {
      case null -> throw new SemanticException(null, "Uttrykket kan ikke være null");
      case StringLiteral _ -> new Type("streng", false);
      case NumberLiteral number -> typeOfNumberLiteral(number);
      case Identifier id -> typeOfIdentifier(id);
      case FunctionCall call -> analyzeFunctionCall(call);
      default -> throw new SemanticException(expr.position,
          "Ukjent uttrykkstype: " + expr.getClass().getSimpleName());
    };
  }

  private Type typeOfNumberLiteral(NumberLiteral number) throws SemanticException {
    if (number.isFloat()) {
      throw new SemanticException(number.position,
          "Desimaltall støttes ikke ennå");
    }
    return DEFAULT_INTEGER_TYPE;
  }

  private Type typeOfIdentifier(Identifier id) throws SemanticException {
    Symbol symbol = symbolTable.get(id.name);
    if (symbol == null) {
      throw new SemanticException(id.position, "Udeklarert identifikator: " + id.name);
    }
    if (!symbol.initialized) {
      throw new SemanticException(id.position,
          "Identifikatoren er ikke initialisert: " + id.name);
    }
    return symbol.type;
  }
}
