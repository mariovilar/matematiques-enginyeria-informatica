/*
 * lcd.h
 *
 *  Created on: Jul 12, 2013
 *      Author: RobG
 *  Adaptacion C. Serre, UB, 2015
 */
#ifndef lib_PAE_LCD_H_
#define lib_PAE_LCD_H_

#include <stdint.h>

void init_ucs_16MHz();
void init_ucs_24MHz();

void halLcdInit();
void halLcdClearScreen(uint8_t blackWhite);
void halLcdClearScreenBkg();
void halLcdPrintLine(char String[], uint8_t Line, uint8_t TextStyle);
void halLcdClearLine(uint8_t Line);
void halLcdPrintLineCol(char String[], uint8_t Line, uint8_t Col, uint8_t TextStyle);
void initLCD();
void halLcdDrawPixel(uint16_t x, uint16_t y, uint16_t Color);
void halLcdDrawLine(uint16_t xStart, uint16_t yStart, uint16_t xEnd, uint16_t yEnd, uint16_t Color);
void halLcdDrawRect(uint16_t xStart, uint16_t yStart, uint16_t xEnd, uint16_t yEnd, uint16_t Color);
void halLcdDrawCircle(uint16_t x, uint16_t y, uint8_t radius, uint16_t Color);
void halLcdFillRect(uint16_t xStart, uint16_t yStart, uint16_t xEnd, uint16_t yEnd, uint16_t Color);
void halLcdFillCircle(uint16_t x, uint16_t y, uint8_t radius, uint16_t Color);
void halLcdDrawImageLut(uint16_t x, uint16_t y, uint16_t w, uint16_t h, uint8_t * data, uint32_t * lut, uint16_t numColors);

uint16_t getScreenWidth();
uint16_t getScreenHeight();
void setOrientation(uint8_t orientation);
uint8_t getOrientacion();
void cambiar_orientacion();

//Dimensiones de la LCD
#define LONG_EDGE_PIXELS 128
#define SHORT_EDGE_PIXELS 128
#define LCD_OFFSET_HEIGHT 0
#define LCD_OFFSET_WIDTH 0
#define MARGEN_H 3 //pixeles de margen horizontales al inicio de una linea
#define MARGEN_V 3 //pixeles de margen verticales arriba de la pantalla

// orientacion de la LCD: 0 90 180 270 grados
#define ORIENTATION 2 //orientacion por defecto
#define ORIENTATION_VERTICAL 0
#define ORIENTATION_HORIZONTAL 1
#define ORIENTATION_VERTICAL_ROTATED 2
#define ORIENTATION_HORIZONTAL_ROTATED 3

//Declaro variables extern para que sean globales,
//y asi el usuario podra cambiar fuente y colores:
extern uint8_t Fuente;
extern uint16_t Color_Fondo;
extern uint16_t Color_Texto;
extern uint16_t Color_Fondo_Inv;
extern uint16_t Color_Texto_Inv;

// font sizes
#define FONT_SM		0
#define FONT_MD		1
#define FONT_LG		2
#define FONT_SM_BKG	3
#define FONT_MD_BKG	4
#define FONT_LG_BKG	5
//Medidas pantalla:
//con fuente pequeña: 21 caracteres
//con fuente mediana: 15 "y pico" carateres, es decir se queda en 15.
//con fuente grande:  10 "y pico" caracteres, pero salen bastante mal. fuente desaconsejada.

#define NORMAL_TEXT				1 //BIT0
#define INVERT_TEXT             2 //BIT1

//Colores predefinidos accesibles para el usuario
#define COLOR_16_BLACK   0x0000
#define COLOR_16_BLUE    0x001F
#define COLOR_16_RED     0xF800
#define COLOR_16_GREEN   0x07E0
#define COLOR_16_CYAN    0x07FF
#define COLOR_16_MAGENTA 0xF81F
#define COLOR_16_YELLOW  0xFFE0
#define COLOR_16_WHITE   0xFFFF
#define COLOR_16_ORANGE	0xFD20
#define COLOR_16_ORANGE_RED	0xFA20
#define COLOR_16_DARK_ORANGE	0xFC60
#define COLOR_16_GRAY	0xBDF7
#define COLOR_16_DARK_GRAY	0x7BEF
#define COLOR_16_DARKER_GRAY	0x634C
#define COLOR_16_NAVY	0x0010
#define COLOR_16_ROYAL_BLUE	0x435C
#define COLOR_16_SKY_BLUE	0x867D
#define COLOR_16_TURQUOISE	0x471A
#define COLOR_16_STEEL_BLUE	0x4416
#define COLOR_16_LIGHT_BLUE	0xAEDC
#define COLOR_16_AQUAMARINE	0x7FFA
#define COLOR_16_DARK_GREEN	0x0320
#define COLOR_16_DARK_OLIVE_GREEN	0x5345
#define COLOR_16_SEA_GREEN	0x2C4A
#define COLOR_16_SPRING_GREEN	0x07EF
#define COLOR_16_PALE_GREEN	0x9FD3
#define COLOR_16_GREEN_YELLOW	0xAFE5
#define COLOR_16_LIME_GREEN	0x3666
#define COLOR_16_FOREST_GREEN	0x2444
#define COLOR_16_KHAKI	0xF731
#define COLOR_16_GOLD	0xFEA0
#define COLOR_16_GOLDENROD	0xDD24
#define COLOR_16_SIENNA	0xA285
#define COLOR_16_BEIGE	0xF7BB
#define COLOR_16_TAN	0xD5B1
#define COLOR_16_BROWN	0xA145
#define COLOR_16_CHOCOLATE	0xD343
#define COLOR_16_FIREBRICK	0xB104
#define COLOR_16_HOT_PINK	0xFB56
#define COLOR_16_PINK	0xFE19
#define COLOR_16_DEEP	0xF8B2
#define COLOR_16_VIOLET	0xEC1D
#define COLOR_16_DARK_VIOLET	0x901A
#define COLOR_16_PURPLE	0xA11E
#define COLOR_16_MEDIUM_PURPLE	0x939B
//
//*****************************************************************************
//
//! This structure defines the characteristics of a Bitmap Image
//
//*****************************************************************************
typedef struct Graphics_Image
{
    uint8_t bPP;	             //!< Bits per pixel and Compressed/Uncompressed
    uint16_t xSize;              //!< xSize
    uint16_t ySize;              //!< ySize
    uint16_t numColors;          //!< Number of Colors in Palette
    const uint32_t  * pPalette;  //!< Pointer to Palette
    const uint8_t * pPixel;      //!< Pointer to pixel data;
} Graphics_Image;

#endif /* lib_PAE_LCD_H_ */
