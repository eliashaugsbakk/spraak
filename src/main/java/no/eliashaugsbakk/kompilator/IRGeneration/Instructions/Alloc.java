package no.eliashaugsbakk.kompilator.IRGeneration.Instructions;

// allocate a new variable: set x: string = "Hello"; mut y: string?;
//
// Alloc("x", "string", false, "Hello") // Alloc("y", "string", true, "null")
public record Alloc(String name, String type, boolean mutable, String initializer) implements Instruction {
  @Override
  public String toString() {
    return "Alloc[name=" + name + ", type=" + type + ", mutable=" + mutable + ", initializer=\"" +
        initializer + "\"]";
  }
}
