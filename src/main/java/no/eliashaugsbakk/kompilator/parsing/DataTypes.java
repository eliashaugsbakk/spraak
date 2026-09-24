package no.eliashaugsbakk.kompilator.parsing;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum DataTypes {
  STRENG("streng", 8, TypeKind.STRING),
  I16("i16", 2, TypeKind.SIGNED_INT),
  I32("i32", 4, TypeKind.SIGNED_INT),
  I64("i64", 8, TypeKind.SIGNED_INT),
  U16("u16", 2, TypeKind.UNSIGNED_INT),
  U32("u32", 4, TypeKind.UNSIGNED_INT),
  U64("u64", 8, TypeKind.UNSIGNED_INT);


  private static final Map<String, DataTypes> LOOKUP = new HashMap<>();

  static {
    for (DataTypes type : values()) {
      LOOKUP.put(type.typeName, type);
    }
  }

  private final String typeName;
  private final int storageBytes;
  private final TypeKind kind;

  DataTypes(String typeName, int storageBytes, TypeKind kind) {
    this.typeName = typeName;
    this.storageBytes = storageBytes;
    this.kind = kind;
  }

  public String typeName() {
    return typeName;
  }

  public int storageBytes() {
    return storageBytes;
  }

  public boolean isInteger() {
    return kind == TypeKind.SIGNED_INT || kind == TypeKind.UNSIGNED_INT;
  }

  public static boolean isInteger(String typeName) {
    return fromName(typeName).map(DataTypes::isInteger).orElse(false);
  }

  public static Optional<DataTypes> fromName(String name) {
    return Optional.ofNullable(LOOKUP.get(name));
  }

  public static boolean contains(String name) {
    return LOOKUP.containsKey(name);
  }

  public boolean isSigned() {
    return kind == TypeKind.SIGNED_INT;
  }

  public BigInteger minValue() {
    return kind == TypeKind.SIGNED_INT ? BigInteger.valueOf(-1).shiftLeft(storageBytes * 8 - 1) :
        BigInteger.ZERO;
  }

  public BigInteger maxValue() {
    return kind == TypeKind.SIGNED_INT ? BigInteger.valueOf(1).shiftLeft(storageBytes * 8 - 1).subtract(BigInteger.ONE) :
        BigInteger.valueOf(1).shiftLeft(storageBytes * 8).subtract(BigInteger.ONE);
  }
}
