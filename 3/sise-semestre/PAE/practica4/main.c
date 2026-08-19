#include <msp432p401r.h>
#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>

// Cridem a totes les llibreries que hem creat. Cadascuna fa una funció diferent
// Moviments guarda els moviments que pot fer el robot
// RxTx guarda la transferencia de dades i la configuració de la UART
// Timer guarda la inicialització del timer
// Interrupcions guarda la gestió de les interrupcions
#include "lib_PAE.h"
#include "moviments.h"
#include "RxTx.h"
#include "timer.h"
#include "interrupcions.h"


// Cridem el main.
void main(void)
{
    WDT_A->CTL = WDT_A_CTL_PW | WDT_A_CTL_HOLD;

    //Inicialitzem les dades com fa falta
    init_ucs_24MHz();

    // Configurem els Timers
    init_timers();

    // Configurem la UART
    init_UART();

    // Configurem les interrupcions
    init_interrupciones();
    __enable_interrupts();

    // Funcions per encendre els LEDS del motor
    left_led();
    right_led();

    // Definim la velocitat a la que es moura el robot
    int speed = 0x100;

    // Funció per fer que el robot es pugui moure
    set_up_velocity();


    // Definim els moviments que volem que el robot faci
    endavant(speed);
    while (1);
}
