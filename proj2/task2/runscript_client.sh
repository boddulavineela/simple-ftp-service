#!/bin/sh
#$1 - server ip address
#$2 - method (optional)
#Task 2

if [ $# -eq 0 ]
  then
    echo "Usage : sh runscript_client.sh <server_ip> [<method>] (0: GoBackN, 1: SelRepeat)"
    exit 1
fi

server_ip=$1
server_port=7735
method=0
method_name="gobackn"

if [ $# -gt 0 ] 
  then
    method=$2
fi

if [ $method -eq 1 ] 
  then
    method_name="selrepeat"
fi

rm -f temp
rm -f results_task2_$method_name.txt

for ((i=1; i<=5; i++)) do
    echo 'Run ' $i
    for ((mss=100; mss<=1000; mss+=100)) 
    do
        echo 'MSS = ' $mss
        java -classpath ../bin edu.ncsu.csc573.project2.client.Client $server_ip $server_port ../resources/rfc/rfc1mb.txt 64 $mss $method >> output
    done
    grep 'N =' output >> temp
    rm output
done

grep 'MSS = 100 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 200 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 300 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 400 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 500 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 600 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 700 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 800 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 900 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
grep 'MSS = 1000 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method_name.txt
