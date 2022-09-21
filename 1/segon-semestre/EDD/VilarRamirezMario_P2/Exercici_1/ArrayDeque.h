/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   ArrayDeque.h
 * Author: mariovilar
 *
 * Created on March 11, 2021, 5:22 PM
 */

#ifndef ARRAYDEQUE_H
#define ARRAYDEQUE_H

#include <vector>
using namespace std;

class ArrayDeque
{
    public:
        ArrayDeque(const int max);
        void enqueueBack (const int key);
        void dequeueFront();
        bool isFull()const;
        bool isEmpty()const;
        void print();
        const int getFront();
        const int size()const;
        void enqueueFront(const int key);
        void dequeueBack();
        const int getBack();

    private:
        int _max_size;
        int _size;
        int _front;
        int _back;
        vector <int> _data;
};

#endif /* ARRAYDEQUE_H */

