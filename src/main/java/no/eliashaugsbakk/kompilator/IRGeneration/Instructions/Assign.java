package no.eliashaugsbakk.kompilator.IRGeneration.Instructions;

import org.jetbrains.annotations.NotNull;

// assign a value to a variable: x = "Hello";
// Assign("x", "Hello")
public record Assign(String name, String value) implements Instruction {
  @Override
  @NotNull
  public String toString() {
    return "Assign[name=" + name + ", value=\"" + value + "\"]";
  }
}

