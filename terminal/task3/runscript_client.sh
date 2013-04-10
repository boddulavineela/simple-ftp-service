#!/bin/sh
#Task 2

server_ip=127.0.0.1
server_port=7735
method=0

if [ $# -lt 2 ]
  then
    echo "Usage : sh runscript_client.sh <loss probability> <method> (0 : GoBackN, 1 : SelRepeat)"
else 
    method=$2
    rm -f results_task3_${method}
    rm -f temp_${method}_${1}

    for ((i=1; i<=5; i++)) do
        echo 'Run ' $i
        java -classpath ../bin edu.ncsu.csc573.project2.client.Client $server_ip $server_port ../resources/rfc/rfc1mb.txt 64 500 $method >> output
        grep 'N =' output >> temp_${method}_${1}
        rm -f output
    done
fi
