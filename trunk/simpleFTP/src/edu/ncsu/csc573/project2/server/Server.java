/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.server;

import edu.ncsu.csc573.project2.util.Constants;
import edu.ncsu.csc573.project2.util.Segment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
    int mss;
    Segment segments[];   
    public Server(int portNumber, String fileName, float p) {
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.p = p;
        segments = new Segment[Constants.kSegmentBufferSize];
        for (int i = 0; i < segments.length; ++i) {
            segments[i] = null;
        } 
        try {
            serverSocket = new DatagramSocket(this.portNumber);
        } catch (Exception e) {
            System.err.println("Failed to create server datagram socket");
        }

        //Server operation code
        //while (true) {
        //Get the MSS from the client
        try {
            recvBuffer = new byte[Constants.kMaxBufferSize];
            DatagramPacket mssPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
            serverSocket.receive(mssPacket);
            String mssString = new String(mssPacket.getData());
            this.mss = Integer.parseInt(mssString.trim());
            //System.out.println("Received MSS = " + mss);
        } catch (Exception e) {
            e.printStackTrace(); 
        }
       
        recvBuffer = new byte[this.mss];
        //Resize the send and receive buffers
        int seqNumber = 0; 
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(recvBuffer, recvBuffer.length);
            try {
                serverSocket.receive(receivePacket);
                Segment recvSegment = Segment.parseFromBytes(receivePacket.getData(), this.mss);
                //System.out.println("Self : " + InetAddress.getLocalHost().getHostAddress());
                //System.out.println("Remote : " + receivePacket.getAddress().getHostAddress());;
                char checksum = recvSegment.calculateChecksum(recvSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress(), this.mss);
                if (checksum + recvSegment.getHeader().getChecksum() != 0xFFFF) {
                    System.out.println("Checksum failed. Discarding segment " + recvSegment.getHeader().getSequence_number());
                } else {
                    if (recvSegment.getHeader().getSegmentType() == Constants.kFinType) {
                        /*segments = new Segment[100];
                        for (int i = 0; i < segments.length; ++i) {
                            segments[i] = null;
                        }*/
                        Segment.setSequenceCounter(0);
                        break;
                    }
                    float random = (float)Math.random();
                    //System.out.println(this.p + " " + random);
                    if (random < this.p) {
                        System.out.println("Packet loss, sequence number = " + recvSegment.getHeader().getSequence_number());
                    } else {
                        seqNumber = recvSegment.getHeader().getSequence_number();
                        if (seqNumber >= segments.length) {
                            Segment temp[] = new Segment[segments.length * 2];
                            for (int i = 0; i < segments.length; ++i) {
                                temp[i] = segments[i];
                            }
                            segments = temp;
                        }

                        if (seqNumber == 0 || (seqNumber > 0 && segments[seqNumber - 1] != null && segments[seqNumber - 1].isAcknowledged())) {
                            //System.out.println("Received : " + recvSegment.getHeader().getSequence_number() + " Acknowledged : " + recvSegment.isAcknowledged());
                            Segment sendSegment = new Segment(recvSegment.getHeader().getSequence_number(), Constants.kAckType, (char) 0, null);
                            checksum = sendSegment.calculateChecksum(sendSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress(), this.mss);              
                            sendSegment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));          
                            DatagramPacket sendPacket = new DatagramPacket(sendSegment.getSegment(), sendSegment.getSegment().length, receivePacket.getAddress(), receivePacket.getPort());

                            serverSocket.send(sendPacket);
                        
                            //System.out.println("Sent acknowledgement for segment : " + sendSegment.getHeader().getSequence_number());
                            recvSegment.setAcknowledged(true);

                            if (segments[seqNumber] == null) {
                                segments[seqNumber] = recvSegment;
                            } 
                            //Duplicate packets. Just discard.
                            //else {
                            //    System.out.println("Duplicate packet : " + seqNumber);
                            //}
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //}

        //Assemble the segments to contiguous file content
        byte fileData[] = new byte[mss * (seqNumber + 1)];
        int counter = 0;
        for (int i = 0; i < (seqNumber + 1); ++i) {
            if (segments[i] == null) {
                System.out.println("Index " + i + " is null");
            } else {
                for (int j = 0; j < segments[i].getData().length; ++j) {
                    byte segmentData[] = segments[i].getData();
                    fileData[counter++] = segmentData[j];
                }
            }
        }
        //System.out.println(new String(fileData).trim());

        //Write the file to the specified filename
        try {
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            fos.write(new String(fileData).trim().getBytes());
            fos.close();
        } catch (Exception e) {
            System.err.println("Failed to write to the specified file.");
        }
    }

    public static void testSegmentTransfer() {
        try {
            byte recvBuffer[] = new byte[100];
            DatagramSocket serverSocket = new DatagramSocket(7734);
            DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);
            serverSocket.receive(packet);

            Segment segment = Segment.parseFromBytes(packet.getData(), 100);
            System.out.println("Self : " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Remote : " + packet.getAddress().getHostAddress());
            
            char checksum = segment.calculateChecksum(segment, packet.getAddress().getAddress(), InetAddress.getLocalHost().getAddress(), 100);
            System.out.println("calculated checksum = " + (int)checksum);
            System.out.println(segment.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Usage : java Server <port#> <file-name> <loss probability>");
            System.exit(0);
        }
        Server server = new Server(Integer.parseInt(args[0]), args[1], Float.parseFloat(args[2]));
        //Server server = new Server(Constants.kServerPortNumber, "/Users/svpendse1/Desktop/rfc_transfer.txt", 0.05f);
        //testSegmentTransfer();
    }
}
