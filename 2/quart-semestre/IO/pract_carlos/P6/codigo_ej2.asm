.data 100h
pila:
.org 24h		; cada vez que se produce una interrupción se salta a este punto
			; del programa y a su vez se salta a la etiqueta ports, cuyo
			; funcionamiento está explicado en el ejercicio.
 JMP ports		
.org 500h
LXI H, pila		; HL <= pila
SPHL			; SP <= HL
CALL ports
NOP
HLT			; detener procesador
ports:
PUSH PSW		; Push AF
IN 04h		; A <= puerto entrada 04h	
ANI 00000001	; A <= A AND 01h
OUT 05h		; puerto salida 05h <= A
POP PSW		; pop AF
RET
