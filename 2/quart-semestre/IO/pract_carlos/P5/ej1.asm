.define
	num 5h
.data 00h
	mat1: db 1, 2, 3, 4, 5
	mat2: db 6, 7, 8, 9, 0
.org 100h
	lxi H, mat1
	mvi B, mat2
	mvi B, 0
	mvi D, num
loop:
	mov E, M	
	ldax B
	add E
	stax B
	inx H
	inx B
	dcr D	
	jp loop
hlt