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
    Segment segments[];   
    public Server(int portNumber, String fileName, float p) {
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.p = p;
        segments = new Segment[1000];
        for (int i = 0; i < segments.length; ++i) {
            segments[i] = null;
        } 
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
                Segment recvSegment = Segment.parseFromBytes(receivePacket.getData(), this.mss);
                char checksum = recvSegment.calculateChecksum(recvSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress(), this.mss);
                if (checksum + recvSegment.getHeader().getChecksum() != 0xFFFF) {
                    System.out.println("Checksum failed. Discarding packet");
                } else {
                    float random = (float)Math.random();
                    //System.out.println(this.p + " " + random);
                    if (random < this.p) {
                        System.out.println("Segment " + recvSegment.getHeader().getSequence_number() + " lost");
                    } else {
                        int seqNumber = recvSegment.getHeader().getSequence_number();
                        if (seqNumber == 0 || (seqNumber > 0 && segments[seqNumber - 1] != null && segments[seqNumber - 1].isAcknowledged())) {
                            System.out.println("Received : " + recvSegment.getHeader().getSequence_number() + " Acknowledged : " + recvSegment.isAcknowledged());
                            Segment sendSegment = new Segment(recvSegment.getHeader().getSequence_number(), Constants.kAckType, (char) 0, null);
                            checksum = sendSegment.calculateChecksum(sendSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress(), this.mss);              
                            sendSegment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));          
                            DatagramPacket sendPacket = new DatagramPacket(sendSegment.getSegment(), sendSegment.getSegment().length, receivePacket.getAddress(), receivePacket.getPort());

                            serverSocket.send(sendPacket);
                        
                            System.out.println("Sent acknowledgement for segment : " + sendSegment.getHeader().getSequence_number());
                            recvSegment.setAcknowledged(true);
                            if (segments[recvSegment.getHeader().getSequence_number()] == null) {
                                segments[recvSegment.getHeader().getSequence_number()] = recvSegment;
                            } else {
                                System.out.println("Duplicate packet : " + recvSegment.getHeader().getSequence_number());
                            }
                        }
                        /*if (seqNumber > 0) {
                            System.out.println("seqNumber : " + seqNumber + " seqNumber - 1 Ack : " + segments[seqNumber - 1].isAcknowledged());
                        }*/
                    }
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

            Segment segment = Segment.parseFromBytes(packet.getData(), 100);
            char checksum = segment.calculateChecksum(segment, packet.getAddress().getAddress(), InetAddress.getLocalHost().getAddress(), 100);
            System.out.println("calculated checksum = " + (int)checksum);
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
        Server server = new Server(Constants.kServerPortNumber, "somefile", 0.1f);

        //testSegmentTransfer();
    }
}
