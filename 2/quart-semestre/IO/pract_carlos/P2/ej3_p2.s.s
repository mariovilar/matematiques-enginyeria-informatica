.data
datos: .word  4, 5
.text
main:
	la a1, datos
	lw a2, 0(a1)
	lw a3, 4(a1)
	beq a2, a3, op_eq
	blt a2, a3, op_lt
	bgt a2, a3, op_gt
op_eq: 
	mul a0, a1, a3
	j end_op
op_lt: 
	sub a0, a2, a3
	j end_op
op_gt: 
	add a0, a1, a3
end_op:
	sw a0, 36(a1)
