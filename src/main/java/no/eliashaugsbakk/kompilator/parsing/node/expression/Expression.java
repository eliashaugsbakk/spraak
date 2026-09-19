package no.eliashaugsbakk.kompilator.parsing.node.expression;

import no.eliashaugsbakk.kompilator.parsing.node.ASTNode;

/**
 * Base class for all expression nodes.
 * An expression is a piece of code that evaluates to a value. (i.e., returns a value)
 * Expressions cannot stand alone as statements; they must be used within statements.
 * <p>
 * Examples:
 * - "Hello"          (string literal expression)
 * - 42               (number literal expression)
 * - x + 5            (binary operation expression)
 * - myFunction()     (function call expression)
 */
public abstract class Expression extends ASTNode {
}
