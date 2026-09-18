package no.eliashaugsbakk.kompilator.parsing;

import no.eliashaugsbakk.kompilator.parsing.node.ASTNode;

public class AST {
  private final ASTNode root;

  public AST(ASTNode root) {
    this.root = root;
  }

  public ASTNode getRoot() {
    return root;
  }
}
