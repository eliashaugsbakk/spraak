package no.eliashaugsbakk.kompilator.parsing;


public record Type(String name, boolean nullable) {
  boolean isAssignableTo(Type target) {
    if (!this.name.equals(target.name)) return false;
    if (this.nullable && !target.nullable) return false;
    return true;
  }
}

