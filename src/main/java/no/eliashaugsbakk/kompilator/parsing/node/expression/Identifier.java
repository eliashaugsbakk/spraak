package no.eliashaugsbakk.kompilator.parsing.node.expression;

import no.eliashaugsbakk.kompilator.tokenization.Position;

/**
 * Resolves to a variable's value
 */
public class Identifier extends Expression {
  public final String name;

  public Identifier(Position position, String name) {
    super(position);
    this.name = name;
  }
}
