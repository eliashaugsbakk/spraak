package no.eliashaugsbakk.kompilator.IRGeneration.Instructions;

import org.jetbrains.annotations.NotNull;

// allocate a new variable: set x: streng = "Hello"; mut y: streng?;
//
// Alloc("x", "streng", false, "Hello") // Alloc("y", "streng", true, "null")
public record Alloc(String name, String type, boolean mutable, String initializer) implements Instruction {
  @Override
  @NotNull
  public String toString() {
    return "Alloc[name=" + name + ", type=" + type + ", mutable=" + mutable + ", initializer=\"" +
        initializer + "\"]";
  }
}
