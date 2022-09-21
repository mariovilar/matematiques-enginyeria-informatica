/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Square.h
 * Author: mariovilar
 *
 * Created on March 4, 2021, 11:32 PM
 */

#ifndef SQUARE_H
#define SQUARE_H

class Square : public Quadrilateral
{
    public:
        Square(int c);
        Square();
        ~Square();
        
        void setCostat(int c);
        
        int getArea();
        int getPerimetre();
        void print();
};

#endif /* SQUARE_H */

