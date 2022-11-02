#!/bin/bash

javac src/*.java -d ./out/

if [ $? = 0 ]; then
    cd out
    java App $1 $2
fi
