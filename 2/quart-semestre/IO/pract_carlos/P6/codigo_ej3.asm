.define
	out1  44h		; se definen los datos outi, los cuales representan
	out2  3eh		; el output requerido para que el display muestre
	out3  6eh		; el n�mero i
	out4  4dh
	out5  ebh
.org 00h			; Comenzamos el programa inicializando el valor del SP y 
				; luego entramos en el bucle principal de nuestro programa
				; en la etiqueta loop.
	LXI H, pila		; HL <= pila
	SPHL			; SP <= HL
	JMP loop

.org 24h			; cada vez que se produce una interrupci�n se salta 
				; a este punto del programa y a su vez se salta a la 
				; subrutina selec, cuyo funcionamiento est� explicado 
				; m�s adelante. Al volver de la rutina tambi�n se vuelve
				; a donde se estaba antes de la interrupci�n (el bucle loop).
	CALL selec
	RET		
.data 200h
pila:
.org 250h
loop:				; Bucle infinito en el que est� el programa a la espera de 
				; una interrupci�n
	JMP loop
selec:			; La subrutina selec empieza viendo qu� n�mero se est� 
				; introduciendo por teclado y en funci�n de cu�l es llama
				; a una de las subrutinas numx o clear (donde x va de 1 a 5)
				; para que el n�mero solicitado se muestre por pantalla.
	PUSH PSW
num1:				; en cada parte numi se comprueba si el puerto in 04h
				; es el n�mero indicado (o la c en el �ltimo caso), en cuyo
				; caso se introduce el n�mero correspondiente en el acumulador
				; y se salta a la etiqueta mostrar en la que el contenido	
				; del acumulador se pone en el puerto de salida para que
				; se muestre el n�mero indicado. En caso de que el n�mero del
				; puerto de entrada no sea el indicado se salta a la etiqueta
				; end en la que se sale de la subrutina selec
	IN 04h
	SBI 31h
	JNZ num2
	MVI A, out1
	JMP  mostrar
num2:
	IN 04h
	SBI 32h
	JNZ num3
	MVI A, out2
	JMP mostrar
num3:
	IN 04h
	SBI 33h
	JNZ num4
	MVI A, out3
	JMP mostrar
num4:
	IN 04h
	SBI 34h
	JNZ num5
	MVI A, out4
	JMP mostrar
num5:
	IN 04h
	SBI 35h
	JNZ clear
	MVI A, out5
	JMP mostrar
clear:
	IN 04h
	SBI 43h		; la tecla c se corresponde con 43h en ASCII
	JNZ end
	MVI A, 00h
	JMP mostrar
mostrar:	
	OUT 07h
end:
	POP PSW
	RET


