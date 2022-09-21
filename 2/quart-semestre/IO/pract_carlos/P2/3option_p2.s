.data
datos: .word  4, 5, 6, 10, 13
resultado: .word 0, 0
.text
main:
	la a1, datos
	lw a2, 0(a1)
	lw a3, 0(a1)
	beq a2, a3, op_eq
	blt a2, a3, op_lt
	bgt a2, a3, op_gt
	fin_op:
op_eq: # a0 <= a1 * a2
	mul a0, a1, a2
	j fin_op
op_lt: # a0 <= a2 - a1
	sub a0, a2, a1
	j fin_op
op_gt: # a0 <= a1 + a2
	add a0, a1, a2
	j fin_op
end:
	nop
