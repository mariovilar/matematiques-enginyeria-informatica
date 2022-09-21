/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.cpp
 * Author: mariovilar
 *
 * Created on March 17, 2021, 12:53 AM
 */

#include <iostream>
#include<fstream>
#include "LinkedQueue.h"
#include "DoubleNode.h"
#include "Patient.h"
using namespace std;

/*
 * 
 */

void llegirFitxer(LinkedQueue<Patient*>& arr);
void entrada(LinkedQueue<Patient*>& arr);
void insert(string nom, string cognom, string id, string boo, LinkedQueue<Patient*>& arr);

int main(int argc, char** argv)
{
    /**/
    // Declaració de variables i inicialització. Es pot canviar 
    LinkedQueue<Patient*> arr;
    int opt;
    
    try
    {
        // Menú
        do 
        {
            // Menu options
            cout << "\n\n Menú d'Opcions" << endl;
            
            cout << "1. Llegir un fitxer amb les entrades de pacients" << endl;
            cout << "2. Imprimir la cua de pacients" << endl;
            cout << "3. Eliminar el primer pacient de la cua" << endl;
            cout << "4. Eliminar el darrer pacient de la cua" << endl;
            cout << "5. Inserir n entrades de pacients des de teclat" << endl;
            cout << "6. Consultar el primer pacient de la cua" << endl;
            cout << "7. Consultar el darrer element de la cua" << endl;
            cout << "8. SORTIR DEL MENÚ" << endl;

            cout << "\n Input option: ";
            cin >> opt;
            if(!cin || opt > 8 || opt <= 0)
                throw invalid_argument("No has introduït una cadena vàlida");

            switch (opt) 
            {
                case 1:
                    // Llista d'instruccions de la opció 1
                    llegirFitxer(arr);
                    break;
                case 2:
                    // Llista d'instruccions de la opció 2
                    arr.print();            
                    break;
                case 3:
                    // Llista d'instruccions de la opció 3            
                    arr.dequeueFront();
                    break;
                case 4:
                    // Llista d'instruccions de la opció 4          
                    arr.dequeueBack();      
                    break;
                case 5:
                    // Llista d'instruccions de la opció 5
                    int n; cout << "Introdueix el nombre de pacients: ";
                    cin >> n;
                    for(int i = 0; i < n; i++)
                        entrada(arr);
                    break;
                    
                case 6:
                    // Llista d'instruccions de la opció 6
                    cout << "arr[0] = " << arr.getFront()->print() << endl;           
                    break;
                    
                case 7:
                    // Llista d'instruccions de la opció 7
                    cout << "arr[-1] = " << arr.getBack()->print() << endl;     
                    break;
                case 8:
                    // Llista d'instruccions de la opció 8         
                    cout << "Sayonara!" << endl;             
                    break;
            }        
        } while (opt != 8);
    }
    // Trobem arguments invàlids
    catch(const invalid_argument &ex)
    {
        cout << ex.what() << endl;
    }
    catch(const out_of_range &ex)
    {
        cout << ex.what() << endl;
    }
    
    return 0;
}

void llegirFitxer(LinkedQueue<Patient*>& arr)
{
    string nom, cognom, id, estat;
    ifstream elText;
    elText.open("entrada.txt");
    if(elText.is_open())
    {
        while(!elText.eof())
        {
            /* Notem que caldrà ser molt curosos a l'hora de mantenir l'ordre
             * en el fitxer.
             */
            
            getline(elText,nom,',');
            getline(elText,cognom,',');
            getline(elText,id,',');
            getline(elText,estat);
            
            // Inserció a la cua, en funció de l'estat
            insert(nom,cognom,id,estat,arr);
        }
    }
    elText.close();
}

void entrada(LinkedQueue<Patient*>& arr)
{
    string nom, cognom, id, estat;
    
    cout << "NOU PACIENT" << endl;
    // Introducció de dades
    cout << "Nom? " << endl;
    cin >> nom;
    cout << "Cognom? " << endl;
    cin >> cognom;
    cout << "Identificació? " << endl;
    cin >> id;
    cout << "Estat? (OK o NOT_OK)" << endl;
    cin >> estat;
    
    // Inserció a la cua, en funció de l'estat
    insert(nom,cognom,id,estat,arr);
    
}

// Per tal de no repetir codi, creem una funció per inserir el pacient a la llista
void insert(string nom, string cognom, string id, string boo, LinkedQueue<Patient*>& arr)
{
    // Inserció a la cua, en funció de l'estat
    Patient *pat = new Patient(nom, cognom, id, boo);
    if(boo.compare("OK") == 0)
        arr.enqueueBack(pat);
    else
        arr.enqueueFront(pat);
}