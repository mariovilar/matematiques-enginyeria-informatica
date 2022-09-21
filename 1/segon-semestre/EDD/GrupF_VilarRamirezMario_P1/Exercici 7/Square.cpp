/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Square.cpp
 * Author: mariovilar
 * 
 * Created on March 4, 2021, 11:32 PM
 */

#include "Quadrilateral.h"
#include "Square.h"
#include <iostream>
using namespace std;

Square::Square(int c)
{
    setCostat(c);
    cout << "Constructor de Square" << endl;
}

Square::Square()
{
    cout << "Constructor de Square" << endl;
}

Square::~Square()
{
    cout << "Destructor de Square" << endl;
}

void Square::setCostat(int c)
{
    if(c <= 0)
        throw invalid_argument("Base 0 o negativa");
    else
        this->base = c;
}

int Square::getArea()
{
    return (this->base * this->base);
}

int Square::getPerimetre()
{
    return 4 * this->base;
}

void Square::print()
{
    cout << "ÀREA: " << (this->base * this->base) << endl;
    cout << "PERÍMETRE: " << (4 * this->base) << endl;
}

