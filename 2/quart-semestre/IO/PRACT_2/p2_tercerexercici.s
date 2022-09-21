# DADES
.data
A: .word 4
B: .word 3
Z: .word 0
# PROGRAMA
.text
main:
lw a0, A # primer valor, A
lw a1, B # segon valor, B
lw a2, Z # acumularem el valor de zero
la a3, A # guardara l'adreca de l inici de la seccio .data
# INSTRUCCIONS 
bgt a0,a1,suma
bgt a1,a0,difer
beq a0,a1,mult
# ETIQUETES
suma:
add a2,a0,a1 # suma
j end
difer:
sub a2,a1,a0 # diferencia
j end
mult: 
mul a2,a0,a1 # multiplicacio
j end
end: 
sw a2,24(a3) # es guarda a la posicio desitjada
nop