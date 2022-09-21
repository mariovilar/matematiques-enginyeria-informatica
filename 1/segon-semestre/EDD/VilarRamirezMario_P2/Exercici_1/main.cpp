/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.cpp
 * Author: mariovilar
 *
 * Created on March 11, 2021, 5:19 PM
 */

#include <iostream>
#include <vector>
#include <stdexcept>
#include "ArrayDeque.h"
using namespace std;

/*
 * 
 */

int demanarInput();

int main(int argc, char** argv)
{
    // Declaració de variables i inicialització de l'array
    ArrayDeque arr(demanarInput());
    int opt;
    
    try
    {
        // Menú
        do 
        {
            // Menu options
            cout << "\n\n Menú d'Opcions" << endl;
            cout << "1. Inserir element al davant de la cua" << endl;
            cout << "2. Inserir element al darrera de la cua" << endl;
            cout << "3. Treure element pel davant de la cua" << endl;
            cout << "4. Treure element pel darrera de la cua" << endl;
            cout << "5. Consultar el primer element" << endl;
            cout << "6. Consultar el darrer element" << endl;
            cout << "7. Imprimir tot el contingut de l’ArrayDeque" << endl;
            cout << "8. SORTIR DEL MENÚ" << endl;

            cout << "\n Input option: ";
            cin >> opt;
            if(!cin || opt > 8 || opt <= 0)
                throw invalid_argument("No has introduït una cadena vàlida");

            switch (opt) 
            {
                case 1:
                    // Llista d'instruccions de la opció 1
                    arr.enqueueFront(demanarInput());
                    break;
                case 2:
                    // Llista d'instruccions de la opció 2
                    arr.enqueueBack(demanarInput());
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
                    cout << "arr[0] = " << arr.getFront() << endl;           
                    break;
                case 6:
                    // Llista d'instruccions de la opció 6          
                    cout << "arr[-1] = " << arr.getBack() << endl;     
                    break;
                case 7:
                    // Llista d'instruccions de la opció 7
                    cout << "Elements del vector" << endl;
                    arr.print();             
                    break;
                case 8:
                    // Llista d'instruccions de la opció 7          
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

// Demana inputs. És bastant útil perquè en demanarem uns quants
int demanarInput()
{
    int in;
    cout << "Introdueix un nombre a continuació: " << endl;
    cin >> in;
    if(!cin)
        throw invalid_argument("No has introduït una cadena vàlida");
    return in;
}