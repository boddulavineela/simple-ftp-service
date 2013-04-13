#!/bin/sh

method_name="gobackn"
if [ $1 -eq 1 ] 
  then
    method_name="selrepeat"
fi

rm -f results_task3_$method_name.txt

for f in ./temp_$1* 
do
    cat $f | awk '{sum+=$9; sumsq+=$9*$9} END {print sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task3_$method_name.txt
done
