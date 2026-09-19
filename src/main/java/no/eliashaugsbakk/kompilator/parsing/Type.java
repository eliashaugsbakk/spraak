package no.eliashaugsbakk.kompilator.parsing;

public class Type {
  final String type;
  public final boolean nullable;

  public Type(String type, boolean nullable) {
    this.type = type;
    this.nullable = nullable;
  }
}

