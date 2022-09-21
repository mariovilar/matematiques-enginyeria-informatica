/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Rectangle.cpp
 * Author: mariovilar
 * 
 * Created on February 28, 2021, 4:01 PM
 */

#include "Rectangle.h"
#include<iostream>
using namespace std;

Rectangle::Rectangle(int b, int h)
{
    setBase(b);
    setAltura(h);
}

void Rectangle::setBase(int b)
{
    if(b <= 0)
        throw invalid_argument("Base 0 o negativa");
    else
        base = b;
}

void Rectangle::setAltura(int h)
{
    if(h <= 0)
        throw invalid_argument("Altura 0 o negativa");
    else
        altura = h;
}

int Rectangle::getArea()
{
    return (base * altura);
}

int Rectangle::getPerimetre()
{
    return (2*base + 2*altura);
}

void Rectangle::print()
{
    cout << "ÀREA: " << (base * altura) << endl;
    cout << "PERÍMETRE: " << (2*base + 2*altura) << endl;
}

