/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Square.h
 * Author: mariovilar
 *
 * Created on February 28, 2021, 7:32 PM
 */

#ifndef SQUARE_H
#define SQUARE_H
#include <iostream>
using namespace std;
/*
 * Per simplicitat, demanarem una base entera, així que l'àrea serà entera.
 */

class Square 
{
    public:
        Square(int c);
        void setBase(int c);
        int getArea();
        int getPerimetre();
        void print();

    private:
        int costat;
};

#endif /* SQUARE_H */

