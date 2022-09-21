/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   LinkedQueue.h
 * Author: mariovilar
 *
 * Created on March 17, 2021, 12:54 AM
 */

#ifndef LINKEDQUEUE_H
#define LINKEDQUEUE_H
#include "DoubleNode.h"
#include <iostream>
using namespace std;

template <class E>
class LinkedQueue
{
public:
    LinkedQueue();
    LinkedQueue(const LinkedQueue& q);
    ~LinkedQueue();
    void enqueueFront(E f);
    void enqueueBack(E f);
    void dequeueFront();
    void dequeueBack();
    
    bool isEmpty()const;
    void print()const;
    const E& getFront()const;
    const E& getBack()const;
    const int size()const;
    
private:
    int _size;
    DoubleNode<E> *_front;
    DoubleNode<E> *_back;
};

using namespace std;

template <class E>
LinkedQueue<E>::~LinkedQueue()
{
    while(!isEmpty())
    {
        dequeueFront();
        _size--;
    }
}

template <class E>
LinkedQueue<E>::LinkedQueue()
{
    this->_front = nullptr;
    this->_back = nullptr;
    this->_size = 0;
}

template <class E>
LinkedQueue<E>::LinkedQueue(const LinkedQueue<E>& q)
{
    if(q._front == nullptr) 
    {
        _front = nullptr;
        _back = nullptr;
    }
    else
    {
        this->_size = 0;
        this->_front = new DoubleNode<E>(q._front->getElement());
        DoubleNode<E> *nNode = _front; // Ens servirà per iterar la LinkedQueue nova
        _size++;
        DoubleNode<E> *qFront = q._front; 
        DoubleNode<E> *qNode = qFront; // Ens servirà per iterar LinkedQueue q
        DoubleNode<E> *nTmp = nNode; // Per fer setPrevious()
        while(qNode->getNext() != nullptr)
        {
            nNode->setNext(new DoubleNode<E>(qNode->getNext()->getElement()));
            nTmp = nNode;
            qNode = qNode->getNext();
            nNode = nNode->getNext();
            nNode -> setPrevious(nTmp);
            _size++;
        }
        this->_back = nNode;
    }
}

template <class E>
void LinkedQueue<E>::enqueueFront(E f)
{
    DoubleNode<E> *node = new DoubleNode<E>(f);
    if(isEmpty())
    {
        _front = node;
        _back = node;
    }
    else
    {
        _front -> setPrevious(node);
        node -> setNext(_front);
        _front = node;
    }
    this->_size++;
}

template <class E>
void LinkedQueue<E>::enqueueBack(E f)
{
    DoubleNode<E> *node = new DoubleNode<E>(f);
    if(isEmpty())
    {
        _front = node;
        _back = node;
    }
    else
    {
        _back -> setNext(node);
        node -> setPrevious(_back);
        _back = node; 
    }
    this->_size++;
}

template <class E>
void LinkedQueue<E>::dequeueFront()
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    else
    {
        DoubleNode<E>* node_p = _front;
        _front = node_p->getNext();
        
        // Si solament hi ha un element
        if(_front == nullptr)
            _back = nullptr;
        // Per la resta de casos
        else
            _front->setPrevious(nullptr);
        delete node_p;
        _size--;
    }
}

template <class E>
void LinkedQueue<E>::dequeueBack()
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    else
    {
        DoubleNode<E>* node_p = _back;
        _back = node_p->getPrevious();
        
        // Si solament hi ha un element
        if(_back == nullptr)
            _front = nullptr;
        // Per la resta de casos
        else
            _back->setNext(nullptr);
        delete node_p;
        _size--;
    }
}

template <class E>
bool LinkedQueue<E>::isEmpty()const
{
    return (_size <= 0);
}

template <class E>
void LinkedQueue<E>::print()const
{
    // Per iterar des del principi fins el final necessitem trobar _front
    DoubleNode<E>* node = this->_front;
    while(node != nullptr)
    {
        cout << node->getElement() << " ";
        node = node->getNext();
    }
}

template <class E>
const E& LinkedQueue<E>::getFront()const
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    return _front->getElement();  
}

template <class E>
const E& LinkedQueue<E>::getBack()const
{
    if(isEmpty())
        throw out_of_range("La cua està buida");
    return _back->getElement();
}

template <class E>
const int LinkedQueue<E>::size()const
{
    return _size;
}

#endif /* LINKEDQUEUE_H */

