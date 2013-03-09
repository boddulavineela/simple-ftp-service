for N in 1 2 4 8 16 32 64 128 256 512 1024
do
    echo 'N = ' $N
    java -classpath dist/simpleFTP.jar edu.ncsu.csc573.project2.server.Server &
    java -classpath dist/simpleFTP.jar edu.ncsu.csc573.project2.client.Client
done
