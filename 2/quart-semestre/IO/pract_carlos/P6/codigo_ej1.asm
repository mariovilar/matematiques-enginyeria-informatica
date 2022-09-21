.define
num 02h
.data 00h
mat1: db 1,2
mat2: db 3,4
mat3: db 0,0
.data 20h
pila:
.org 600h		; en esta primera parte de código se asigna al SP el valor correspondiente para que apunte a la etiqueta pila 
			; y se cargan mat1 y mat2 en los pares de registros DE y HL y num en el B
LXI H, pila		; HL <= pila
SPHL			; SP <= HL
MVI B, num		; B <= [num]
LXI D, mat1		; DE <= mat1
LXI H, mat2		; HL <= mat2
loop:			; el loop llama a la rutina suma y luego resta 1 al registro B. Si después de restar 1, B es distinto de 0, 
			; entonces vuelve a empezar el loop. Cuando se sale del loop se para el procesador.A
CALL suma		
DCR B			; B <= B - 1
JNZ loop		; si A != 0 entonces saltar a loop
NOP
HLT			; detener el procesador
suma:			; Esta subrutina deja la palabra de estado de programa intacta y momentáneamente alberga el resultado de la suma del contenido de la 
			; dirección almacenada en HL y en DE en A, luego guarda esta suma almacenada en A en la posición de memoria apuntada por los registros
			; DE. Finalmente les suma 1 a los pares de registros HL y DE (este cambio altera los registros también fuera de la subrutina).
PUSH PSW		; push AF
LDAX D		; A <= [DE]
ADD M			; A <= A + [HL]
STAX D		; [DE] <= A
INX H			; HL <= HL + 1
INX D			; DE <= DE + 1
POP PSW		; pop AF
RET 
