.data
etiqueta: .word 1
.text
lw a1, 0(a1)
sw a1, 3(a1)
bne t1, t2, etiqueta
addi t2, t3, 11
sub zero,a2,a3 # Esta instrucción es correcta pero no hace nada
lw a1, 3(a0)




