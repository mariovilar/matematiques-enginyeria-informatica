#ifndef TIMER_H_
#define TIMER_H_

// Definim el counter
volatile uint8_t counter;


// Definim les funcions d'aquesta llibreria
void init_timers(void);
void Reset_Timeout(void);
int TimeOut(int t);
void TA1_0_IRQHandler(void);

#endif /* TIMER_H_ */
