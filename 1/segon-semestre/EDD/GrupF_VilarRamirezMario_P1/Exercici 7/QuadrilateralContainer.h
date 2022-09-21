/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   QuadrilateralContainer.h
 * Author: mariovilar
 *
 * Created on March 4, 2021, 11:27 PM
 */

#ifndef QUADRILATERALCONTAINER_H
#define QUADRILATERALCONTAINER_H
#include <vector>
using namespace std;

class QuadrilateralContainer
{
    public:
        QuadrilateralContainer();
        void addQuadrilateral(Quadrilateral*);
        ~QuadrilateralContainer();
        
        // Ho deixarem en int perquè tota la resta d'àrees les hem fet en int
        // És a dir, ho fem per coherència
        int getAreas();

    private:
        vector<Quadrilateral*> v;
        // Comptador de figures, si es passa de 10 llançarà un out_of_range
        int count;
        // Ens ajudarà amb el getArea()
        int area;

};

#endif /* QUADRILATERALCONTAINER_H */

