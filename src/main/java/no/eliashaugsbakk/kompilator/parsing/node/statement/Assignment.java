package no.eliashaugsbakk.kompilator.parsing.node.statement;

import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;

public class Assignment extends Statement {
  public final String identifier;
  public final Expression expression;

  public Assignment(String identifier, Expression expression) {
    this.identifier = identifier;
    this.expression = expression;
  }
}
