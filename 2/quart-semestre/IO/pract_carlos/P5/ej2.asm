.define
	num 5
	clave 55h
.data 00h
	mat1: db 1, 2, 3, 4, 5
	mat2: db 6, 7, 8, 9, 0
	mat3: db 0, 0, 0, 0, 0
.org 100h
	lxi H, mat1 ; cargamos dirección del primer elemento de mat1 en HL
	lxi B, mat3 ; hacemos lo propio con mat3 en BC
	mvi D, num ; cargamos el contenido de num en el registro D
carga:
	mov A, M ; cargamos en A el contenido de la dirección de memoria contenida en HL
	stax B ; almacena el contenido de A en la dirección de memoria contenida en 
	inx H ; HL <= HL + 1
	inx B ; BC <= BC + 1
	dcr D ; DE <= DE - 1
	jp carga ; si A < 0  el programa continúa, si no va a la etiqueta carga
	lxi H, mat2 ; cargamos dirección del primer elemento de mat2 en HL
	lxi B, mat3 ; hacemos lo propio con mat3
	mvi D, num ; cargamos el contenido de num en el registro D
loop:
	mov E, M ; cargamos en E el contenido de la dirección de memoria contenida en HL
	ldax B ; cargamos en A el contenido de la posición de memoria contenida en BC
	add E ; A <= A + E
	stax B ; guarda el contenido de A en la posición de memoria almacenada en BC
	inx H ; HL <= HL + 1
	inx B	; BC <= BC + 1
	dcr D ; DE <= DE - 1
	jp loop ; si A < 0  el programa continúa, si no va a la etiqueta loop
	;call sub_codificadora ; push del contenido del PC en la pila y el Pc toma el valor sub_codificadora
hlt ; parada del procesador

.org 150h
sub_codificadora:
	push H ; push en la pila de los contenidos de HL
	push B ; igual con BC
	push D ; igual con DE
	push PSW ; push en la pila de AF
	mvi B, clave ; carga el contenido de clave en el registro B
codifica:
	mov A, M ; cargamos en A el contenido de la dirección de memoria contenida en HL
	xri clave ; A <= [clave] XOR A
	mov M, A ; cargamos en A el contenido de la dirección de memoria contenida en HL
	inx H ; HL <= HL + 1
	dcr D ; DE <= DE - 1
	jp codifica ; si A < 0  el programa continúa, si no va a la etiqueta codifica
	pop PSW ; pop en la pila que se almacena en AF
	pop D ; pop en la pila que se almacena en DE
	pop B ; pop en la pila que se almacena en BC
	pop H ; pop en la pila que se almacena en HL
ret