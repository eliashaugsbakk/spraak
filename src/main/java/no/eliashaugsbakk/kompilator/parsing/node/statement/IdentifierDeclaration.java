package no.eliashaugsbakk.kompilator.parsing.node.statement;

import no.eliashaugsbakk.kompilator.parsing.Type;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;

public class IdentifierDeclaration extends Statement {
  // identifier name: [my_var]: type = 4;
  final String identifier;

  // identifier type: my_var: [type] = 4;
  final Type type;

  // identifier initialization:  my_var: type [4];
  // may be null: my_var: type?;
  final Expression expression; // may be null: x: int?;

  public IdentifierDeclaration(String identifier, Type type, Expression expression) {
    this.identifier = identifier;
    this.type = type;
    this.expression = expression;
  }
}
