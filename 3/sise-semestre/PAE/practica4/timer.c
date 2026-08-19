#include <msp432p401r.h>
#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>

#include "lib_PAE.h"
#include "moviments.h"
#include "RxTx.h"
#include "timer.h"
#include "interrupcions.h"

// Funcio per inicialitzar els timers
void init_timers(void) {
    TIMER_A1->CTL = TIMER_A_CTL_ID__1 | TIMER_A_CTL_SSEL__ACLK | TIMER_A_CTL_CLR
            | TIMER_A_CTL_MC__UP;
    TIMER_A1->CCR[0] = 2399;

    TIMER_A1->CCTL[0] |= TIMER_A_CCTLN_CCIE;
}

// Reset del timeout
void Reset_Timeout(void) {
    counter = 0;
}

// Veiem que hem exedit el timeout
int TimeOut(int t) {
    // Mirem si hem exedit el temps límit
    if(counter > t){
        return 1;
    }
    return 0;
}

// Interrupció que controla la variable timer segons el clock definit
void TA1_0_IRQHandler(void) {
    TA1CCTL0 &= ~TIMER_A_CCTLN_CCIFG;

    // Augmentem el counter
    counter++;
}
