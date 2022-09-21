/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   QuadrilateralContainer.cpp
 * Author: mariovilar
 * 
 * Created on March 4, 2021, 11:27 PM
 */

#include "Quadrilateral.h"
#include "QuadrilateralContainer.h"
#include <stdexcept>
#include <vector>
#include <iostream>
using namespace std;

QuadrilateralContainer::QuadrilateralContainer()
{
    cout << "Constructor de QuadrilateralContainer" << endl;
    v = vector<Quadrilateral*>();
    count = 0, area = 0;
}

QuadrilateralContainer::~QuadrilateralContainer()
{
    cout << "Destructor de QuadrilateralContainer" << endl;
}

void QuadrilateralContainer::addQuadrilateral(Quadrilateral* q)
{
    if(count < 10)
    {
        v.push_back(q);
        // Denotem l'àrea des d'aquí, ja que és molt més simple
        area += q->getArea();
        count++;
    }
    else
        throw invalid_argument("Hi ha més de 10 figures");
}

int QuadrilateralContainer::getAreas()
{
    return area;
}