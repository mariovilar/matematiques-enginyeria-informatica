.data 
a: .word 5
b: .word -6
resultado: .word 0
.text
	la a0, a
	lw a1, 0(a0)
	lw a2, 4(a0)
cierto:
falso:
fin:
	sw a3, 8(a0)