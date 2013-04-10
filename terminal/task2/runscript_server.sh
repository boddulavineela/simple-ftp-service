#!/bin/sh
#$1 - target file name
#$2 - method (optional)
#if there are no input arguments, use the Go Back N method, otherwise use the specified method.
if [ $# -eq 0 ]
  then 
    echo "Usage : sh runscript_server.sh <filename> [<method>] (0: GoBackN, 1: SelRepeat)"
else 
    if [ $# -eq 1 ] 
      then
        java -classpath ../bin edu.ncsu.csc573.project2.server.Server 7735 $1 0.05 0
    else 
        java -classpath ../bin edu.ncsu.csc573.project2.server.Server 7735 $1 0.05 $2
    fi
fi
