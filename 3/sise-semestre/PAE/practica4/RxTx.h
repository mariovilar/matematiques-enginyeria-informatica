#ifndef RXTX_H_
#define RXTX_H_

// Variables volatils necessaries per un bon funcionament
volatile byte DatoLeido_UART;
volatile byte Byte_Recibido;

// Struct per crear els packets de dades
typedef struct
{
    // Packet rebut
    byte StatusPacket[16];

    // Indiquem si hem accedit el temps en llegir el packet
    byte TimeOut;

    // Mirem si el CheckSum es correcte
    byte checkCheckSum;
} RxReturn;


// Definim les funcions d'aquesta llibreria
byte TxPacket(byte bID, byte bParameterLength, byte bInstruction, byte Parametros[16]);
void Reset_Timeout(void);
int timeOut(int);
RxReturn RxPacket(void);
void init_UART_emulador(void);
void init_UART(void);
void TxUACx(byte);
void Sentit_Dades_Rx(void);
void Sentit_Dades_Tx(void);


#endif /* RXTX_H_ */
