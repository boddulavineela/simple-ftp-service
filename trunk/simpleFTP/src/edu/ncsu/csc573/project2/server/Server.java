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
    int mss;
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

        //Get the MSS from the client
        try {
            DatagramPacket mssPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
            serverSocket.receive(mssPacket);
            String mssString = new String(mssPacket.getData());
            this.mss = Integer.parseInt(mssString.trim());
            System.out.println("Received MSS = " + mss);
        } catch (Exception e) {
            
        }
       
        sendBuffer = new byte[this.mss];
        recvBuffer = new byte[this.mss];
        //Resize the send and receive buffers
        
        //Server operation code
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(recvBuffer, recvBuffer.length);
            try {
                serverSocket.receive(receivePacket);
                Segment recvSegment = Segment.parseFromString(new String(receivePacket.getData()));
                System.out.println("Received : " + recvSegment.toString());
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
