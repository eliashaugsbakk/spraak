package no.eliashaugsbakk.kompilator;

import java.util.List;
import org.junit.jupiter.api.Test;

class AssemblyBuilderTest {

  @Test
  void boilerplateGetsGenerated() {
    // Setup
    AssemblyBuilder builder = new AssemblyBuilder();

    // Act

    // Assert
    String result = builder.createAssembly(List.of());
    assert result.contains("""
        .intel_syntax noprefix
        .global _start
        .text
        _start: 
        """);

    assert result.contains("""
        mov rax, 60
        xor rdi, rdi
        syscall
        """);
    assert result.contains(".data");
  }

  @Test
  void handlePrintGeneratesCorrectAssemblyForSingleStatement() {
    // Setup
    AssemblyBuilder builder = new AssemblyBuilder();
    builder.stringVariables.put("greeting", "Hello");

    // Act
    builder.handlePrint("print(greeting)");

    // Assert
    String result = builder.text.toString();
    assert result.contains("lea rsi, [greeting]");
    assert result.contains("lea rdx, [5]"); // "Hello"
  }

  @Test
  void handlePrintWorksCorrectlyWithMultiplePrintStatements() {
    // Setup
    AssemblyBuilder builder = new AssemblyBuilder();
    builder.stringVariables.put("msg1", "Hi");
    builder.stringVariables.put("msg2", "World");

    // Act
    builder.handlePrint("print(msg1)");
    builder.handlePrint("print(msg2)");

    // Assert
    String result = builder.text.toString();
    assert (result).contains("lea rsi, [msg1]");
    assert (result).contains("lea rsi, [msg2]");
    assert (result).contains("lea rdx, [2]");  // "Hi"
    assert (result).contains("lea rdx, [5]"); // "World"
  }
}
