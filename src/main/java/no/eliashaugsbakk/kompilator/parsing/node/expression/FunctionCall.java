package no.eliashaugsbakk.kompilator.parsing.node.expression;

import java.util.List;

/**
 * Represents a function call statement (e.g., print("Hello, world")).
 */
public class FunctionCall extends Expression {
  public String functionName;
  public List<Expression> arguments;

  public FunctionCall(String functionName, List<Expression> arguments) {
    this.functionName = functionName;
    this.arguments = arguments;
  }
}
