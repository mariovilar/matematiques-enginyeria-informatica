.data
dataByte0: .byte 4
databyte1: .byte 55
dataByte2: .byte 235
dataByte3: .byte 31
data1: .word 1
data2: .word 0
data3: .word 4

.text
main:
	lw a0,data1
	lb t0,dataByte0
	sw a0,data2(zero)
	lw a1,data3
loop:
	beq a1,a0,salta
	sub a1,a1,a0
	j loop

salta:
	lw a3,data2

end:
	j end