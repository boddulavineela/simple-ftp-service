/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.server;

import edu.ncsu.csc573.project2.util.Constants;
import edu.ncsu.csc573.project2.util.Segment;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

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
    Vector<Segment> segments;   
    public Server(int portNumber, String fileName, float p) {
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.p = p;
        segments = new Vector<Segment>(100);
        
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
                Segment recvSegment = Segment.parseFromBytes(receivePacket.getData());
                char checksum = Segment.calculateChecksum(recvSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
                System.out.println(receivePacket.getAddress().getHostAddress() + " " + InetAddress.getLocalHost().getHostAddress());
                if (checksum != recvSegment.getHeader().getChecksum()) {
                    System.out.println("Checksum failed. Discarding packet");
                    System.out.println("Calculated : " + (int)checksum);
                    System.out.println("Actual : " + (int)recvSegment.getHeader().getChecksum());
                } else {
                    System.out.println("Received : " + recvSegment.toString() + " Acknowledged : " + recvSegment.isAcknowledged());

                    Segment sendSegment = new Segment(recvSegment.getHeader().getSequence_number(), Constants.kAckType, (char) 0, null);
                    checksum = Segment.calculateChecksum(sendSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());              
                    sendSegment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));          
                    DatagramPacket sendPacket = new DatagramPacket(sendSegment.getSegment(), sendSegment.getSegment().length, receivePacket.getAddress(), receivePacket.getPort());

                    serverSocket.send(sendPacket);
                    System.out.println("Sent acknowledgement for segment : " + sendSegment.getHeader().getSequence_number());
               
                    recvSegment.setAcknowledged(true);
                    segments.add(recvSegment.getHeader().getSequence_number(), recvSegment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testSegmentTransfer() {
        try {
            byte recvBuffer[] = new byte[100];
            DatagramSocket serverSocket = new DatagramSocket(7734);
            DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);
            serverSocket.receive(packet);

            Segment segment = Segment.parseFromBytes(packet.getData());
            System.out.println(segment.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        /*if (args.length != 3) {
            System.err.println("Usage : java Server <port#> <file-name> <loss probability>");
        }*/
        //Server server = new Server(Integer.parseInt(args[0]), args[1], Float.parseFloat(args[2]));      
        //Server server = new Server(Constants.kServerPortNumber, "somefile", 0.5f);

        testSegmentTransfer();
    }
}
