/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   DoubleNode.h
 * Author: mariovilar
 *
 * Created on March 17, 2021, 1:02 AM
 */

#ifndef DOUBLENODE_H
#define DOUBLENODE_H

#include <iostream>

template <class E>
class DoubleNode
{
    public:
        DoubleNode(E el);
        E getElement();
        DoubleNode *getNext();
        void setNext(DoubleNode *e);
        DoubleNode *getPrevious();
        void setPrevious(DoubleNode *e);
        
        ~DoubleNode();
    private:
        E element;
        DoubleNode *next;
        DoubleNode *previous;
        
};

template <class E>
DoubleNode<E>::DoubleNode(E el)
{
    next = nullptr;
    previous = nullptr;
    element = el;
}

template <class E>
DoubleNode<E>::~DoubleNode() {}

template <class E>
E DoubleNode<E>::getElement()
{
    return element;
}

template <class E>
DoubleNode<E>* DoubleNode<E>::getNext()
{
    return next;
} 

template <class E>
void DoubleNode<E>::setNext(DoubleNode<E> *e)
{
    next = e;
}

template <class E>
DoubleNode<E>* DoubleNode<E>::getPrevious()
{
    return previous;
}

template <class E>
void DoubleNode<E>::setPrevious(DoubleNode<E> *e)
{
    previous = e;
}

#endif /* DOUBLENODE_H */

