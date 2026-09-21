package no.eliashaugsbakk.kompilator.parsing.node.statement;

import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.tokenization.Position;

/**
 * A statement which holds an expression.
 * print("hello"); does not have a return value; it is a statement, but it is also
 *    function call.
 */
public class ExpressionStatement extends Statement {
  public final Expression expression;
  public ExpressionStatement(Position position, Expression expression) {
    super(position);
    this.expression = expression;
  }
}
