/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   Patient.h
 * Author: mariovilar
 *
 * Created on March 22, 2021, 6:20 PM
 */

#ifndef PATIENT_H
#define PATIENT_H
#include <string>

class Patient
{
public:
    Patient(std::string nom, std::string cognom, std::string id, std::string estat);
    ~Patient();
    void setEstat(std::string estat);
    std::string getEstat()const;
    void setNom(std::string nom);
    std::string getNom()const;
    void setCognom(std::string cognom);
    std::string getCognom()const;
    void setId(std::string id);
    std::string getId()const;
    
    std::string print();
    
private:
    std::string nom;
    std::string cognom;
    std::string id;
    std::string estat;
};

#endif /* PATIENT_H */

