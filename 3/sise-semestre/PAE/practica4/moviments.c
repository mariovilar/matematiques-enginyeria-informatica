#include <msp432p401r.h>
#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>

// Cridem a totes les llibreries que hem creat. Cadascuna fa una funció diferent
#include "lib_PAE.h"
#include "moviments.h"
#include "RxTx.h"
#include "timer.h"
#include "interrupcions.h"

// Set_up_velocity s'encarrega de fer que el moviment de les rodes sigui constant
void set_up_velocity(void){
    // Definim els valors que hi haurà a l'array RxParam, és el que li enviem des del robot
    byte RxParam[16];
    RxParam[0] = CW_ANGLE_LIMIT_L;
    RxParam[1] = 0x00;
    RxParam[2] = 0x00;
    RxParam[3] = 0x00;
    RxParam[4] = 0x00;

    RxReturn respuesta;
    byte bPacket;

    // Mentre el checkSum sigui correcte llegim les dades
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x02, 0x05, 0x03, RxParam);
        respuesta = RxPacket();
    }
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x03, 0x05, 0x03, RxParam);
        respuesta = RxPacket();
    }

}

// Funció per fer que el robot es mogui cap endavant
void endavant(int speed){
    RxReturn respuesta;
    byte bPacket;

    // La direcció del motor2 serà 1
    byte direction = 1;

    // Definim els valor de RxParam
    byte RxParam1[16];
    RxParam1[0] = 0x20;

    // Utilitzem el valor speed desitjat
    RxParam1[1] = speed & 0xFF;
    RxParam1[2] = ((direction<<2)) | ((speed>>8)&0x03);

    bPacket = TxPacket(0x02, 0x03, 0x03, RxParam1);

    // Mentre el checkSum sigui correcte llegim les dades
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x02, 0x03, 0x03, RxParam1);
        respuesta = RxPacket();
    }

    // La direcció del motor3 serà 0
    direction = 0;
    RxParam1[2] = ((direction<<2)&4) | ((speed>>8)&0x03);

    // Mentre el checkSum sigui correcte llegim les dades
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x03, 0x03, 0x03, RxParam1);
        respuesta = RxPacket();
    }
}

// Funció per fer que el robot es mogui cap enrere
void enrere(int speed){
    RxReturn respuesta;
    byte bPacket;

    // La direcció del motor2 serà 0
    byte direction = 0;

    // Definim els valor de RxParam
    byte RxParam1[16];
    RxParam1[0] = 0x20;

    // Utilitzem el valor speed desitjat
    RxParam1[1] = speed & 0xFF;
    RxParam1[2] = ((direction<<2)) | ((speed>>8)&0x03);

    bPacket = TxPacket(0x02, 0x03, 0x03, RxParam1);

    // Mentre el checkSum sigui correcte llegim les dades
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x02, 0x03, 0x03, RxParam1);
        respuesta = RxPacket();
    }

    // La direcció del motor3 serà 1
    direction = 1;
    RxParam1[2] = ((direction<<2)&4) | ((speed>>8)&0x03);

    // Mentre el checkSum sigui correcte llegim les dades
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x03, 0x03, 0x03, RxParam1);
        respuesta = RxPacket();
    }
}

// Funció per fer que giri a l'esquerra. Hi ha dos velocitats, una per a cada roda. D'aquesta forma pot no girar simètricament
void gir_esquerra(int speed1, int speed2){
    RxReturn respuesta;
    byte bPacket;

    // La direcció del motor2 serà 0
    byte direction = 0;

    // Definim els valor de RxParam
    byte RxParam1[16];
    RxParam1[0] = 0x20;

    // La roda d'aquest motor girarà a la velocitat speed1
    RxParam1[1] = speed1 & 0xFF;
    RxParam1[2] = ((direction<<2)) | ((speed1>>8)&0x03);

    bPacket = TxPacket(0x02, 0x03, 0x03, RxParam1);

    // Executem mentre el checkSum sigui correcte
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x02, 0x03, 0x03, RxParam1);
        respuesta = RxPacket();
    }

    // La direcció del motor3 serà 0
    direction = 0;

    // La roda d'aquest motor girarà a la velocitat speed2
    RxParam1[1] = speed2 & 0xFF;
    RxParam1[2] = ((direction<<2)&4) | ((speed2>>8)&0x03);

    // Executem mentre el checkSum sigui correcte
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x03, 0x03, 0x03, RxParam1);
        respuesta = RxPacket();
    }
}

// Funció per fer que giri a la dreta. Hi ha dos velocitats, una per a cada roda. D'aquesta forma pot no girar simètricament
void gir_dreta(int speed1, int speed2){
    RxReturn respuesta;
    byte bPacket;

    // La direcció del motor2 serà 1
    byte direction = 1;

    // Definim els valor de RxParam
    byte RxParam1[16];
    RxParam1[0] = 0x20;

    // La roda d'aquest motor girarà a la velocitat speed1
    RxParam1[1] = speed1 & 0xFF;
    RxParam1[2] = ((direction<<2)) | ((speed1>>8)&0x03);

    bPacket = TxPacket(0x02, 0x03, 0x03, RxParam1);

    // Executem el bucle mentre el check sum sigui correcte
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x02, 0x03, 0x03, RxParam1);
        respuesta = RxPacket();
    }

    // La direcció del motor3 serà 1
    direction = 1;

    // La roda d'aquest motor girarà a la velocitat speed2
    RxParam1[1] = speed2 & 0xFF;
    RxParam1[2] = ((direction<<2)&4) | ((speed2>>8)&0x03);

    // Executem el bucle mentre el check sum sigui correcte
    respuesta.checkCheckSum = 1;
    while(respuesta.checkCheckSum){
        // Enviem la instrucció
        bPacket = TxPacket(0x03, 0x03, 0x03, RxParam1);
        respuesta = RxPacket();
    }
}

// Funció per encendre el LED del motor esquerra
void left_led(void) {
    // Configurem la instrucció
    byte bID = 0x02;
    byte bParameterLength = 0x02;
    byte bInstruction = 0x03;
    byte Parametros[16];
    Parametros[0] = 0x19;
    Parametros[1] = 0x01;

    // Envíem la instrucció
    TxPacket(bID, bParameterLength, bInstruction, Parametros);

    RxReturn ret = RxPacket();
    UCA0IE |= UCRXIE;
    Sentit_Dades_Rx();
}

// Funció per encendre el LED del motor dret
void right_led(void) {
    // Configurem la instrucció
    byte bID = 0x03;
    byte bParameterLength = 0x02;
    byte bInstruction = 0x03;
    byte Parametros[16];
    Parametros[0] = 0x19;
    Parametros[1] = 0x01;

    // Envíem la instrucció
    TxPacket(bID, bParameterLength, bInstruction, Parametros);

    RxReturn ret = RxPacket();
    UCA0IE |= UCRXIE;
    Sentit_Dades_Rx();
}
