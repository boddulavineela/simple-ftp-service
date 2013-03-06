/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.client;

import edu.ncsu.csc573.project2.util.Constants;
import edu.ncsu.csc573.project2.util.Segment;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 *
 * @author svpendse1
 */
public class Client {
    String serverHostName;  //The hostname where the server runs
    int serverPortNumber;   //The port numberof the server
    String fileName;        //The name of the file to be transferred
    int n;                  //The window size (in MSS)
    int mss;                //The Maximum Segment Size (in bytes)
    DatagramSocket clientSocket;    //The client side datagram socket
    String fileContents;    //String to store the file contents
    byte recvBuffer[];   //The receive buffer
    InetAddress IPAddress;  //The IPAddress of the server 
    public Client(String serverHostName, int serverPortNumber,
                  String fileName, int n, int mss) {
        this.serverHostName = serverHostName;
        this.serverPortNumber = serverPortNumber;
        this.fileName = fileName;
        this.n = n;
        this.mss = mss;

        recvBuffer = new byte[mss];
        try {
            clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(Constants.kSoTimeout);
        } catch (Exception e) {
            System.err.println("Failed to create client datagram socket");
        }
          //Client operation code
        LinkedList<Segment> segments = this.readFileContentsAsSegments();

        /*for (int i = 0; i < segments.size(); ++i) {
            System.out.println(segments.get(i).getSegment().length());
            System.out.println(Segment.parseFromString(segments.get(i).getSegment()).getSegment().length());
        }*/
        sendData(segments);
    }
   
    public LinkedList<Segment> readFileContentsAsSegments() {
        LinkedList<Segment> segments = new LinkedList<Segment>();
        String content = "";
        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
            String currentLine;
            while ((currentLine = buf.readLine()) != null) {
                content += currentLine + "\n";
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
            int len = content.length();
            while (len > 0) {
                byte segmentData[] = new byte[mss - Constants.kSegmentHeaderSize];
                bis.read(segmentData, 0, segmentData.length);
                Segment currentSegment = new Segment(Constants.kDataType, (char)0, segmentData);
                segments.add(currentSegment);   
                len -= segmentData.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return segments; 
    }
   
    
    public void sendData(LinkedList<Segment> segments) {
        //System.out.println("Number of segments = " + segments.size());
        boolean sent[] = new boolean[segments.size()];
        for (int i = 0; i < sent.length; ++i) {
            sent[i] = false;
        }
        int low = 0;
        int high = Math.min(segments.size(), n);
        IPAddress = null;
        try {
            IPAddress = InetAddress.getByName(serverHostName);
        } catch (Exception e) {
            
        }
        //Send the MSS to the server
        String mssString = "" + mss;
        try {
            DatagramPacket mssPacket = new DatagramPacket(mssString.getBytes(), mssString.length(), IPAddress, serverPortNumber);

            clientSocket.send(mssPacket);
        } catch (Exception e) {
            System.err.println("Could not send MSS to the server");
        }
        //Send the segments to the server using the Go Back N protocol

        //Send the first window
        for (int i = low; i < high; ++i) {
            this.sendSegment(segments.get(i));
        }
       
        while (! sent[sent.length - 1]) {
            try {
                //System.out.println("Outside Low : " + low + " , High : " + high);
                Segment segment = this.receiveSegment();
                if (segment != null) { 
                    //System.out.println("Received acknowledgement for segment : " + segment.getHeader().getSequence_number());
                     //if (low == segment.getHeader().getSequence_number() && low <= segments.size() - 1) {

                     //Cummulative acknowledgements
                     if (low <= segments.size() - 1) {
                        //if (low > segment.getHeader().getSequence_number()) {
                        //    System.out.println("Cummulative acknowledgement occurred");
                        //}
                        int cnt = low;
                        for (;cnt <= segment.getHeader().getSequence_number(); ++cnt) {
                            sent[low] = true;
                            low++;
                            if (high <= segments.size() - 1) {
                                this.sendSegment(segments.get(high));
                                high++;
                            }
                        }
                    }
                }
            } catch (SocketTimeoutException se) {
                //A timeout occurred
                System.out.println("Timeout, sequence number = " + segments.get(low).getHeader().getSequence_number());
                for (int i = low; i < high; ++i) {
                    this.sendSegment(segments.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Segment finSegment = new Segment(Constants.kFinType, (char)0, null);
        this.sendSegment(finSegment);
        clientSocket.close();
    }

    public void sendSegment(Segment segment) {
      try {
            char checksum = segment.calculateChecksum(segment, InetAddress.getLocalHost().getAddress(), IPAddress.getAddress(), this.mss);
            segment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
            DatagramPacket sendPacket = new DatagramPacket(segment.getSegment(),
                                                           segment.getSegment().length, 
                                                           IPAddress, serverPortNumber);
            clientSocket.send(sendPacket);
            //System.out.println ("Segment " + segment.getHeader().getSequence_number() + " sent");
        } catch (Exception e ) {
            e.printStackTrace();
        }    
    }
   
    public Segment receiveSegment() throws SocketTimeoutException, IOException, SocketException, UnknownHostException {
        recvBuffer = new byte[this.mss];
        DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
        clientSocket.receive(recvPacket);
        Segment segment = Segment.parseFromBytes(recvPacket.getData(), this.mss);
        char checksum = segment.calculateChecksum(segment, IPAddress.getAddress(), InetAddress.getLocalHost().getAddress(), this.mss);
        if (checksum + segment.getHeader().getChecksum() != 0xFFFF) {
            System.out.println("Checksum failed. Discarding segment " + segment.getHeader().getSequence_number());
            return null;
        } 
        return segment;
    }
    
    public static void testSegmentTransfer() {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            byte data[] = new byte[92];
            Segment segment = new Segment(1, Constants.kAckType, (char)0, null);
            char checksum = segment.calculateChecksum(segment, InetAddress.getLocalHost().getAddress(), InetAddress.getLocalHost().getAddress(), 100);
            segment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
            //segment.getHeader().setChecksum((char)checksum);
            System.out.println(segment.toString());
            DatagramPacket packet = new DatagramPacket(segment.getSegment(), segment.getSegment().length, InetAddress.getLocalHost(), 7734);
            clientSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }
    
    public static void main(String[] args) {
        /*if (args.length != 5) {
            System.err.println("Usage : java Client <server-host-name> <server-port#> " + 
                               "<file-name> <N> <MSS>");
        }*/
        Client client = new Client("127.0.0.1", Constants.kServerPortNumber, "resources/rfc/rfc2328.txt", 100, 500);
        
        //testSegmentTransfer();
        /*Client client = new Client(args[0], Integer.parseInt(args[1]),
                                   args[2], Integer.parseInt(args[3]), 
                                   Integer.parseInt(args[4]));*/
    }
}
