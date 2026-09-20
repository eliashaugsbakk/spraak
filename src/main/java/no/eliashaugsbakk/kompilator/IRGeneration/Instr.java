package no.eliashaugsbakk.kompilator.IRGeneration;

import java.util.List;

public sealed interface Instr permits Alloc, Assign, Call {
}


// allocate a new variable: set x: string = "Hello"; mut y: string?;
//
// Alloc("x", "string", false, "Hello") // Alloc("y", "string", true, "null")
record Alloc(String name, String type, boolean mutable, String initializer) implements Instr {
  @Override
  public String toString() {
    return "Alloc[name=" + name + ", type=" + type + ", mutable=" + mutable + ", initializer=\"" + initializer + "\"]";
  }
}


// assign a value to a variable: x = "Hello";
// Assign("x", "Hello")
record Assign(String name, String value) implements Instr {
  @Override
  public String toString() {
    return "Assign[name=" + name + ", value=\"" + value + "\"]";
  }
}


// call a function: skriv("hello");
// Call("skriv", ["t1"])
record Call(String fn, List<String> args) implements Instr {
}
