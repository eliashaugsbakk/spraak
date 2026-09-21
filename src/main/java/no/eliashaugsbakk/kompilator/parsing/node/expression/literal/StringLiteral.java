package no.eliashaugsbakk.kompilator.parsing.node.expression.literal;

import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.tokenization.Position;

/**
 * Represents a string literal expression (e.g., "Hello, World").
 */
public class StringLiteral extends Expression {
  public final String value;

  public StringLiteral(Position position, String value) {
    super(position);
    this.value = value;
  }
}
