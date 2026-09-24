package no.eliashaugsbakk.kompilator.tokenization;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum KeyWords {
  SET("set"),
  MUT("mut");

  private final String text;
  private static final Map<String, KeyWords> LOOKUP = new HashMap<>();

  static {
    for (KeyWords kw : values()) {
      LOOKUP.put(kw.text, kw);
    }
  }

  KeyWords(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public static boolean contains(String text) {
    return LOOKUP.containsKey(text);
  }

  public static Optional<KeyWords> fromText(String text) {
    return Optional.ofNullable(LOOKUP.get(text));
  }
}
