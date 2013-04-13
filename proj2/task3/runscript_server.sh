#!/bin/sh
#$1 - loss probability
#$2 - target file name
#$3 - method (optional)
#if there are no input arguments, use the Go Back N method, otherwise use the specified method.
if [ $# -lt 2 ]
  then
    echo "Usage sh runscript_server.sh <loss probability> <filename> [<method>] (0 : GoBackN, 1 : SelRepeat)"
else
    if [ $# -eq 2 ] 
      then
        java -classpath ../bin edu.ncsu.csc573.project2.server.Server 7735 $2 $1 0
    else 
        java -classpath ../bin edu.ncsu.csc573.project2.server.Server 7735 $2 $1 $3
    fi
fi
