package no.eliashaugsbakk.kompilator.parsing.node.statement;

import no.eliashaugsbakk.kompilator.parsing.node.ASTNode;

/**
 * Base class for all statement nodes.
 * A statement is a top-level line of code that performs an action.
 * <p>
 * Examples:
 * - print("Hello");  (function call statement)
 * - var x: int = 5;  (variable declaration statement)
 * - if (x > 0) { }   (conditional statement)
 */
public abstract class Statement extends ASTNode {
}
