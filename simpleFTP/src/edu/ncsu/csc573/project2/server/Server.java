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
    //int mss;
    int n;      //The receiver window size (Selective Repeat)
    Segment segments[];
    FileOutputStream fos;
    boolean isFirstPacket;
    public Server(int portNumber, String fileName, float p) {
        this.portNumber = portNumber;
        this.fileName = fileName;
        this.isFirstPacket = true;
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
        receiveDataSelRepeat();
        //receiveDataGoBackN();
    }
   
    public void receiveDataSelRepeat() {
        int low = 0, high = 0;
        //Server operation code
        while (true) {
            //Get the MSS from the client
            try {
                recvBuffer = new byte[Constants.kMaxBufferSize];
                DatagramPacket nPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                serverSocket.receive(nPacket);
                String nString = new String(nPacket.getData());
                this.n = Integer.parseInt(nString.trim());

                segments = new Segment[this.n];
                for (int i = 0; i < segments.length; ++i) {
                    segments[i] = null;
                }
                System.out.println("Received n = " + n);

                low = 0;
                high = n;
            } catch (Exception e) {
                e.printStackTrace();
            }
            recvBuffer = new byte[Constants.kMaxBufferSize];
            //Resize the send and receive buffers
            int seqNumber = 0;
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                try {
                    serverSocket.receive(receivePacket);
                    if (isFirstPacket) {
                        try {
                            fos = new FileOutputStream(new File(fileName));
                        } catch (Exception e) {
                            System.err.println("Failed to create output stream for file : " + fileName);
                        }
                        isFirstPacket = false;
                    }
                    byte packetData[] = new byte[receivePacket.getLength()];
                    System.arraycopy(receivePacket.getData(), 0, packetData, 0, receivePacket.getLength());
                    Segment recvSegment = Segment.parseFromBytes(packetData);
                    recvSegment.setAcknowledged(false);
                    //System.out.println("Self : " + InetAddress.getLocalHost().getHostAddress());
                    //System.out.println("Remote : " + receivePacket.getAddress().getHostAddress());;
                    char checksum = recvSegment.calculateChecksum(recvSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
                    if (checksum + recvSegment.getHeader().getChecksum() != 0xFFFF) {
                        System.out.println("Checksum failed. Discarding segment " + recvSegment.getHeader().getSequence_number());
                    } else {
                        if (recvSegment.getHeader().getSegmentType() == Constants.kFinType) {
                            this.isFirstPacket = true;
                            fos.flush();
                            fos.close();
                            Segment.setSequenceCounter(0);
                            break;
                        }
                        float random = (float)Math.random();
                        //System.out.println(this.p + " " + random);
                        if (random < this.p) {
                            System.out.println("Packet loss, sequence number = " + recvSegment.getHeader().getSequence_number());
                        } else {
                            seqNumber = recvSegment.getHeader().getSequence_number();
                            if (seqNumber < low) {
                                Segment sendSegment = new Segment(seqNumber, Constants.kAckType, (char) 0, null);
                                checksum = sendSegment.calculateChecksum(sendSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
                                sendSegment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
                                DatagramPacket sendPacket = new DatagramPacket(sendSegment.getSegment(), sendSegment.getSegment().length, receivePacket.getAddress(), receivePacket.getPort());
                                serverSocket.send(sendPacket);

                            } else if (seqNumber == low) {

                                //System.out.println("seqNumber : " + seqNumber + " low : " + low + " high : " + high);
                                segments[seqNumber - low] = recvSegment;
                                segments[seqNumber - low].setAcknowledged(false);
                                
                                /*System.out.print("Window : ");
                                for (int i = 0; i < segments.length; ++i) {
                                    if (segments[i] == null) {
                                        System.out.print("-1 ");
                                    } else {
                                        System.out.print(segments[i].getHeader().getSequence_number() + " ");
                                    }
                                }
                                System.out.println();*/
                                
                                Segment sendSegment = new Segment(seqNumber, Constants.kAckType, (char) 0, null);
                                checksum = sendSegment.calculateChecksum(sendSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
                                sendSegment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
                                DatagramPacket sendPacket = new DatagramPacket(sendSegment.getSegment(), sendSegment.getSegment().length, receivePacket.getAddress(), receivePacket.getPort());
                                serverSocket.send(sendPacket);
                                segments[seqNumber - low].setAcknowledged(true);

                                for (int i = 0; i < segments.length; ++i) {
                                    if (segments[0] != null) {
                                        byte segmentData[] = segments[0].getData();
                                        fos.write(new String(segmentData).trim().getBytes());
                                        for (int j = 1; j < segments.length; ++j) {
                                            segments[j - 1] = segments[j]; 
                                            segments[j] = null;
                                        }
                                        low++;
                                        high++;
                                    } else {
                                        break;
                                    }
                                }
                            } else if (seqNumber < high) {
                                segments[seqNumber - low] = recvSegment;
                                segments[seqNumber - low].setAcknowledged(false);
                                
                                //System.out.println("seqNumber : " + seqNumber + " low : " + low + " high : " + high);
                                Segment sendSegment = new Segment(seqNumber, Constants.kAckType, (char) 0, null);
                                checksum = sendSegment.calculateChecksum(sendSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
                                sendSegment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
                                DatagramPacket sendPacket = new DatagramPacket(sendSegment.getSegment(), sendSegment.getSegment().length, receivePacket.getAddress(), receivePacket.getPort());
                                serverSocket.send(sendPacket);
                                segments[seqNumber - low].setAcknowledged(true);

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }     
  
    }

    public void receiveDataGoBackN() {
        //Server operation code
        while (true) {
            //Get the MSS from the client
            /*try {
                recvBuffer = new byte[Constants.kMaxBufferSize];
                DatagramPacket mssPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                serverSocket.receive(mssPacket);
                String mssString = new String(mssPacket.getData());
                this.mss = Integer.parseInt(mssString.trim());
                //System.out.println("Received MSS = " + mss);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            recvBuffer = new byte[Constants.kMaxBufferSize];
            //Resize the send and receive buffers
            int seqNumber = 0;
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                try {
                    serverSocket.receive(receivePacket);
                    if (isFirstPacket) {
                        try {
                            fos = new FileOutputStream(new File(fileName));
                        } catch (Exception e) {
                            System.err.println("Failed to create output stream for file : " + fileName);
                        }
                        isFirstPacket = false;
                    }
                    byte packetData[] = new byte[receivePacket.getLength()];
                    System.arraycopy(receivePacket.getData(), 0, packetData, 0, receivePacket.getLength());
                    Segment recvSegment = Segment.parseFromBytes(packetData);
                    //System.out.println("Self : " + InetAddress.getLocalHost().getHostAddress());
                    //System.out.println("Remote : " + receivePacket.getAddress().getHostAddress());;
                    char checksum = recvSegment.calculateChecksum(recvSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
                    if (checksum + recvSegment.getHeader().getChecksum() != 0xFFFF) {
                        System.out.println("Checksum failed. Discarding segment " + recvSegment.getHeader().getSequence_number());
                    } else {
                        if (recvSegment.getHeader().getSegmentType() == Constants.kFinType) {
                            //Assemble the segments to contiguous file content
                            this.isFirstPacket = true;
                            fos.flush();
                            fos.close();
                            /*int fileDataLength = 0;
                            for (int i = 0; i < (seqNumber + 1); ++i) {
                                fileDataLength += segments[i].getData().length;
                            }
                            
                            byte fileData[] = new byte[fileDataLength];
                            int counter = 0;
                            for (int i = 0; i < (seqNumber + 1); ++i) {
                                if (segments[i] == null) {
                                    System.out.println("Index " + i + " is null");
                                } else {
                                    byte segmentData[] = segments[i].getData();
                                    for (int j = 0; j < segments[i].getData().length; ++j) {
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
                            }*/
                            
                            //Reset the segments array
                            segments = new Segment[100];
                            for (int i = 0; i < segments.length; ++i) {
                                segments[i] = null;
                            }
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
                              
                                //Send the acknowledgement
                                Segment sendSegment = new Segment(recvSegment.getHeader().getSequence_number(), Constants.kAckType, (char) 0, null);
                                checksum = sendSegment.calculateChecksum(sendSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
                                sendSegment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
                                DatagramPacket sendPacket = new DatagramPacket(sendSegment.getSegment(), sendSegment.getSegment().length, receivePacket.getAddress(), receivePacket.getPort());
                                
                                serverSocket.send(sendPacket);
                                
                                //System.out.println("Sent acknowledgement for segment : " + sendSegment.getHeader().getSequence_number());
                                recvSegment.setAcknowledged(true);
                                
                                if (segments[seqNumber] == null) {
                                    segments[seqNumber] = recvSegment;
                                    //Write the segment to the file
                                    byte segmentData[] = recvSegment.getData();
                                    fos.write(new String(segmentData).trim().getBytes());
 
                                }
                                //Duplicate packets. Just discard.
                                //else {
                                //    System.out.println("Duplicate packet : " + seqNumber);
                                //}
                            } else {
                                //Send last acknowledgement for last received segment
                                int ackSeqNumber = seqNumber - 1;
                                while (ackSeqNumber >= 0) {
                                    if (segments[ackSeqNumber] != null && segments[ackSeqNumber].isAcknowledged()) {
                                        break;    
                                    }
                                    ackSeqNumber--;
                                }
                                Segment sendSegment = new Segment(ackSeqNumber, Constants.kAckType, (char) 0, null);
                                checksum = sendSegment.calculateChecksum(sendSegment, receivePacket.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
                                sendSegment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
                                DatagramPacket sendPacket = new DatagramPacket(sendSegment.getSegment(), sendSegment.getSegment().length, receivePacket.getAddress(), receivePacket.getPort());

                                serverSocket.send(sendPacket);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            System.out.println("Self : " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Remote : " + packet.getAddress().getHostAddress());
            
            char checksum = segment.calculateChecksum(segment, packet.getAddress().getAddress(), InetAddress.getLocalHost().getAddress());
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
