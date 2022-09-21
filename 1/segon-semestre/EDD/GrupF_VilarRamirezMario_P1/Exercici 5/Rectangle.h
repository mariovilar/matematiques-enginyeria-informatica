/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Rectangle.h
 * Author: mariovilar
 *
 * Created on February 28, 2021, 4:01 PM
 */

#ifndef RECTANGLE_H
#define RECTANGLE_H

class Rectangle {
public:
    Rectangle(int b, int h);
    void setBase(int b);
    void setAltura(int h);
    int getArea();
    int getPerimetre();
    void print();

private:
    int base;
    int altura;
};

#endif /* RECTANGLE_H */
