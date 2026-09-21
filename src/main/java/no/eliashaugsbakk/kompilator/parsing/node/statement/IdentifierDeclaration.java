package no.eliashaugsbakk.kompilator.parsing.node.statement;

import no.eliashaugsbakk.kompilator.parsing.Type;
import no.eliashaugsbakk.kompilator.parsing.node.expression.Expression;
import no.eliashaugsbakk.kompilator.tokenization.Position;

public class IdentifierDeclaration extends Statement {
  // identifier name: [my_var]: type = 4;
  public final String identifier;

  // identifier type: my_var: [type] = 4;
  public final Type type;

  // identifier initialization:  my_var: type [4];
  // may be null: my_var: type?;
  public final Expression initializer; // may be null: x: int?;

  public final boolean mutable;

  public IdentifierDeclaration(Position position, String identifier, Type type, Expression initializer, boolean mutable) {
    super(position);
    this.identifier = identifier;
    this.type = type;
    this.initializer = initializer;
    this.mutable = mutable;
  }
}
