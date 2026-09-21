package no.eliashaugsbakk.kompilator.parsing.node;

import no.eliashaugsbakk.kompilator.tokenization.Position;

/**
 * Base class for all nodes in the Abstract Syntax Tree.
 */
public abstract class ASTNode {
  public final Position position;


  public ASTNode(Position position) {
    this.position = position;
  }
}
