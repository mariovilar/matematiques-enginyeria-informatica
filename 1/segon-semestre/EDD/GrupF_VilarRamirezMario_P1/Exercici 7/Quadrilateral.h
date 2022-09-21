/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Quadrilateral.h
 * Author: mariovilar
 *
 * Created on March 4, 2021, 11:32 PM
 */

#ifndef QUADRILATERAL_H
#define QUADRILATERAL_H
#include <vector>

class Quadrilateral
{
    public:
        Quadrilateral();
        virtual ~Quadrilateral();
        
        // Polimorfisme
        virtual int getArea() = 0;
        virtual int getPerimetre() = 0;
        virtual void print() = 0;
        
    protected:
        int base;
        int altura;
};

#endif /* QUADRILATERAL_H */

