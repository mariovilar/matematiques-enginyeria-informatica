.data
xx: .word -8
yy: .word -9

.text
main:
lw a0, xx
lw a1, yy 
la a2, xx

beq a0, a1, mult
bgtz a0, pos
bltz a0, negat

pos: 
bgtz a1, resta
j suma
negat:
bltz a1, assignacio
j suma
mult:
mul a2, a0, a1
j end
suma:
bgt a0, a1, suma01
j suma02
suma01:
add a2, a0, a1
j end
suma02:
add a2, a1, a0
j end
resta:
bgt a0, a1, resta01
j resta02
resta01:
sub a2, a0, a1
j end
resta02:
sub a2, a1, a0
j end
assignacio:
neg a0, a0
neg a1, a1
sw a0, 0(a2)
sw a1, 4(a2)
j end

end: nop