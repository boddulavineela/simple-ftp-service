#!/bin/sh
#Task 1

server_ip=127.0.0.1
server_port=7735
method=0

if [ $# -gt 0 ] 
  then
    method=$1
fi

rm -f temp
rm -f results_task1

for ((i=1; i<=5; i++)) do
    echo 'Run ' $i
    for N in 1 2 4 8 16 32 64 128 256 512 1024 
    do
        echo 'N = '$N
         java -classpath ../dist/simpleFTP.jar edu.ncsu.csc573.project2.client.Client $server_ip $server_port ../resources/rfc/rfc1mb.txt $N 500 $method >> output
    done 
    grep 'N =' output >> temp
    rm output
done

grep 'N = 1 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 2 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 4 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 8 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 16 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 32 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 64 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 128 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 256 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 512 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
grep 'N = 1024 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1
