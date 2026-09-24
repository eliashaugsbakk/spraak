package no.eliashaugsbakk.kompilator.asmGeneration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Alloc;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Assign;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Call;
import no.eliashaugsbakk.kompilator.IRGeneration.Instructions.Instruction;
import no.eliashaugsbakk.kompilator.parsing.DataTypes;

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
      bss.append(String.format("%s: .skip 8%n", alloc.name()));
      return;
    }

    DataTypes type = DataTypes.fromName(alloc.type())
        .orElseThrow(() -> new AssemblyBuilderException("Typen støttes ikke: " + alloc.type()));

    if (type == DataTypes.STRENG) {
      handleStringAlloc(alloc);
      return;
    }

    data.append(String.format("%s: %s %s%n", alloc.name(), directiveFor(type.storageBytes()),
        alloc.initializer()));
  }

  private void handleAllocRO(Alloc alloc) {
    if (DataTypes.fromName(alloc.type()).isEmpty()) {
      throw new AssemblyBuilderException("Typen støttes ikke som konstant: " + alloc.type());
    }
    emitString(alloc.name(), alloc.initializer());
  }

  private void handleStringAlloc(Alloc alloc) {
    String pointer = alloc.name() + "_ptr";
    emitString(pointer, alloc.initializer());
    data.append(String.format("%s: .quad %s%n", alloc.name(), pointer));
  }

  private void emitString(String label, String value) {
    rodata.append(String.format("%s: .ascii \"%s\"%n", label, value));
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

  private String directiveFor(int sizeBytes) {
    return switch (sizeBytes) {
      case 1 -> ".byte";
      case 2 -> ".word";
      case 4 -> ".long";
      case 8 -> ".quad";
      default -> throw new AssemblyBuilderException("Ugyldig størrelse: " + sizeBytes);
    };
  }
}
