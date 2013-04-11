CSC 573 PROJECT 2 - Simple FTP Service

AUTHORS : Saurabh V. Pendse (svpendse@ncsu.edu)
          Ashish J. Sharma (ajsharma@ncsu.edu)

PURPOSE

    Implementation of the Simple FTP Service over UDP, ensuring reliability in the 
    application layer. This utility should be able to transfer files between remote 
    hosts. Sliding window protocols are often used to achieve the same in the 
    application layer. Specifically, we use the Go Back N strategy to acehive 
    reliability. This is our baseline algorithm. In addition, we also implement the 
    Selective Repeat Strategy. 
    
    The purpose of this project is to study the effects of parameters like 
    the window size (N), the Maximum Segment Size (MSS) and loss probability (p) on 
    the end-to-end transfer time between the remote hosts. In order to obtain 
    statistically significant results, we transfer a file having size of at least 
    1 MB (to better capture the variance, in terms of the time taken to transfer), as 
    well as perform each run 5 times. 
    
    We report the mean values and the confidence intervals (estimated using the 
    t-distribution) as part of our results. We also attempt to explain the behavior 
    observed using the IP/UDP and sliding window concepts learnt as part of this 
    course.

    TIMEOUT : We use a timeout period of 50 ms for our experiments. Our experimental 
              setup consisted of two remote hosts on the same private network but 
              separated by two hops. The round trip time measured via traceroute 
              was approximately 25 ms.

              Hence we set our RTO (Retransmission Timeout) = 2 * RTT (Round-Trip Time).
BUILD

    This project has a Makefile incldued in the base directory. Follow the following 
    steps to build this project : 
        1. Make sure that you are in the base directory i.e. the directory with the 
           name "proj2". You should see the Makefile and the src/ directory.
        2. If a bin directory exists, execute "make clean".
        3. Compile using "make" or "make all".
        4. You should see a successful compilation and a bin/ directory created. This 
           directory contains all the class files for this project.

RUN

    This project also includes shell scripts to execute each of the 3 tasks. In case 
    you do not want to use the shell scripts, you can run the Server and the Client 
    independently as follows : 

    To run the server, execute the command :
        java -classpath bin/ edu.ncsu.csc573.project2.server.Server <port#> <file-name> <loss probability> [<method>]
        
            port# - 7735 (for this project).
            file-name - Enter the absolute (full) path (although relative path will 
                        work as well)of the file where you want to store the 
                        transferred content.
            loss probability - A real number in [0, 1).
            [<method>] - is optional. If you do not specify anything, it will execute 
                         the Go Back N Strategy. Enter 0 for Go Back N, 1 for Selective 
                         Repeat.

    To run the client, execute the command : 
        java -classpath bin/ edu.ncsu.csc573.project2.client.Client <server-ip> <server-port#> <file-name> <N> <MSS> [<method>]

        <server-ip> - The server host name.
        <server-port#> - The server port number (7735, for this project).
        <file-name> - The path (absolute or relative) to the file name. 
        <N> - The window size.
        <MSS> - The Maximum Segment Size (in bytes).
        [<method>] - is optional. If you do not specify anything, it will execute 
                     the Go Back N Strategy. Enter 0 for Go Back N, 1 for Selective 
                     Repeat.

    Note : Make sure that you use the same method (either 0 or 1) in both the Server 
           or the Client. Inconsistent methods will lead to exceptions. 
   
    Assumption : A Unix(-like) environment with permissions to run shell scripts. You
                 can change the permissions using chmod 750 <script_name>.sh


    -> TASK 1 : Effect of N (Enter the "task1" directory which is located inside the 
                "proj2" directory)
        =) GO BACK N
            
            In a terminal tab/window : 
            - Server : execute -> sh runscript_server.sh <filename> [0]

            In a separate terminal tab/window : 
            - Client : execute -> sh runscript_client.sh <server_ip> [0]

            These scripts will execute the client and the server  
            with the Go-Back-N strategy multiple times for N in 1, 2, 4, 8, 16, 
            32, 64, 128, 256, 512, 1024. The loss probability p is fixed to 
            0.05, and the MSS is fixed to 500 bytes.
        
            This will generate the file results_task1_gobackn.txt.
                 
        =) SEL. REPEAT 
            
            In a terminal tab/window : 
            - Server : execute -> sh runscript_server.sh <filename> 1

            In a separate terminal tab/window : 
            - Client : execute -> sh runscript_client.sh <server_ip> 1

            These scripts will execute the client and the server  
            with the Selective Repeat strategy multiple times for N in 1, 2, 4, 8, 
            16, 32, 64, 128, 256, 512, 1024. The loss probability p is fixed to 
            0.05, and the MSS is fixed to 500 bytes.

            This will generate the file results_task1_selrepeat.txt.

    -> TASK 2 : Effect of MSS (Enter the "task2" directory which is located inside the 
                "proj2" directory)
        =) GO BACK N
            
            In a terminal tab/window : 
            - Server : execute -> sh runscript_server.sh <filename> [0]

            In a separate terminal tab/window : 
            - Client : execute -> sh runscript_client.sh <server_ip> [0]

            These scripts will execute the client and the server  with the Go-Back-N 
            strategy multiple times for MSS in 100, 200, 300, 400, 500, 600, 700, 
            800, 900, 1000. The loss probability p is fixed to 0.05, and the window 
            size N is fixed to 64 bytes.
            
            This will generate the file results_task2_gobackn.txt.
             
        =) SEL. REPEAT 
            
            In a terminal tab/window : 
            - Server : execute -> sh runscript_server.sh <filename> 1

            In a separate terminal tab/window : 
            - Client : execute -> sh runscript_client.sh <server_ip> 1

            These scripts will execute the client and the server with the Selective 
            Repeat strategy multiple times for MSS in 100, 200, 300, 400, 500, 600, 
            700, 800, 900, 1000. The loss probability p is fixed to 0.05, and the 
            window size N is fixed to 64 bytes.
           
            This will generate the file results_task2_selrepeat.txt.

    -> TASK 3 : Effect of p (Enter the "task3" directory which is located inside the 
                "proj2" directory)
        =) GO BACK N
        
            In a terminal tab/window : 
            - Server : execute -> sh runscript_server.sh <loss probability> <filename> [0]

            In a separate terminal tab/window : 
            - Client : execute -> sh runscript_client.sh <server_ip> <loss probability> 0 

            Each run of these scripts will execute the client and the server 
            with the Go-Back-N strategy for the specified loss probability. Make 
            10 such runs each time changing the loss probability from 0.01 to 0.1.

            Then execute the parsescript.sh script as follows : 
            sh parsescript.sh 0 
              
            This will generate the file results_task3_gobackn.txt.

        =) SEL. REPEAT

            In a terminal tab/window : 
            - Server : execute -> sh runscript_server.sh <loss probability> <filename> 1

            In a separate terminal tab/window : 
            - Client : execute -> sh runscript_client.sh <server_ip> <loss probability> 1

            Each run of these scripts will execute the client and the server 
            with the Selective Repeatstrategy for the specified loss probability. 
            Make 10 such runs each time changing the loss probability from 0.01 to 
            0.1.

            Then execute the parsescript.sh script as follows : 
            sh parsescript.sh 1 
              
            This will generate the file results_task3_selrepeat.txt.


HOW TO INTERPRET THE RESULT FILES : 

    Every result file consists of three columns : 
        Column 1 : The mean value for each experiment.
        Column 2 : The low end of the confidence interval (estimated using the t-dist.) .
        Column 3 : The high end of the confidence interval (estimated using the t-dist.).


HOW TO GENERATE VISUALS : 

    Generating the visuals requires MATLAB installed on the target system. Simply 
    execute the proj_visuals.m MATLAB script. This will generate all the required 
    graphs in the respective folders i.e. task1, task2, task3. The naming convention 
    is as follows : 
    
        figure_<task name>_<method>.eps
        
    The entire pdf document can be generated using the Makefile provided in the the 
    tex/ folder.
