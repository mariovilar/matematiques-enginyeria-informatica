.data
datos: .word 9, 3, 4, 1
resultado: .word 0, 0
.text
main:
	la a0, datos
	lw a1, 0(a0)
	lw a2, 4(a0)
	lw a3, 8(a0)
	lw a4, 12(a0)
loop:
	add a5, a1, a2
	add a6, a4, a2
	addi a3, a3, -1
	bgez a3, loop
	sw a5, 16(a0)
	sw a6, 20(a0)
end:
	nop
