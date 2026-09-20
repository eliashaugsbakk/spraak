package no.eliashaugsbakk.kompilator.IRGeneration.Instructions;

// assign a value to a variable: x = "Hello";
// Assign("x", "Hello")
public record Assign(String name, String value) implements Instruction {
  @Override
  public String toString() {
    return "Assign[name=" + name + ", value=\"" + value + "\"]";
  }
}

