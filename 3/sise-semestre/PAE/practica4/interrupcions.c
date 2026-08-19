#include <msp432p401r.h>
#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>

#include "lib_PAE.h"
#include "moviments.h"
#include "RxTx.h"
#include "timer.h"
#include "interrupcions.h"

// Inicialitzem les interrupcions pel robot
void init_interr_robot(void){
    NVIC->ICPR[0] |= 10000000000;
    NVIC->ISER[0] |= 10000000000;

    NVIC->ICPR[0] |= 1 << EUSCIA0_IRQn;
    NVIC->ISER[0] |= 1 << EUSCIA0_IRQn;

    NVIC->ICER[0] |= 1 << EUSCIA2_IRQn;
    NVIC->ISER[0] |= 1 << EUSCIA2_IRQn;


    NVIC->ICPR[1] |= 1 << (PORT3_IRQn & 31);
    NVIC->ISER[1] |= 1 << (PORT3_IRQn & 31);
}

// Inicialitzem les interrupcions per l'elumador
void init_interr_emulador(void){
    NVIC->ICPR[0] |= 1 << TA1_0_IRQn;
    NVIC->ISER[0] |= 1 << TA1_0_IRQn;

    NVIC->ICPR[0] |= 1 << EUSCIA0_IRQn;
    NVIC->ISER[0] |= 1 << EUSCIA0_IRQn;

    NVIC->ICPR[1] |= 1 << (PORT3_IRQn & 31);
    NVIC->ISER[1] |= 1 << (PORT3_IRQn & 31);
}


// Configurem les interrupcions
void init_interrupciones() {
    init_interr_robot();
    //init_interr_emulador();
}


// Recepció de bytes utilizant interrupcions
void EUSCIA0_IRQHandler(void)
{
    EUSCI_A0->IFG &=~ EUSCI_A_IFG_RXIFG;
    UCA0IE &= ~UCRXIE;
    DatoLeido_UART = UCA0RXBUF;
    Byte_Recibido=1;
    UCA0IE |= UCRXIE;
}

// Recepció de bytes utilizant interrupcions
void EUSCIA2_IRQHandler(void)
{
    EUSCI_A2->IFG &=~ EUSCI_A_IFG_RXIFG;
    UCA2IE &= ~UCRXIE;
    DatoLeido_UART = UCA2RXBUF;
    Byte_Recibido=1;
    UCA2IE |= UCRXIE;
}
