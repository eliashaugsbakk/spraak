package no.eliashaugsbakk.kompilator.asmGeneration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Alloc;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Assign;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Call;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Instruction;

public class AssemblyBuilder {
  final Map<String, StringVar> stringVariables = new HashMap<>();

  record StringVar(String value, boolean mutable) {
  }

  final StringBuilder finalAssembly;
  final StringBuilder text;
  final StringBuilder rodata;
  final StringBuilder data;
  final StringBuilder bss;

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
    // Global uninitialized variables, Read - Write
    this.bss = new StringBuilder("\n.section .bss\n");
  }

  public String createAssembly(List<Instruction> IR) {
    for (Instruction inst : IR) {
      if (inst instanceof Alloc alloc) {
        handleAlloc(alloc);
      } else if (inst instanceof Assign assign) {
        handleAssign(assign);
      } else if (inst instanceof Call call) {
        handleCall(call);
      }
    }

    this.text.append(exit);

    this.finalAssembly.append(text);
    this.finalAssembly.append(rodata);
    this.finalAssembly.append(data);
    this.finalAssembly.append(bss);

    return this.finalAssembly.toString();
  }

  private void handleAssign(Assign assign) {
    String reassignLabel = assign.name() + "_reassign";
    this.stringVariables.put(assign.name(), new StringVar(assign.value(), true));

    this.rodata.append(String.format("""
        %s: .ascii "%s"
        """, reassignLabel, assign.value()));

    this.text.append(String.format("""
        lea rax, [rip + %s]
        mov [rip + %s], rax
        """, reassignLabel, assign.name()));
  }

  private void handleAlloc(Alloc alloc) {
    this.stringVariables.put(alloc.name(), new StringVar(alloc.initializer(), alloc.mutable()));
    if (alloc.mutable()) {
      handleAllocMut(alloc);
    } else {
      handleAllocRO(alloc);
    }
  }

  private void handleAllocMut(Alloc alloc) {
    if (alloc.type() == null) {
      this.bss.append(String.format("""
          %s: .skip 8
          """, alloc.name()));
    } else if (alloc.type().equals("streng")) {
      String pointer = alloc.name() + "_ptr";
      this.rodata.append(String.format("""
          %s: .ascii "%s"
          """, pointer, alloc.initializer()));
      this.data.append(String.format("""
          %s: .quad %s
          """, alloc.name(), pointer));
    } else {
      throw new AssemblyBuilderException("Typen støttes ikke");
    }
  }

  private void handleAllocRO(Alloc alloc) {
    if (alloc.type().equals("streng")) {
      this.rodata.append(String.format("""
          %s: .ascii "%s"
          """, alloc.name(), alloc.initializer()));
    } else {
      throw new AssemblyBuilderException("Typen støttes ikke av skriv");
    }
  }

  private void handleCall(Call call) {
    if (call.fn().equals("skriv")) {
      handlePrint(call);
    } else {
      throw new AssemblyBuilderException("Ukjent kalltype: " + call.fn());
    }
  }

  void handlePrint(Call call) {
    if (call.args().size() != 1) {
      throw new AssemblyBuilderException("Utskrift støtter bare ett argument");
    }
    String variableName = call.args().getFirst();
    StringVar var = this.stringVariables.get(variableName);
    int stringLength = var.value().length();

    if (var.mutable()) {
      this.text.append(String.format("""
        mov rsi, [rip + %s]
        mov rax, 1
        mov rdi, 1
        mov rdx, %d
        syscall
        
        """, variableName, stringLength));
    } else {
      this.text.append(String.format("""
        mov rax, 1
        mov rdi, 1
        lea rsi, %s
        mov rdx, %d
        syscall
        
        """, variableName, stringLength));
    }
  }
}
