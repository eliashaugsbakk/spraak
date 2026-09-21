package no.eliashaugsbakk.kompilator.parsing.node;

import java.util.ArrayList;
import java.util.List;
import no.eliashaugsbakk.kompilator.parsing.node.statement.Statement;
import no.eliashaugsbakk.kompilator.tokenization.Position;

/**
 * Root node of the AST. Contains all top-level statements.
 */
public class Program extends ASTNode {
  public final List<Statement> statements = new ArrayList<>();

  public Program(Position position) {
    super(position);
  }

  public void addStatement(Statement statement) {
    this.statements.add(statement);
  }
}
