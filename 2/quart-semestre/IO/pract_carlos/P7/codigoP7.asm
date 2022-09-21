.define
	numbers_count 10
.org 00h	
	mvi B, E0h
	mvi C, 00h
	mvi D, 00h
	jmp loop
.org 24h
	call tecla_in
	ret														
.data 150h
	numbers: db 30h,31h,32h,33h,34h,35h,36h,37h,38h,39h 
	n1_digits: db 00h, 00h, 00h
	n1_sign: db 00h
	n1: db 00h
	n2_digits: db 00h, 00h, 02h
	n2_sign: db 00h
	n2: db 00h
	sol_digits: db 00h, 00h, 00h
	sol_sign: db 00h
	sol: db 00h
	op : db 00h
.org 200h
loop: 
	jmp loop	

tecla_in:														
	mov A, D
	cpi 00h
	jz insert_sign1
	cpi 05h
	jz insert_sign2
	cpi 04h
	jz insert_op
	cpi 09h
	jz insert_equals

insert_num:
	in 04h 
	call check_number
	cpi 00h
	jz error													
	mov E, A
	mov A, D
	cpi 01h
	jz primer_num
	cpi 02h
	jz primer_num
	cpi 03h
	jz primer_num
	sbi 06h			; si el dígito es del segundo número
	lxi H, n2_digits
loop_digito2:
	cpi 00h
	jz end_loop_digito2
	inx H
	dcr A	
	jmp loop_digito2
end_loop_digito2:
	mov A, E	
	stax B
	inx B
	sui 30h
	mov M, A
	jmp end											
primer_num:
	dcr A			; restamos uno al acc para saber qué 
				; dígito se introduce (0, 1 ó 2)
	lxi H, n1_digits 
loop_digito1:
	cpi 00h
	jz end_loop_digito1
	inx H	
	dcr A
	jmp loop_digito1
end_loop_digito1:
	mov A, E	
	stax B
	inx B
	sui 30h
	mov M, A
	jmp end

insert_op:
	in 04h
	cpi 2bh
	jz not_error
	cpi 2dh
	jz not_error
	cpi 7ch
	jz not_error
	cpi 26h
	jz not_error
	jmp error
not_error:
	lxi H, op
	mov M, A
	stax B
	inx B
	jmp end

insert_sign1:
	in 04h
	cpi 2Dh
	jz neg1	
	cpi 2Bh
	jz pos1
	jmp error
pos1:
	lxi H, n1_sign
	mvi M, 00h
	stax B
	inx B
	jmp end
neg1:
	lxi H, n1_sign
	mvi M, 01h
	stax B
	inx B
	jmp end

insert_sign2:
	in 04h
	cpi 2dh
	jz neg2
	cpi 2Bh
	jz pos2
	jmp error
pos2:
	lxi H, n2_sign
	mvi M, 00h
	stax B
	inx B
	jmp end
neg2:
	lxi H, n2_sign
	mvi M, 01h
	stax B
	inx B
	jmp end
	
insert_equals:
	in 04h
	cpi 3dh
	jnz error	
	stax B				; Escribir un igual
	inx B

	; SACAR N1 y N2 a partir de sus dígitos decimales
	lxi H, n1_digits
	call convert_bin
	lxi H, n2_digits
	call convert_bin

	; OPERAR N1 Y N2 Y PONER RESULTADO EN SOL
	lxi H, n1
	mov E, M

	lxi H, op
	mov A, M
	cpi 2bh
	jz op_sum
	cpi 2dh
	jz op_sub
	cpi 26h
	jz op_and
	cpi 7ch
	jz op_or
op_sum:
	lxi H, n2
	mov A, M
	push B
	call is_neg
	mov D, C
	add E	
	push PSW
	call is_neg
	mov B, C
	mov A, E
	call is_neg
	mov A, C
	xra D	
	cpi 01h
	jz is_correct1	; si tienen signo distinto 
				; la operación siempre es correcta
	mov A, C
	cmp B
	jz is_correct1
	jmp pop_2_error
is_correct1:
	pop PSW
	pop B
	jmp end_op

op_sub:
	lxi H, n2
	mov A, M
	cma
	inr A

	push B
	call is_neg
	mov D, C
	add E	
	push PSW
	call is_neg
	mov B, C
	mov A, E
	call is_neg
	mov A, C
	xra D	
	cpi 01h
	jz is_correct2	; si tienen signo distinto 
				; la operación siempre es correcta
	mov A, C
	cmp B
	jz is_correct2
	jmp pop_2_error
is_correct2:
	pop PSW
	pop B
	jmp end_op

op_and:

	lxi H, n2
	mov A, M
	ana E
	jmp end_op
op_or:
	lxi H, n2
	mov A, M
	ora E	
end_op:
	lxi H, sol	
	mov M, A
	call convert_dec

	; ESCRIBIR RESULTADOS
	lxi H, sol_sign			; Escribir signo
	mov A, M	
	cpi 00h
	jz sol_pos
	mvi A, 2dh
	jmp end_sol_pos
sol_pos:
	mvi A, 2bh
end_sol_pos:
	stax B
	inx B
	lxi H, sol_digits			; Escribir dígitos
	mov A, M	
	adi 30h
	stax B
	inx B
	inx H
	mov A, M
	adi 30h
	stax B
	inx B
	inx H
	mov A, M	
	adi 30h
	stax B
	hlt
end:
	inr D
	ret



check_number:			; esta rutina deja A como está si es uno
					; de los caracteres permitidos en la 
					; dirección numbers y lo cambia a 0
					; de lo contrario.
	push D
	push H	
	mvi E, numbers_count	; E <= numbers_count
	lxi H, numbers		; HL <= numbers
allowed_loop:
	mov D, M			; D <= [HL]
	cmp D				; zeroByte <=  D == A
	jz end_allowed		; PC <= end_allowed if zeroByte == 1
	inx H				; HL <= HL + 1
	dcr E				; E <= E - 1
	jnz allowed_loop		; PC <= allowed_loop if zeroByte == 0
not_allowed:
	mvi A, 00h
end_allowed:
	pop H
	pop D	
	ret


mul:					; multiplica B * C y lo suma 
					; a A y lo guarda en A
	push PSW
	mov A, B
	cpi 00h
	jnz end_comp_b_0
	pop PSW
	ret
end_comp_b_0:
	pop PSW
	add C
	push B
	call is_neg
	mov B, A	
	mov A, C	
	cpi 01h
	jz pop_3_error
	mov A, B
	pop B
	dcr B
	jnz mul
	ret

div:				; divide A entre C de manera entera y guarda el resto en A y el result en C
	push H 
	mov H, A
	mvi A, 00h
	mvi L, 00h
div_loop:
	add C
	inr L
	cmp H	
	jz division_exacta
	jc div_loop
	dcr L	
	sub C	
division_exacta:
	mov C, A
	mov A, H
	sub C
	mov C, L
	pop H
	ret


convert_dec: 			; convierte el contenido del acumulador
					; en tres dígitos decimales que guarda 
					; en las posiciones sol_digit, sol_digit + 1,
					; sol_digit + 2. Pone el signo en sol_sign


						
					
	push H
	push B
	call is_neg
	mov D, A
	mov A, C
	cpi 01h
	jz change_sign_to_dec
	mov A, D
	jmp end_change_sign_to_dec
change_sign_to_dec:
	lxi H, sol_sign
	mov M, A
	mov A, D
	cma
	inr A
end_change_sign_to_dec:
	lxi H, sol	
	mov M, A
	lxi H, sol_digits 
	mvi C, 100
	call div	
	mov M, C
	mvi C, 10
	call div
	inx H
	mov M, C
	inx H	
	mov M, A
	pop B
	pop H	
	ret


convert_bin:			; convierte los tres dígitos contenidos en
					; HL, HL + 1, HL + 2 en un byte binario si es 
					; posible. Toma el signo de HL + 3.
					; Este bit binario se almacena en HL + 4
	push PSW
	push H
	push B			; necesario ya que is_neg altera BC
	mvi A, 00h
	mov B, M			
	mvi C, 100
	call mul
	inx H
	mov B, M	
	mvi C, 10
	call mul
	inx H
	mov B, M	
	add B				
	call is_neg			; comprobar carry 

	push PSW
	mov A, C
	cpi 01h
	jz pop_2_error		;;;;;;;;;;;;;;;;; quizá pueda mantener a en B y devolverlo más tarde


	inx H	
	mov A, M
	cpi 01h
	jz change_sign
	pop PSW
	jmp end_change_sign
change_sign:
	pop PSW
	cma
	inr A
end_change_sign:
	inx H
	mov M, A
	pop B
	pop H
	pop PSW	
	ret

		
is_neg:				; comprueba si el primer bit de 
					; A es 1, si lo es pone C = 1 y si
					; no lo es pone C = 0
	push PSW
	mvi C, 00h
	ani 80h
	cpi 80h
	jnz end_is_neg
	mvi C, 01h			
end_is_neg:
	pop PSW
	ret

pop_3_error:
	pop B
pop_2_error:
	pop B
pop_error:	
	pop B
error:
	mvi A, 45h
	stax B
	inx B
	mvi A, 52h
	stax B
	inx B
	stax B
	inx B
	mvi A, 4fh
	stax B
	inx B
	mvi A, 52h
	stax B
	hlt
	









