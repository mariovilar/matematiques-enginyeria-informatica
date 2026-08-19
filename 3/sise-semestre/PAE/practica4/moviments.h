#ifndef MOVIMENTS_H_
#define MOVIMENTS_H_

// Definim el byte i alguns nombres importants
typedef uint8_t byte;

#define CW_ANGLE_LIMIT_L 0x06
#define CW_ANGLE_LIMIT_R 0x07
#define CCW_ANGLE_LIMIT_L 0x08
#define CCW_ANGLE_LIMIT_R 0x09

// Definim les funcions d'aquesta llibreria
void set_up_velocity(void);
void endavant(int);
void enrere(int);
void gir_esquerra(int, int);
void gir_dreta(int, int);
void left_led(void);
void right_led(void);

#endif /* MOVIMENTS_H_ */
