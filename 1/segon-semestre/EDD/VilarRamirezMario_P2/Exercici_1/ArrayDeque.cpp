/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   ArrayDeque.cpp
 * Author: mariovilar
 * 
 * Created on March 11, 2021, 5:22 PM
 */

#include "ArrayDeque.h"
#include <vector>
#include <iostream>
using namespace std;

ArrayDeque::ArrayDeque(const int max)
{
    _max_size = max;
    _front = 0;
    _back = -1;
    _size = 0;
    vector <int> v(_max_size); _data = v;
}

void ArrayDeque::enqueueBack(const int key)
{
    if(isFull())
        throw out_of_range("Full Queue!");
    else
    {
        cout << "Enqueuing..." << endl;
        _back = (_back + 1) % _max_size; 
        _data[_back] = key;
        _size++;
    } 
}

void ArrayDeque::enqueueFront(const int key)
{
    if(isFull())
        throw out_of_range("Full Queue!");
    else
    {
        cout << "Enqueuing..." << endl;
        _front = (_front - 1) % _max_size;
        _data[_front] = key;
        _size++;
    }
}

void ArrayDeque::dequeueBack()
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    else
    {
        cout << "Removing... " << endl;
        _back = (_back - 1) % _max_size;
        _size--;
    }
}

void ArrayDeque::dequeueFront()
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    else
    {
        cout << "Removing... " << endl;
        _front = (_front + 1) % _max_size;
        _size--;
    }
}

bool ArrayDeque::isFull() const
{
    return (_size == _max_size);
}

bool ArrayDeque::isEmpty() const
{
    return (_size <= 0);
}

void ArrayDeque::print()
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    
    /* Com hem implementat un array circular, els nombres estarien desplaçats.
     * Suposem que volem els nombres en ordre: volem recórrer la llista des de
     * _front fins _max_size, i després de l'inici fins _back. Aquesta iteració
     * while ens serviria sigui quin sigui més gran dels dos atributs.
     */
    
    int i = _front;
    // A partir de l'expressió utilitzada abans, imprimim les dades del vector
    if(_front != _back)
    {
        while(i != _back)
        {
            cout << _data[i] << " ";
            i = (++i) % _max_size;
        }
    }
    cout << _data[i] << " ";

}

const int ArrayDeque::getFront()
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    return _data[_front];  
}

const int ArrayDeque::getBack()
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    return _data[_back];
}

const int ArrayDeque::size() const
{
    return _size;
}