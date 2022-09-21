.data
xx: .word 3
yy: .word 5
oo: .word 2
zz: .word 8
.text
la a0, xx
sub a1, a1, a1
add a2, zero, zero
loop: addi a7, a1, -4
beqz a7, final
lw a3, 0(a0)
add a2, a3, a3
addi a0, a0, 4
addi a1, a1, 1
j loop
final:sw a2, 4(a0)
