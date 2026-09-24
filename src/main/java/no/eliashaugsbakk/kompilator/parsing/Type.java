package no.eliashaugsbakk.kompilator.parsing;


public record Type(String name, boolean nullable) {
  public static final Type VOID = new Type("tomt", false);

  public boolean isAssignableTo(Type target) {
    if (!this.name.equals(target.name)) return false;
    return !this.nullable || target.nullable;
  }
}

