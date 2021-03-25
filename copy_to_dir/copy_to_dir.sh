#!/bin/bash
filename=$1
copies=0
while [ $# -gt 1 ]
do
    mkdir -p $2
    output=$(cp -i -v $filename $2)
    if [ "$output" ]
    then
        copies=$(($copies+1))
    fi
    shift
done
echo $copies

