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
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    byte sendBuffer[];      //The send buffer
    byte recvBuffer[];   //The receive buffer
    
    public Client(String serverHostName, int serverPortNumber,
                  String fileName, int n, int mss) {
        this.serverHostName = serverHostName;
        this.serverPortNumber = serverPortNumber;
        this.fileName = fileName;
        this.n = n;
        this.mss = mss;

        sendBuffer = new byte[mss];
        recvBuffer = new byte[n * mss];
        try {
            clientSocket = new DatagramSocket();
        } catch (Exception e) {
            System.err.println("Failed to create client datagram socket");
        }
          //Client operation code
        LinkedList<Segment> segments = readFileContentsAsSegments();

        for (int i = 0; i < segments.size(); ++i) {
            System.out.println(segments.get(i).toString());
        }
        //sendData();
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
                Segment currentSegment = new Segment(mss, Constants.kDatatype, segmentData);
                segments.add(currentSegment);   
                len -= segmentData.length;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return segments; 
    }
    public void sendData() {
         try {
            InetAddress IPAddress = InetAddress.getByName(Constants.kServerHostName);
            DatagramPacket sendPacket = new DatagramPacket("hello".getBytes(), 4, IPAddress, Constants.kServerPortNumber);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            
        }
     
    }
    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage : java Client <server-host-name> <server-port#> " + 
                               "<file-name> <N> <MSS>");
        }       
        Client client = new Client(args[0], Integer.parseInt(args[1]),
                                   args[2], Integer.parseInt(args[3]), 
                                   Integer.parseInt(args[4]));
    }
}
