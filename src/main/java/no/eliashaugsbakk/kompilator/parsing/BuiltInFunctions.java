package no.eliashaugsbakk.kompilator.parsing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public enum BuiltInFunctions {

  SKRIV("skriv", new Signature(
      List.of(new Param("streng eller heltall", BuiltInFunctions::printable)),
      Type.VOID));


  private static boolean printable(Type type) {
    if (type.nullable()) return false;
    return DataTypes.fromName(type.name())
        .map(d -> d == DataTypes.STRENG || d.isInteger())
        .orElse(false);
  }

  public record Param(String description, Predicate<Type> accepts) {}
  public record Signature(List<Param> parameters, Type returnType) {}

  private static final Map<String, BuiltInFunctions> LOOKUP = new HashMap<>();

  static {
    for (BuiltInFunctions function : values()) {
      LOOKUP.put(function.functionName, function);
    }
  }

  private final String functionName;
  private final Signature signature;

  BuiltInFunctions(String functionName, Signature signature) {
    this.functionName = functionName;
    this.signature = signature;
  }

  public String functionName() {
    return functionName;
  }

  public Signature signature() {
    return signature;
  }

  public static Optional<BuiltInFunctions> fromName(String name) {
    return Optional.ofNullable(LOOKUP.get(name));
  }
}
