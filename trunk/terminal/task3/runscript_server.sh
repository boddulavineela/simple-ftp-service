#!/bin/sh

#if there are no input arguments, use the Go Back N method, otherwise use the specified method.
if [ $# -eq 0 ]
  then
    echo "Usage sh runscript_server.sh <loss probability> <method> (0 : GoBackN, 1 : SelRepeat)"
else
    if [ $# -eq 1 ] 
      then
        java -classpath ../bin edu.ncsu.csc573.project2.server.Server 7735 /Users/svpendse1/Desktop/rfc_transfer.txt $1 0
    else 
        java -classpath ../bin edu.ncsu.csc573.project2.server.Server 7735 /Users/svpendse1/Desktop/rfc_transfer.txt $1 $2
    fi
fi
