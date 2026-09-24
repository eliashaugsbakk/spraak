.intel_syntax noprefix
.global print_u64

.text

#; ==============================================================================
#; Function: print_u64
#; Description: Prints an unsigned 64-bit integer to stdout using a stack buffer.
#; ------------------------------------------------------------------------------
#; Register Usage:
#;   rdi - input number (unsigned u64)
#;   rax - working quotient (dividend)
#;   rdx - modulo remainder
#;   rsi - buffer pointer
#;   rcx - divisor (10)
#;   r8  - digit counter (number of written digits)
#; ==============================================================================
print_u64:
    #; --- Setup Stack Buffer & Constants ---
    sub rsp, 16             #; Allocate 16-byte buffer on the stack
    mov rax, rdi            #; Load input number into working quotient register
    mov rcx, 10             #; Divisor = 10
    xor r8, r8              #; Clear digit counter (r8 = 0)
    lea rsi, [rsp + 16]     #; Start buffer pointer at top of stack space

.L_u64_convert:
    #; --- Digit Conversion Loop ---
    xor rdx, rdx            #; Clear rdx for div (uses rdx:rax pair)
    div rcx                 #; rax = rax / 10, rdx = rax % 10

    add rdx, '0'            #; Convert remainder (0-9) to ASCII digit
    dec rsi                 #; Move buffer pointer 1 byte left
    mov byte ptr [rsi], dl  #; Store ASCII character in memory

    inc r8                  #; Increment digit counter
    
    test rax, rax           #; Check if quotient reached 0
    jnz .L_u64_convert      #; If rax != 0, process next digit

.L_u64_write:
    #; --- Syscall Phase ---
    mov rax, 1              #; sys_write
    mov rdi, 1              #; file descriptor: stdout
    #; rsi already points to the start of the string
    mov rdx, r8             #; length of string in bytes
    syscall

    #; --- Cleanup & Return ---
    add rsp, 16             #; Deallocate stack buffer
    ret                     #; Return to caller
