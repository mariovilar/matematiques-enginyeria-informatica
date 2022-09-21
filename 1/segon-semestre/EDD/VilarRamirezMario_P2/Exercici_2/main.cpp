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
#include "LinkedQueue.h"
#include "DoubleNode.h"
using namespace std;

/*
 * 
 */
template <class E>
E demanarInput();

void test1()
{
    cout << "Test de prova 1" << endl;
    LinkedQueue <int> linkedQueue1;
    linkedQueue1.enqueueFront(10);
    linkedQueue1.enqueueBack(20);
    linkedQueue1.enqueueFront(30);
    linkedQueue1.enqueueBack(40);
    linkedQueue1.print();
    linkedQueue1.dequeueFront();
    linkedQueue1.enqueueBack(40);
    cout << "\n -----------" << endl;
    linkedQueue1.print();
    
    // test COPY-CONSTRUCTOR
    LinkedQueue <int> copyCat = linkedQueue1;
    cout << "\n Test del constructor còpia" << endl;
    copyCat.enqueueBack(3);
    copyCat.print();
    cout << "\n -----------" << endl;
    copyCat.dequeueFront();
    copyCat.print();
}

void test2()
{
    cout << "\n Test de prova 2" << endl;
    LinkedQueue <int> linkedQueue2;
    linkedQueue2.enqueueFront(10);
    cout << "1r getElement(): " << linkedQueue2.getFront() << endl;
    linkedQueue2.enqueueBack(20);
    linkedQueue2.enqueueBack(30);
    linkedQueue2.print();
    linkedQueue2.dequeueBack();
    cout << "2n getElement(): " << linkedQueue2.getBack() << endl;
    linkedQueue2.dequeueBack();
    linkedQueue2.dequeueFront();
    linkedQueue2.print();
    
    /* Ens saltarà la excepció que termina el programa per cua buida, clarament, 
     * o sigui que deixem comentada la següent línia de codi.
     * Si es vol comprovar, es pot descomentar.
     * 
     * linkedQueue2.dequeueFront(); 
     */
    cout << " " << endl;
}

int main(int argc, char** argv)
{
    /**/
    // Declaració de variables i inicialització. Es pot canviar 
    LinkedQueue<int> arr;
    int opt;
    
    try
    {
        // Test
        test1(); 
        test2();
        
        // Menú
        do 
        {
            // Menu options
            cout << "\n\n Menú d'Opcions" << endl;
            cout << "Suposarem totes les entrades en int" << endl;
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
                    arr.enqueueFront(demanarInput<int>());
                    break;
                case 2:
                    // Llista d'instruccions de la opció 2
                    arr.enqueueBack(demanarInput<int>());
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
template <class E>
E demanarInput()
{
    E in;
    cout << "Introdueix un input a continuació: " << endl;
    cin >> in;
    if(!cin)
        throw invalid_argument("No has introduït una cadena vàlida");
    return in;
}

