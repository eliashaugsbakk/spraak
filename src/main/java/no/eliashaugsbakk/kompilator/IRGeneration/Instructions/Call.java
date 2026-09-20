package no.eliashaugsbakk.kompilator.IRGeneration.Instructions;

import java.util.List;

// call a function: skriv("hello");
// Call("skriv", ["t1"])
public record Call(String fn, List<String> args) implements Instruction {
}
