#!/bin/sh
#Task 2

server_ip=10.139.61.41
server_port=7735
method=0

if [ $# -gt 0 ] 
  then
    method=$1
fi

rm -f results_task3
rm -f temp_${method}_${2}

for ((i=1; i<=5; i++)) do
    echo 'Run ' $i
    java -classpath ../dist/simpleFTP.jar edu.ncsu.csc573.project2.client.Client $server_ip $server_port ../resources/rfc/rfc1mb.txt 64 500 $method >> output
    grep 'N =' output >> temp_${method}_${2}
    rm output
done
