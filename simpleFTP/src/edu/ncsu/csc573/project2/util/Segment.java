/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Scanner;

/**
 *
 * @author svpendse1
 */
public class Segment {
    static int sequence_counter = 0; //The sequence counter;
    int mss;   //The total segment size in bytes;
    SegmentHeader header;   //The Segment Header
    byte data[];    //The segment data
    boolean acknowledged;   //Indicates whether this segment has been acknowledged or not
    public Segment(int mss, char segmentType, char checksum, byte data[]) {
        this.mss = mss;
        this.data = data;
        this.acknowledged = false;
        if (data.length > (mss - Constants.kSegmentHeaderSize)) {
            System.err.println("Segment size cannot exceed " + mss + " bytes");
        }
        header = new SegmentHeader(sequence_counter, checksum, segmentType);
        sequence_counter++;
    }
   
    public Segment(int sequence_number, int mss, char segmentType, char checksum, byte data[]) {
        this.mss = mss;
        this.data = data;
        if (data.length > (mss - Constants.kSegmentHeaderSize)) {
            System.err.println("Segment size cannot exceed " + mss + " bytes");
        }
        header = new SegmentHeader(sequence_number, checksum, segmentType);

    }
    
    public void setData(byte data[]) {
        this.data = data;
    }
    
    class SegmentHeader {
        int sequence_number;    //A 32-bit sequence number;
        char checksum;         //The checksum value
        char segmentType;       //The packet type
        SegmentHeader(int sequence_number, char checksum, char segmentType) {
            this.sequence_number = sequence_number;
            this.checksum = checksum;
            this.segmentType = segmentType;
        }
       
        public String toString() {
            return "Seq : " + sequence_number + ", Checksum : " + checksum + ", SegType : " + segmentType; 
        }

        public String getSegmentHeader() {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeInt(sequence_number);
                dos.writeChar(checksum);
                dos.writeChar(segmentType);
                dos.close();
                byte header_bytes[] = bos.toByteArray();
                bos.close();

                return new String(header_bytes);
            } catch (Exception e) {
            }
            return null;
        }
    }

    public String toString() {
        return "" + "(" + header.toString() + ") " + ", MSS = " + mss + ", Data = " + new String(data);
    }

    public String getSegment() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(header.getSegmentHeader());
        buffer.append(new String(data));
        return buffer.toString(); 
    }

    public static Segment parseFromString(String input) { 
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(input.getBytes());
            DataInputStream dis = new DataInputStream(bis);
            int seq = dis.readInt();
            char cksum = dis.readChar(); 
            char type = dis.readChar(); 

            String contents = "";
            int c;
            while ((c = dis.read()) != -1) {
               contents += (char)c;
            }
            
            Segment segment = new Segment(seq, Constants.kSegmentHeaderSize + contents.length(),
                                          type, cksum, contents.getBytes());
            return segment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
