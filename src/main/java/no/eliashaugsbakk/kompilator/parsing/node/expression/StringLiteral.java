package no.eliashaugsbakk.kompilator.parsing.node.expression;

/**
 * Represents a string literal expression (e.g., "Hello, World").
 */
public class StringLiteral extends Expression {
  public final String value;

  public StringLiteral(String value) {
    this.value = value;
  }
}
