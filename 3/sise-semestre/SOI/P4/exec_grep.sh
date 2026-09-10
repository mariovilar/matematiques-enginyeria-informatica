#!/bin/bash

gcc -O -shared -fPIC $1.c -o $1.so
export LD_PRELOAD=$PWD/$1.so
grep "Frankenstein" PhineasyFerb.txt
