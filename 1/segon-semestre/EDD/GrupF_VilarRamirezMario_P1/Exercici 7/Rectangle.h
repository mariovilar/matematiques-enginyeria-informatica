/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Rectangle.h
 * Author: mariovilar
 *
 * Created on March 4, 2021, 11:32 PM
 */

#ifndef RECTANGLE_H
#define RECTANGLE_H

class Rectangle : public Quadrilateral
{
    public:
        Rectangle(int b, int h);
        Rectangle();
        ~Rectangle();
        
        void setBase(int b);
        void setAltura(int h);
        
        int getArea();
        int getPerimetre();
        void print();
};

#endif /* RECTANGLE_H */

