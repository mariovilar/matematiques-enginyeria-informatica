.data
datos: .word  3083,17
resultado: .word 0, 0
.text
main:
	la a3, datos
	add a0, zero, zero 
	lw a1, 0(a3)
	lw a2, 4(a3)
loop:
	add a0, a0, a1
	addi a2, a2, -1
	bnez a2, loop
end:
	nop
