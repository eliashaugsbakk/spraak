package no.eliashaugsbakk.kompilator.semanticAnalysis;

import no.eliashaugsbakk.kompilator.parsing.Type;

class Symbol {
  final String name;
  final Type type;
  final boolean mutable;
  boolean initialized;

  Symbol(String name, Type type, boolean mutable, boolean initialized) {
    this.name = name;
    this.type = type;
    this.mutable = mutable;
    this.initialized = initialized;
  }

  @Override
  public String toString() {
    return String.format("Symbol{name='%s', type=%s, mutable=%b, initialized=%b}",
        name, type, mutable, initialized);
  }
}
