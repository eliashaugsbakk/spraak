package no.eliashaugsbakk.kompilator.asmGeneration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Call;
import org.junit.jupiter.api.Test;

class AssemblyBuilderTest {

  @Test
  void boilerplateGetsGenerated() {
    AssemblyBuilder builder = new AssemblyBuilder();

    String result = builder.createAssembly(List.of());

    assertTrue(result.contains(".intel_syntax noprefix\n.global _start"));
    assertTrue(result.contains("mov rax, 60\nxor rdi, rdi\nsyscall"));
    assertTrue(result.contains(".data"));
  }

  @Test
  void handlePrintGeneratesCorrectAssemblyForImmutableVariable() {
    AssemblyBuilder builder = new AssemblyBuilder();
    builder.stringVariables.put("greeting", new AssemblyBuilder.StringVar("Hello", false));

    builder.handlePrint(new Call("skriv", List.of("greeting")));

    String result = builder.text.toString();
    assertTrue(result.contains("lea rsi, greeting"));
    assertTrue(result.contains("mov rdx, 5"));
  }

  @Test
  void handlePrintGeneratesCorrectAssemblyForMutableVariable() {
    AssemblyBuilder builder = new AssemblyBuilder();
    builder.stringVariables.put("greeting", new AssemblyBuilder.StringVar("Hello", true));

    builder.handlePrint(new Call("skriv", List.of("greeting")));

    String result = builder.text.toString();
    assertTrue(result.contains("mov rsi, [rip + greeting]"));
    assertTrue(result.contains("mov rdx, 5"));
  }

  @Test
  void handlePrintCorrectlyCountsNewlineAsOneByte() {
    AssemblyBuilder builder = new AssemblyBuilder();
    builder.stringVariables.put("msg", new AssemblyBuilder.StringVar("Hello\n", false));

    builder.handlePrint(new Call("skriv", List.of("msg")));

    assertTrue(builder.text.toString().contains("mov rdx, 6"));
  }

  @Test
  void handlePrintWorksCorrectlyWithMultiplePrintStatements() {
    AssemblyBuilder builder = new AssemblyBuilder();
    builder.stringVariables.put("msg1", new AssemblyBuilder.StringVar("Hi", false));
    builder.stringVariables.put("msg2", new AssemblyBuilder.StringVar("World", false));

    builder.handlePrint(new Call("skriv", List.of("msg1")));
    builder.handlePrint(new Call("skriv", List.of("msg2")));

    String result = builder.text.toString();
    assertTrue(result.contains("lea rsi, msg1"));
    assertTrue(result.contains("lea rsi, msg2"));
    assertTrue(result.contains("mov rdx, 2"));
    assertTrue(result.contains("mov rdx, 5"));
  }
}
