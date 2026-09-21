package no.eliashaugsbakk.kompilator.parsing.node.statement;

import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.tokenization.Position;

public class Assignment extends Statement {
  public final String identifier;
  public final Expression expression;

  public Assignment(Position position, String identifier, Expression expression) {
    super(position);
    this.identifier = identifier;
    this.expression = expression;
  }
}
