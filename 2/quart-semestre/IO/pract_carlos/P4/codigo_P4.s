.data
valorDada: .word 2
guardaResultat: .word 0
.text
main:
la a0, valorDada
lw a7, 0(a0)
addi a2, zero, 9
add a3, zero, zero
loop:
add a3, a2, a3
addi a7, a7, -1
bgt a7, zero, loop
la a0, guardaResultat
sw a3, 0(a0)
