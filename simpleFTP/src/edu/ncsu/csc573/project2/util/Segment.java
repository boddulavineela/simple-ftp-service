/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.util.Scanner;

/**
 *
 * @author svpendse1
 */
public class Segment {
    static int sequence_counter = 0; //The sequence counter;
    //int mss;   //The total segment size in bytes;
    SegmentHeader header;   //The Segment Header
    byte data[];    //The segment data
    boolean acknowledged;   //Indicates whether this segment has been acknowledged or not
    public Segment(char segmentType, char checksum, byte data[]) {
        //this.mss = mss;
        this.data = data;
        this.acknowledged = false;
        /*if (data.length > (mss - Constants.kSegmentHeaderSize)) {
            System.err.println("Segment size cannot exceed " + mss + " bytes");
        }*/
        header = new SegmentHeader(sequence_counter, checksum, segmentType);
        sequence_counter++;
    }
   
    public Segment(int sequence_number, char segmentType, char checksum, byte data[]) {
        //this.mss = mss;
        this.data = data;
        this.acknowledged = false;
        /*if (data.length > (mss - Constants.kSegmentHeaderSize)) {
            System.err.println("Segment size cannot exceed " + mss + " bytes");
        }*/
        header = new SegmentHeader(sequence_number, checksum, segmentType);

    }
    
    public static void setSequenceCounter(int value) {
        sequence_counter = 0;
    }

    public static int getSequenceCounter() {
        return sequence_counter;
    }
    
    public SegmentHeader getHeader() {
        return header;
    }

    public void setHeader(SegmentHeader header) {
        this.header = header;
    }
    
    
    public boolean isAcknowledged() {
        return this.acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }
    
    public void setData(byte data[]) {
        this.data = data;
    }
    
    public byte[] getData() {
        return data;
    }
   
    public char calculateChecksum(Segment segment, byte sourceAddress[], byte destAddress[]) {
   
        byte segment_data[] = segment.getData();

        int pad_length = 0;
        byte segment_data_padded[] = null;
        if (segment_data != null) {
            //pad_length = (int)Math.ceil(1.0 * segment_data.length / 4);
            segment_data_padded = new byte[4 * (segment.getSegment().length - Constants.kSegmentHeaderSize)];
            for (int i = 0; i < segment_data.length; ++i) {
                segment_data_padded[i] = segment_data[i];
            }
        }
      
        int result = 0;
        //Pseudo Header
        result += (sourceAddress[0] << 8 | sourceAddress[1]);
        result += (sourceAddress[2] << 8 | sourceAddress[3]);
        result += (destAddress[0] << 8 | destAddress[1]);
        result += (destAddress[2] << 8 | destAddress[3]);
        result += (0x0011);
        int total_length = segment.getSegment().length;
        result += (total_length & 0xFFFF);
        //Header
        int sequence_number = segment.getHeader().getSequence_number();
        int segmentType = segment.getHeader().getSegmentType();
        int checksum = segment.getHeader().getChecksum();
        result += (sequence_number >> 16) + (sequence_number & 0xFFFF) + segmentType; 

        /*System.out.println("Total length = " + (total_length & 0xFFFF));
        System.out.println("Seq Low : " + (sequence_number >> 16));
        System.out.println("Seq High : " + (sequence_number & 0xFFFF));
        System.out.println("Segment Type : " + (int)segmentType);
        System.out.println("Checksum : " + checksum);
        System.out.println("Result before data : " + result);*/

        //Data
        if (segment_data_padded != null) {
            for (int i = 0; i < segment_data_padded.length; i+=4) {
                result += (segment_data_padded[i] << 8 | segment_data_padded[i+1]);
                result += (segment_data_padded[i+2] << 8 | segment_data_padded[i+3]);
            }
        }
        
        //System.out.println("Result after data : " + result);
        int carry = result >> 16;
        result += carry;
        
        return (char)result;
    }
    
    public class SegmentHeader {
        int sequence_number;    //A 32-bit sequence number;
        char checksum;         //The checksum value
        char segmentType;       //The packet type
        SegmentHeader(int sequence_number, char checksum, char segmentType) {
            this.sequence_number = sequence_number;
            this.checksum = checksum;
            this.segmentType = segmentType;
        }

        public int getSequence_number() {
            return sequence_number;
        }

        public void setSequence_number(int sequence_number) {
            this.sequence_number = sequence_number;
        }

        public char getChecksum() {
            return checksum;
        }

        public void setChecksum(char checksum) {
            this.checksum = checksum;
        }

        public char getSegmentType() {
            return segmentType;
        }

        public void setSegmentType(char segmentType) {
            this.segmentType = segmentType;
        }
      
        
        public String toString() {
            return "Seq : " + sequence_number + ", Checksum : " + (int)checksum + ", SegType : " + (int)segmentType; 
        }

        public byte[] getSegmentHeader() {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeInt(sequence_number);
                dos.writeChar(checksum);
                dos.writeChar(segmentType);
                byte header_bytes[] = bos.toByteArray();
                dos.close();
                bos.close();

                return header_bytes;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public String toString() {
        if (this.data == null) {
            return "" + "(" + header.toString() + ")";
        }
        return "" + "(" + header.toString() + ") " + ", Data = " + new String(data);
    }

    public byte[] getSegment() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.write(header.getSegmentHeader());
            if (data != null) {
                dos.write(data);
            }
            byte segment_bytes[] = bos.toByteArray();
            dos.close();
            bos.close();
            return segment_bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Segment parseFromBytes(byte input[]) { 
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(input);
            DataInputStream dis = new DataInputStream(bis);
            int seq = dis.readInt();
            char cksum = dis.readChar(); 
            char type = dis.readChar(); 

            byte data[] = null;
            if (type == Constants.kDataType) {
                data = new byte[input.length - Constants.kSegmentHeaderSize];
                dis.read(data);
            } else {
                
            }
            
            Segment segment = new Segment(seq,
                                          type, cksum, data); 
            return segment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Segment segment = new Segment(1, Constants.kAckType, (char)0, null); 
        char checksum = segment.calculateChecksum(segment, InetAddress.getLocalHost().getAddress(), InetAddress.getLocalHost().getAddress());
        byte segment_bytes[] = segment.getSegment();
        for (int i = 0; i < segment_bytes.length; ++i) {
            System.out.print(segment_bytes[i] + " " );
        }
        System.out.println();
        segment.getHeader().setChecksum((char)checksum);

        segment_bytes = segment.getSegment();
        for (int i = 0; i < segment_bytes.length; ++i) {
            System.out.print(segment_bytes[i] + " " );
        }
        System.out.println();
        System.out.println("Final Checksum : " + (int)checksum);
        
        Segment parsedSegment = Segment.parseFromBytes(segment.getSegment());
        segment_bytes = parsedSegment.getSegment();
        for (int i = 0; i < segment_bytes.length; ++i) {
            System.out.print(segment_bytes[i] + " " );
        }
        System.out.println();
        checksum = segment.calculateChecksum(parsedSegment, InetAddress.getLocalHost().getAddress(), InetAddress.getLocalHost().getAddress());
        System.out.println("Final Checksum : " + (int)checksum);
        //segment.getHeader().setChecksum((char)((~checksum) & 0xFFFF));
        //System.out.println("Checksum : " + (int)Segment.calculateChecksum(segment, InetAddress.getLocalHost().getAddress(), InetAddress.getLocalHost().getAddress()));
    }
}
