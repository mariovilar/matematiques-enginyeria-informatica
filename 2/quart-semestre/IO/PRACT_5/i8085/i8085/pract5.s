//
//  pract5.s
//  i8085
//
//  Created by Mario Vilar on 15/5/22.
//

.data 50h
mat1: db 1,2,3,4,5
mat2: db 6,7,8,9,0
mat3: db 0,0,0,0,0
.org 100h
lxi d,mat1
lxi h,mat2
lxi b,mat3
loop:
ldax d
add m
stax b
inr c
inr e
inr l
mvi a,mat2
cmp e
jnz loop
hlt
