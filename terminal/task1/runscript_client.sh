#!/bin/sh
#$1 - server ip address
#$2 - method (optional)
#Task 1

if [ $# -eq 0 ]
  then
    echo "Usage : sh runscript_client.sh <server_ip> [<method>] (0: GoBackN, 1: SelRepeat)"
    exit 1
fi

server_ip=$1
server_port=7735
method=0

if [ $# -gt 0 ] 
  then
    method=$2
fi

rm -f temp
rm -f results_task1_$method

for ((i=1; i<=5; i++)) do
    echo 'Run ' $i
    for N in 1 2 4 8 16 32 64 128 256 512 1024 
    do
        echo 'N = '$N
         java -classpath ../bin edu.ncsu.csc573.project2.client.Client $server_ip $server_port ../resources/rfc/rfc1mb.txt $N 500 $method >> output
    done 
    grep 'N =' output >> temp
    rm output
done

grep 'N = 1 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 2 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 4 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 8 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 16 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 32 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 64 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 128 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 256 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 512 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
grep 'N = 1024 M' temp | awk '{sum+=$9; sumsq+=$9*$9} END {print $3, sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task1_$method
