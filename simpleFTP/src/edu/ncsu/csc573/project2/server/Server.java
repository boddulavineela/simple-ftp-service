/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.server;

import edu.ncsu.csc573.project2.util.Constants;
import edu.ncsu.csc573.project2.util.Segment;
import java.net.DatagramPacket;
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
    byte recvBuffer[];       //The receive buffer
    byte sendBuffer[];          //The send buffer
    public Server(int portNumber, String fileName, float p) {
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.p = p;

        sendBuffer = new byte[Constants.kMaxBufferSize];
        recvBuffer = new byte[Constants.kMaxBufferSize];
        try {
            serverSocket = new DatagramSocket(Constants.kServerPortNumber);
        } catch (Exception e) {
            System.err.println("Failed to create server datagram socket");
        }
        //Server operation code
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(recvBuffer, recvBuffer.length);
            try {
                serverSocket.receive(receivePacket);
                Segment segment = Segment.parseFromString(new String(receivePacket.getData()));
                System.out.println("Received : " + segment.toString());
                //System.out.println("Received : "  + new String(receivePacket.getData()).trim());
            } catch (Exception e) {
            }
        }
    }
   
    public static void main(String[] args) {
        /*if (args.length != 3) {
            System.err.println("Usage : java Server <port#> <file-name> <loss probability>");
        }*/
        //Server server = new Server(Integer.parseInt(args[0]), args[1], Float.parseFloat(args[2]));      
        Server server = new Server(Constants.kServerPortNumber, "somefile", 0.5f);
    }
}
