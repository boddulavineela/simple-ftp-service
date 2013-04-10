#!/bin/sh
#Task 2

server_ip=127.0.0.1
server_port=7735
method=0

if [ $# -gt 0 ] 
  then
    method=$1
fi

rm -f temp
rm -f results_task2_$method

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

grep 'MSS = 100 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 200 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 300 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 400 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 500 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 600 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 700 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 800 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 900 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
grep 'MSS = 1000 ' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $6, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task2_$method
