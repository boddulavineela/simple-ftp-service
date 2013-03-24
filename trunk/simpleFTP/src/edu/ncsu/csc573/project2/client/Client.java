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
    File file;
    long remainingLength;
    int numSegments;
    FileInputStream fis;    //The file input stream to read file contents
    
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
   
        try {
            file = new File(fileName);
            remainingLength = file.length();
            numSegments = (int)Math.ceil(1.0 * remainingLength / (mss - Constants.kSegmentHeaderSize));
            System.out.println("File size = " + remainingLength);
            System.out.println("Number of segments = " + numSegments);
            fis = new FileInputStream(file);
        } catch (Exception e) {
            System.err.println("Failed to create file input stream"); 
        }
        
        long startTime = System.nanoTime();
        sendDataSelRepeat();
        //sendDataGoBackN();
        long endTime = System.nanoTime();
        System.out.println("N = " + n + " MSS = " + mss + " time = " + 1.0 * (endTime - startTime) / 1000000000 + " seconds.");
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
  
    private Segment readNextSegment() {
        Segment segment = null;
        try {
            if (remainingLength <= 0) {
                return null;
            }
            byte segmentData[] = new byte[mss - Constants.kSegmentHeaderSize];
            int count = 0;
            byte nextByte = -1;
            while (count < segmentData.length) {
                nextByte = rdt_send();
                if (nextByte == -1) {
                    break;
                }
                segmentData[count++] = nextByte;
            }
            segment = new Segment(Constants.kDataType, (char)0, segmentData);
            remainingLength -= (mss - Constants.kSegmentHeaderSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return segment; 
    }

    private byte rdt_send() {
        byte nextByte = -1;
        try {
            nextByte = (byte) fis.read();
        } catch (Exception e) {
            System.err.println("Error reading data from file");
            return -1;
        }
        return nextByte;
    } 
    
    public void sendDataSelRepeat() {
        
        int low = 0;
        int high = Math.min(numSegments, n);
        LinkedList<Segment> window = new LinkedList<Segment>();
        IPAddress = null;
        try {
            IPAddress = InetAddress.getByName(serverHostName);
        } catch (Exception e) {
            System.err.println("Failed to get server by hostname");
        }

        //Send the window size N to the server
        String nString = "" + n;
        try {
            DatagramPacket nPacket = new DatagramPacket(nString.getBytes(), nString.length(), IPAddress, serverPortNumber);
            clientSocket.send(nPacket);
        } catch (Exception e) {
            System.err.println("Could not send n to the server");
        }
        
        //Initialize the first window
        for (int i = low; i < high; ++i) {
            Segment segment = readNextSegment();
            window.add(segment);
        }
        //Send the first window
        for (int i = low; i < high; ++i) {
            this.sendSegment(window.get(i));
        }
        int sentCount = 0; 
        while (sentCount < numSegments) {
            try {
                //System.out.println("Outside Low : " + low + " , High : " + high);
                Segment segment = this.receiveSegment();
                if (segment != null) { 
                    /*System.out.println("Received acknowledgement for segment : " + segment.getHeader().getSequence_number());
                    System.out.print("Window : ");
                    for (int i = 0; i < window.size(); ++i) {
                        System.out.print(window.get(i).getHeader().getSequence_number());
                        if (window.get(i).isAcknowledged()) {
                            System.out.print("t");
                        } else {
                            System.out.print("f");
                        }
                        System.out.print(" ");
                    }
                    System.out.println();*/

                    int ack_segment = segment.getHeader().getSequence_number();
                    
                    //System.out.println("low : " + low + " numSegments - 1 : " + (numSegments - 1));
                    if (low <= numSegments - 1) {
                         if (ack_segment < low) {
                         } else if (ack_segment == low) {
                             //System.out.println("Setting segment : " + window.getFirst().getHeader().getSequence_number() + " = true");
                             window.getFirst().setAcknowledged(true);
                             sentCount++;
                             //System.out.println("sentCount : " + sentCount);
                             if (high <= numSegments - 1) {                            
                                 for (int i = 0; i < window.size(); ++i) {
                                     if (! window.get(0).isAcknowledged()) {
                                         break;
                                     }
                                     window.removeFirst();
                                     Segment nextSegment = readNextSegment();
                                     if (nextSegment != null) {
                                         window.add(nextSegment);
                                         this.sendSegment(nextSegment);
                                         //System.out.println("Sending segment : " + nextSegment.getHeader().getSequence_number() + " low : " + low + " high : " +high);
                                         low++;
                                         high++;
                                     } else {
                                         //System.out.println("Last window, low : " + low + " high : " + high);
                                         low++;
                                     }
                                 }
                             }
                             //System.out.println("high : " + high + " numSegments - 1 : " + (numSegments - 1));
                         } else if (ack_segment < high) {
                             //System.out.println("ack_segment : " + ack_segment + " " + "low : " + low + " high : " + high);
                             sentCount++;
                             //System.out.println("sentCount : " + sentCount);
                             //System.out.println("Setting segment : " + window.get(ack_segment - low).getHeader().getSequence_number() + " = true");
                             window.get(ack_segment - low).setAcknowledged(true);
                             //System.out.println("Setting segment : " + window.get(ack_segment - low).getHeader().getSequence_number() + " = true");
                         }
                        //if (low > segment.getHeader().getSequence_number()) {
                        //    System.out.println("Cummulative acknowledgement occurred");
                        //}
                        //System.out.println("low : " + low + " sequence umber = " + segment.getHeader().getSequence_number());
                     }
                }
                /*for (int i = 0; i < window.size(); ++i) {
                    System.out.print(window.get(i).getHeader().getSequence_number() + " ");
                }
                System.out.println();*/
            } catch (SocketTimeoutException se) {
                //A timeout occurred
                System.out.println("Timeout, sequence number = " + window.peek().getHeader().getSequence_number());
                for (int i = 0; i < window.size(); ++i) {
                    //System.out.println("window.get("+ i + ").isAcknowledged() = " + window.get(i).isAcknowledged());
                    if (window.get(i) != null && ! window.get(i).isAcknowledged()) {
                        this.sendSegment(window.get(i));
                        //System.out.println("Retransmitting : " + window.get(i).getHeader().getSequence_number());
                    }
                }
                //System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Segment finSegment = new Segment(Constants.kFinType, (char)0, null);
        this.sendSegment(finSegment);
        clientSocket.close();

    }

    public void sendDataGoBackN() {
        //System.out.println("Number of segments = " + segments.size());
        boolean sent[] = new boolean[numSegments];
        for (int i = 0; i < sent.length; ++i) {
            sent[i] = false;
        }
        int low = 0;
        int high = Math.min(numSegments, n);
        LinkedList<Segment> window = new LinkedList<Segment>();
        IPAddress = null;
        try {
            IPAddress = InetAddress.getByName(serverHostName);
        } catch (Exception e) {
            System.err.println("Failed to get server by hostname");
        }
        
        //Send the MSS to the server
        /*String mssString = "" + mss;
        try {
            DatagramPacket mssPacket = new DatagramPacket(mssString.getBytes(), mssString.length(), IPAddress, serverPortNumber);

            clientSocket.send(mssPacket);
        } catch (Exception e) {
            System.err.println("Could not send MSS to the server");
        }*/

        //Send the segments to the server using the Go Back N protocol

        //Initialize the first window
        for (int i = low; i < high; ++i) {
            Segment segment = readNextSegment();
            window.add(segment);
        }

        //Send the first window
        for (int i = low; i < high; ++i) {
            this.sendSegment(window.get(i));
        }
       
        while (! sent[sent.length - 1]) {
            try {
                //System.out.println("Outside Low : " + low + " , High : " + high);
                Segment segment = this.receiveSegment();
                if (segment != null) { 
                    //System.out.println("Received acknowledgement for segment : " + segment.getHeader().getSequence_number());
                     //if (low == segment.getHeader().getSequence_number() && low <= segments.size() - 1) {

                     //Cummulative acknowledgements
                     if (low <= numSegments - 1) {
                        /*if (low < segment.getHeader().getSequence_number()) {
                            System.out.println("Cummulative acknowledgement occurred");
                        }*/
                        /*System.out.print("Window : ");
                        for (int i = 0; i < window.size(); ++i) {
                            System.out.print(window.get(i).getHeader().getSequence_number() + " ");
                        } 
                        System.out.println();*/
                        //System.out.println("low : " + low + " sequence umber = " + segment.getHeader().getSequence_number());
                        int cnt = low;
                        for (;cnt <= segment.getHeader().getSequence_number(); ++cnt) {
                            window.removeFirst();
                            sent[low] = true;
                            low++;
                            if (high <= numSegments - 1) {
                                Segment nextSegment = readNextSegment();
                                window.add(nextSegment);
                                //System.out.println("Sending segment : " + nextSegment.getHeader().getSequence_number());
                                this.sendSegment(nextSegment);
                                high++;
                            }
                        }
                    }
                }
                /*for (int i = 0; i < window.size(); ++i) {
                    System.out.print(window.get(i).getHeader().getSequence_number() + " ");
                }
                System.out.println();*/
            } catch (SocketTimeoutException se) {
                //A timeout occurred
                System.out.println("Timeout, sequence number = " + window.peek().getHeader().getSequence_number());
                for (int i = 0; i < window.size(); ++i) {
                    this.sendSegment(window.get(i));
                    //System.out.print(window.get(i).getHeader().getSequence_number());
                }
                //System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        Segment finSegment = new Segment(Constants.kFinType, (char)0, null);
        this.sendSegment(finSegment);
        clientSocket.close();
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
            //System.out.println("Self : " + InetAddress.getLocalHost().getHostAddress());
            //System.out.println("Remote : " + IPAddress.getHostAddress());
            char checksum = segment.calculateChecksum(segment, InetAddress.getLocalHost().getAddress(), IPAddress.getAddress());
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
        Segment segment = Segment.parseFromBytes(recvPacket.getData());
        char checksum = segment.calculateChecksum(segment, IPAddress.getAddress(), InetAddress.getLocalHost().getAddress());
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
            System.out.println("Self : " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Remote : " + InetAddress.getByName("10.139.60.135").getHostAddress());

            char checksum = segment.calculateChecksum(segment, InetAddress.getByName("10.139.60.135").getAddress(), InetAddress.getLocalHost().getAddress());
            segment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
            //segment.getHeader().setChecksum((char)checksum);
            System.out.println(segment.toString());
            DatagramPacket packet = new DatagramPacket(segment.getSegment(), segment.getSegment().length, InetAddress.getByName("10.139.60.135"), 7734);
            clientSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }
   
    public static void testRdtSend() {
        Client client = new Client("127.0.0.1", Constants.kServerPortNumber, "resources/rfc/rfc861.txt", 5, 100);

        Segment segment;
        while ((segment = client.readNextSegment()) != null) {
            System.out.println(segment.getHeader().getSequence_number());
        }
        /*int value = 0;
        while ((value = client.rdt_send()) != -1) {
            System.out.print((char)value);
        }*/
    }
   
    public static void main(String[] args) {

        //testRdtSend();
                //Client client = new Client("127.0.0.1", Constants.kServerPortNumber, "resources/rfc/rfc2328.txt", 100, 500);
        
        //testSegmentTransfer();
        if (args.length != 5) {
            System.err.println("Usage : java Client <server-host-name> <server-port#> " + 
                               "<file-name> <N> <MSS>");
            System.exit(0);
        }

        Client client = new Client(args[0], Integer.parseInt(args[1]),
                                   args[2], Integer.parseInt(args[3]), 
                                   Integer.parseInt(args[4]));

        /*Client client = new Client(args[0], Integer.parseInt(args[1]),
                                   args[2],
                                   16,
                                   500);*/

    }
}
