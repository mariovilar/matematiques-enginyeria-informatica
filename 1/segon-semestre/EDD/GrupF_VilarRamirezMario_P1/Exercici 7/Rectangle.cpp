/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Rectangle.cpp
 * Author: mariovilar
 * 
 * Created on March 4, 2021, 11:32 PM
 */

#include "Quadrilateral.h"
#include "Rectangle.h"
#include <iostream>
using namespace std;

Rectangle::Rectangle(int b, int h)
{
    this->setBase(b);
    this->setAltura(h);
    cout << "Constructor de Rectangle" << endl;
}

Rectangle::Rectangle()
{
    cout << "Constructor de Rectangle" << endl;
}

Rectangle::~Rectangle()
{
    cout << "Destructor de Rectangle" << endl;
}

void Rectangle::setBase(int b)
{
    if(b <= 0)
        throw invalid_argument("Base 0 o negativa");
    else
        this->base = b;
}

void Rectangle::setAltura(int h)
{
    if(h <= 0)
        throw invalid_argument("Altura 0 o negativa");
    else
        this->altura = h;
}

int Rectangle::getArea()
{
    return (this->base * this->altura);
}

int Rectangle::getPerimetre()
{
    return (2*this->base + 2*this->altura);
}

void Rectangle::print()
{
    cout << "ÀREA: " << (this->base * this->altura) << endl;
    cout << "PERÍMETRE: " << (2*this->base + 2*this->altura) << endl;
}

