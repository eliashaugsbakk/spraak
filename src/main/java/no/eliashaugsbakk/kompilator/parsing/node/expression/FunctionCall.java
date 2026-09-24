package no.eliashaugsbakk.kompilator.parsing.node.expression;

import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.BuiltInFunctions;
import no.eliashaugsbakk.kompilator.parsing.Type;
import no.eliashaugsbakk.kompilator.tokenization.Position;

/**
 * Represents a function call statement (e.g., print("Hello, world")).
 */
public class FunctionCall extends Expression {
  public final String functionName;
  public final List<Expression> arguments;

  public FunctionCall(Position position, String functionName, List<Expression> arguments) {
    super(position);
    this.functionName = functionName;
    this.arguments = arguments;
  }

  public Type getReturnType() {
    return BuiltInFunctions.fromName(functionName)
        .map(function -> function.signature().returnType())
        .orElse(null);
  }
}
