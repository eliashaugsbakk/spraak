package no.eliashaugsbakk.kompilator.parsing;

import java.util.Objects;

public class Type {
  public final String type;
  public final boolean nullable;

  public Type(String type, boolean nullable) {
    this.type = type;
    this.nullable = nullable;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Type t) {
      return this.type.equals(t.type) && this.nullable == t.nullable;
    } else  {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, nullable);
  }
}

