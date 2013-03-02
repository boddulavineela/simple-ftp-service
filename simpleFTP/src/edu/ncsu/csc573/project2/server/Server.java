/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.server;

import java.net.DatagramSocket;

/**
 *
 * @author svpendse1
 */
public class Server {
    
    int portNumber; //The port number to which the server is listening
    String fileName; //The name of the file where the data will be written
    float p;    //The packet loss probability
    DatagramSocket serverSocket;    //The server side datagram socket
    byte receiveBuffer[];       //The receive buffer
    byte sendBuffer[];          //The send buffer
    public Server(int portNumber, String fileName, float p) {
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.p = p;

        //Server operation code
    }
   
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage : java Server <port#> <file-name> <loss probability>");
        }
        Server server = new Server(Integer.parseInt(args[0]), args[1], Float.parseFloat(args[2]));      
    }
}
