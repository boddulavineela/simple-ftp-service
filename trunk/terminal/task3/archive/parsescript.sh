if [ $# -eq 0 ] 
   then 
        echo "Please specify method : 0 - GoBackN, 1 - SelRepeat"
else
    rm -f results_task3_$1
    for f in ./temp_$1* 
    do
        cat $f | awk '{sum+=$9; sumsq+=$9*$9} END {print sum/NR, sum/NR - 2.776 * sqrt(sumsq/NR - (sum/NR)**2) / sqrt(NR), sum/NR + 2.776 * sqrt(sumsq/NR - (sum/NR)**2)/ sqrt(NR)}' >> results_task3_$1
    done
fi
