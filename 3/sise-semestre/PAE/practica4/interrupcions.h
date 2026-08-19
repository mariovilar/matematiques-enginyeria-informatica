#ifndef INTERRUPCIONS_H_
#define INTERRUPCIONS_H_

// Definim les funcions necessàries per a les interrupcions
void init_interr_robot(void);
void init_interr_emulador(void);
void init_interrupciones();
void EUSCIA0_IRQHandler(void);
void EUSCIA2_IRQHandler(void);

#endif /* INTERRUPCIONS_H_ */
