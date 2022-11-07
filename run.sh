#!/bin/bash

javac src/*.java -d ./out/ -cp libs/*.jar

if [ $? = 0 ]; then
    cd out
    java -cp .:libs/*.jar App $1 $2
fi