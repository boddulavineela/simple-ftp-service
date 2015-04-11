#!/bin/sh

#if there are no input arguments, use the Go Back N method, otherwise use the specified method.
if [ $# -eq 0 ] 
  then
    java -classpath ../dist/simpleFTP.jar edu.ncsu.csc573.project2.server.Server 7735 /Users/svpendse1/Desktop/rfc_transfer.txt 0.05 0
else 
    java -classpath ../dist/simpleFTP.jar edu.ncsu.csc573.project2.server.Server 7735 /Users/svpendse1/Desktop/rfc_transfer.txt 0.05 $1
fi
