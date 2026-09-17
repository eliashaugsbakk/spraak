package no.eliashaugsbakk.kompilator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssemblyBuilder {
  Map<String, String> stringVariables = new HashMap<>();

  StringBuilder finalAssembly;
  StringBuilder text;
  StringBuilder rodata;
  StringBuilder data;

  private final static String exit = """
      mov rax, 60
      xor rdi, rdi
      syscall
      """;

  public AssemblyBuilder() {
    this.finalAssembly = new StringBuilder(".intel_syntax noprefix\n.global _start\n");
    // CPU instructions
    this.text = new StringBuilder(".text\n_start:\n\n");
    // Read only data
    this.rodata = new StringBuilder("\n.section .rodata\n");
    // Global initialized variables, Read - Write
    this.data = new StringBuilder("\n.data\n");
  }

  String createAssembly(List<String> IR) {
    for (String line : IR) {
      line = line.trim();

      if (line.startsWith("print")) {
        handlePrint(line);
      } else if (line.contains("=")) {
        handleAssignment(line);
      }
    }
    this.text.append(exit);
    this.finalAssembly.append(text);
    this.finalAssembly.append(rodata);
    this.finalAssembly.append(data);
    return this.finalAssembly.toString();
  }

  private void handleAssignment(String line) {
    /* Example IR:
    variable_name: type = data
    str1: string = "Hello world"
     */
    String name = line.substring(0, line.indexOf(":"));

    String dataType = line.substring(line.indexOf(":") + 2, line.indexOf("=") - 1);

    String value = line.substring(line.indexOf("=") + 2);

    if (dataType.contains("string")) {
      assignString(name, value);
    } else {
      IO.println("err: Unknown data type: " + dataType);
    }
  }

  private void assignString(String name, String value) {
    value = value.substring(1, value.length() - 1);
    this.rodata.append(String.format("""
        %s: .ascii "%s"
        """, name, value));
    this.stringVariables.put(name, value);
  }

  void handlePrint(String line) {
    String variableName = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")"));

    this.text.append(String.format("""
        mov rax, 1
        mov rdi, 1
        lea rsi, [%s]
        lea rdx, [%d]
        syscall
        
        """, variableName, this.stringVariables.get(variableName).length()));
  }
}
