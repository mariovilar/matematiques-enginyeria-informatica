/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Square.cpp
 * Author: mariovilar
 * 
 * Created on February 28, 2021, 7:32 PM
 */

#include "Square.h"
#include<iostream>
#include <stdexcept>
using namespace std;

Square::Square(int c)
{
    setBase(c);
}

void Square::setBase(int c)
{
    if(c <= 0)
        throw invalid_argument("Costat 0 o negatiu");
    else
        costat = c;
}

int Square::getArea()
{
    return (costat * costat);
}

int Square::getPerimetre()
{
    return 4 * costat;
}

void Square::print()
{
    cout << "ÀREA: " << (costat * costat) << endl;
    cout << "PERÍMETRE: " << (4 * costat) << endl;
}
