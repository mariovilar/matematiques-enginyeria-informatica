.data
xx: .word 0
yy: .word 0b000110000001011
zz: .word 0b000000000010001
.text
main:
lw a0, xx
lw a1, yy
lw a2, zz
loop:
beqz a2,end
add a0, a0, a1
addi a2,a2,-1
j loop
end: nop