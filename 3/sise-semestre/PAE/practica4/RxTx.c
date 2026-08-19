#include <msp432p401r.h>
#include <stdio.h>
#include <stdint.h>
#include <stdbool.h>

#include "lib_PAE.h"
#include "moviments.h"
#include "RxTx.h"
#include "timer.h"
#include "interrupcions.h"

// Configurem la UART per l'emulador. Utilitzem ID0
void init_UART_emulador(void){
    UCA0CTLW0 |= UCSWRST;
    UCA0CTLW0 |= UCSSEL__SMCLK;

    UCA0MCTLW = UCOS16;
    UCA0BRW = 13;
    UCA0MCTLW |= (0x25 << 8);


    // Configurem els pins de la UART.
    // El port P3.2 correspon a UART2RX i el port P3.3 correspon a UART2TX
    P1SEL0 |= (BIT2 | BIT3);
    P1SEL1 &= ~(BIT2 | BIT3);
    UCA0CTLW0 &= ~UCSWRST;

    EUSCI_A0->IFG &= ~EUSCI_A_IFG_RXIFG;
    EUSCI_A0->IE |= EUSCI_A_IE_RXIE;

    //El pin 0 del port 3 indica la direcció de data 1 per TX i 0 per RX
    P3SEL0 &= ~BIT0;
    P3SEL1 &= ~BIT0;
    P3DIR |= BIT0;

    // Recepció de dades
    Sentit_Dades_Rx();
}


// Configurem la UART per l'emulador. Utilitzem ID2
void init_UART(void){
    UCA2CTLW0 |= UCSWRST;
    UCA2CTLW0 |= UCSSEL__SMCLK;

    UCA2MCTLW = UCOS16;
    UCA2BRW = 3;

    // Configurem els pins de la UART.
    // El port P3.2 correspon a UART2RX i el port P3.3 correspon a UART2TX
    P3SEL0 |= (BIT2 | BIT3);
    P3SEL1 &= ~(BIT2 | BIT3);
    UCA2CTLW0 &= ~UCSWRST;

    EUSCI_A2->IFG &= ~EUSCI_A_IFG_RXIFG;
    EUSCI_A2->IE |= EUSCI_A_IE_RXIE;


    //El pin 0 del port 3 indica la direcció de data 1 per TX i 0 per RX
    P3SEL0 &= ~BIT0;
    P3SEL1 &= ~BIT0;
    P3DIR |= BIT0;

    // Recepció de dades
    Sentit_Dades_Rx();
}

// Configurem el packet que envia el robot
byte TxPacket(byte bID, byte bParameterLength, byte bInstruction, byte Parametros[16])
{
    byte bCount,bCheckSum,bPacketLength;
    byte TxBuffer[32];

    Sentit_Dades_Tx();

    TxBuffer[0] = 0xff;
    TxBuffer[1] = 0xff;
    TxBuffer[2] = bID;
    TxBuffer[3] = bParameterLength+2;
    TxBuffer[4] = bInstruction;

    char error[] = "adr. no permitida";

    // Si es vol escriure una direcció <= 0x05 obtenim un error
    if ((Parametros[0] < 6) && (bInstruction == 3)){
        halLcdPrintLine(error, 8, INVERT_TEXT);
        return 0;
    }

    for(bCount = 0; bCount < bParameterLength; bCount++) {
        TxBuffer[bCount+5] = Parametros[bCount];
    }

    bCheckSum = 0;
    bPacketLength = bParameterLength+4+2;

    for(bCount = 2; bCount < bPacketLength-1; bCount++) {
        bCheckSum += TxBuffer[bCount];
    }

    TxBuffer[bCount] = ~bCheckSum;

    for (bCount = 0; bCount < bPacketLength; bCount++) //Aquest bucle és el que envia la trama al Mòdul Robot
    {
        TxUACx(TxBuffer[bCount]);
    }

    while( (UCA2STATW & UCBUSY) ); // waiting
    Sentit_Dades_Rx();

    return(bPacketLength);
}

// Configurem el packet que rep el motor
RxReturn RxPacket(void)
{
    RxReturn respuesta;
    byte bCount, bLength, bCheckSum;
    byte Rx_time_out=0;
    respuesta.TimeOut = Rx_time_out;

    UCA2IE |= UCRXIE;

    Sentit_Dades_Rx();

    for(bCount = 0; bCount < 4; bCount++)
    {
        Reset_Timeout();
        Byte_Recibido=0;

        while (!Byte_Recibido)
        {
            Rx_time_out=TimeOut(1000);

            if (Rx_time_out) break;
        }

        if (Rx_time_out){
            respuesta.TimeOut = 1;
            break;
        }

        respuesta.StatusPacket[bCount] = DatoLeido_UART;
    }


    // Continua llegint la resta de bytes del Status Packet si no ha saltat el Timeout
    if (!Rx_time_out)
    {
        bLength = respuesta.StatusPacket[3] + 4;

        for(bCount = 4; bCount < bLength; bCount++){
            Reset_Timeout();
            Byte_Recibido=0;

            while(!Byte_Recibido){
                Rx_time_out=TimeOut(1000);

                if(Rx_time_out) break;
            }

            if(Rx_time_out){
                respuesta.TimeOut = 1;
                break;
            }

            respuesta.StatusPacket[bCount] = DatoLeido_UART;
        }
    }

    // Comprovem que les dades s'hagin llegit correctament
    bCheckSum = 0;

    if(!Rx_time_out){

        for(bCount = 2; bCount < bLength - 1; bCount++){
            bCheckSum += respuesta.StatusPacket[bCount];
        }
        bCheckSum = ~bCheckSum;
        if (bCheckSum != respuesta.StatusPacket[bLength - 1])
        {
            respuesta.checkCheckSum = 1;
        } else {
            respuesta.checkCheckSum = 0;
        }
    }

    return respuesta;
}

void TxUACx(byte bTxdData)
{
    while (!(UCA2IFG & UCTXIFG)); // Espera a que estigui preparat el buffer de transmissió
    UCA2TXBUF = bTxdData; //escrivim el byte a ser enviat

}


// Funcions per canviar el sentit de les comunicacions
void Sentit_Dades_Rx(void){
    P3OUT &= ~BIT0;
}

void Sentit_Dades_Tx(void) {
    P3OUT |= BIT0;
}
