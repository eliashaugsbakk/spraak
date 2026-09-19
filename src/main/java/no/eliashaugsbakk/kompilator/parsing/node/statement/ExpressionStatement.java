package no.eliashaugsbakk.kompilator.parsing.node.statement;

import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;

/**
 * A statement which holds an expression.
 * print("hello"); does not have a return value; it is a statement, but it is also
 *    function call.
 */
public class ExpressionStatement extends Statement {
  public Expression expression;
  public ExpressionStatement(Expression expression) {
    this.expression = expression;
  }
}
