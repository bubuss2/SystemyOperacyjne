#!/bin/bash
if [ "$#" == "3" ]
then
    counter=0
    high=$3
    low=$2
    while [ $counter -lt $1 ]
    do
        shuf -i $low-$high -n 1
        counter=$(($counter + 1))
    done
fi

if [ "$#" == "1" ]
then
    file=$(< $1)
    sum=0
    count=0
    for i in $file
    do
        sum=$(($sum+$i))
        count=$(($count+1))
    done
    echo $((sum/count))
fi