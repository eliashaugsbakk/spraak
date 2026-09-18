package no.eliashaugsbakk.kompilator.parsing.node.statement;

import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;

public class ExpressionStatement extends Statement {
  public Expression expression;
  public ExpressionStatement(Expression expression) {
    this.expression = expression;
  }
}
