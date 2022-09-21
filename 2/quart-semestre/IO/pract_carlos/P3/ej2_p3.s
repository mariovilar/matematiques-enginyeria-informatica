.data
comptador: .word 47
resultat: .word 0
.text
	la a0, comptador
	lw a0, 0(a0)
	addi a1, zero, 0
	addi a2, zero, 1
loop:
	blez a0, end
	add a3, a2, a1
	addi a1, a2, 0
	addi a2, a3, 0
	addi a0, a0, -1
	j loop
end:
	la a0, resultat
	sw a1, 0(a0)
