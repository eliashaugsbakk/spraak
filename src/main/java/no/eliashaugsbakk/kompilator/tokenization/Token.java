package no.eliashaugsbakk.kompilator.tokenization;

public record Token(TokenType type, String value, Position position) {
}
