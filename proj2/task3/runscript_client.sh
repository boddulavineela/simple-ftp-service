#!/bin/sh
#$1 - Server IP Address
#$2 - loss probability
#$3 - method (required) 
#Task 2


if [ $# -lt 3 ]
  then
    echo "Usage : sh runscript_client.sh <server ip> <loss probability> <method> (0 : GoBackN, 1 : SelRepeat)"
else
    server_ip=$1
    server_port=7735
    method=$3
    method_name="gobackn"
    if [ $method -eq 1 ]
      then
        method_name="selrepeat"
    fi

    rm -f results_task3_${method_name}.txt
    rm -f temp_${method}_${2}
   
    for ((i=1; i<=5; i++)) do
        echo 'Run ' $i
        java -classpath ../bin edu.ncsu.csc573.project2.client.Client $server_ip $server_port ../resources/rfc/rfc1mb.txt 64 500 $method >> output
        grep 'N =' output >> temp_${method}_${2}
        rm -f output
    done
fi
