/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Patient.cpp
 * Author: mariovilar
 * 
 * Created on March 22, 2021, 6:20 PM
 */

#include "Patient.h"
#include <string>
#include <iostream>
using namespace std;

Patient::Patient(string nom, string cognom, string id, string state)
{
    setNom(nom);
    setCognom(cognom);
    setId(id);
    setEstat(state);
}

Patient::~Patient(){}


void Patient::setEstat(string state)
{
    if((state.compare("OK"))==0 || (state.compare("NOT_OK"))==0)
        this->estat = state;
    else
        throw invalid_argument("Estat no vàlid (OK o NOT_OK)");
}

string Patient::getEstat()const
{
    return this->estat;
}

void Patient::setNom(string nom)
{
    this->nom = nom;
}

string Patient::getNom()const
{
    return this->nom;
}

void Patient::setCognom(string cognom)
{
    this->cognom = cognom;
}

string Patient::getCognom()const
{
    return this->cognom;
}

void Patient::setId(string id)
{
    if(id.size() >= 6)
        this->id = id;
    else
        throw invalid_argument("ID massa curt");
}

string Patient::getId()const
{
    return this->id;
}

string Patient::print()
{
    return(nom + ", " + cognom + ", " + id + ", " + estat);
}