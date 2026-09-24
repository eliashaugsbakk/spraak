package no.eliashaugsbakk.kompilator.parsing.node.expression.literal;

import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.tokenization.Position;

public class NumberLiteral extends Expression {
  public final String value;

  public NumberLiteral(Position position, String value) {
    super(position);
    this.value = value;
  }

  public boolean isFloat() {
    return value.indexOf('.') >= 0;
  }
}
