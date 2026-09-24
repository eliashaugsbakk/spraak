.intel_syntax noprefix
.global print_i64

.text

#; ==============================================================================
#; Function: print_i64
#; Description: Prints a signed 64-bit integer to stdout using a stack buffer.
#; ------------------------------------------------------------------------------
#; Register Usage:
#;   rdi - input number (signed i64)
#;   rax - working quotient (dividend)
#;   rdx - modulo remainder
#;   rsi - buffer pointer
#;   rcx - divisor (10)
#;   r8  - digit counter (number of written digits)
#;   r9  - is negative (1 if negative, 0 if positive)
#; ==============================================================================
print_i64:
    #; --- Setup Stack Buffer & Constants ---
    sub rsp, 16             #; Allocate 16-byte buffer on the stack
    mov rcx, 10             #; Divisor = 10
    xor r8, r8              #; Clear digit counter (r8 = 0)
    xor r9, r9              #; Clear negative flag (r9 = 0)
    lea rsi, [rsp + 16]     #; Start buffer pointer at top of stack space

    #; --- Sign Handling ---
    mov rax, rdi            #; Load input number into working register
    cmp rax, 0              #; Check if input is negative
    jge .L_i64_convert      #; If positive/zero, skip negation

    neg rax                 #; Make rax positive for division
    mov r9, 1               #; Set negative flag

.L_i64_convert:
    xor rdx, rdx            #; Clear rdx for div (uses rdx:rax pair)
    div rcx                 #; rax = rax / 10, rdx = rax % 10

    add rdx, '0'            #; Convert remainder (0-9) to ASCII digit
    dec rsi                 #; Move buffer pointer 1 byte left
    mov byte ptr [rsi], dl  #; Store ASCII character in memory

    inc r8                  #; Increment digit counter

    test rax, rax           #; Check if quotient reached 0
    jnz .L_i64_convert      #; If rax != 0, process next digit

    #; --- Prepend Minus Sign if Needed ---
    cmp r9, 1               #; Was the input number negative?
    jne .L_i64_write        #; If not, skip sign prepending

    dec rsi                 #; Move pointer left for sign byte
    mov byte ptr [rsi], '-' #; Prepend '-' ASCII character
    inc r8                  #; Count the '-' in total byte length

.L_i64_write:
    #; --- Syscall Phase ---
    mov rax, 1              #; sys_write
    mov rdi, 1              #; file descriptor: stdout
    #; rsi already points to the start of the string
    mov rdx, r8             #; length of string in bytes
    syscall

    #; --- Cleanup & Return ---
    add rsp, 16             #; Deallocate stack buffer
    ret                     #; Return to caller
