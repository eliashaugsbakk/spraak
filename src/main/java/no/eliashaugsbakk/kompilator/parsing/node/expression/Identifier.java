package no.eliashaugsbakk.kompilator.parsing.node.expression;

/**
 * Resolves to a variable's value
 */
public class Identifier extends Expression {
  public final String name;

  public Identifier(String name) {
    this.name = name;
  }
}
