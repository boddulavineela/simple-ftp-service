/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.util;

/**
 *
 * @author svpendse1
 */
public class Segment {
    static int sequence_counter = 0; //The sequence counter;
    int mss;   //The total segment size in bytes;
    SegmentHeader header;   //The Segment Header
    byte data[];    //The segment data
    
    public Segment(int mss, char segmentType, byte data[]) {
        this.mss = mss;
        this.data = data;
        if (data.length > (mss - Constants.kSegmentHeaderSize)) {
            System.err.println("Segment size cannot exceed " + mss + " bytes");
        }
        header = new SegmentHeader(sequence_counter, (char)0, segmentType);
        sequence_counter++;
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
    }

    public String toString() {
        return "" + "(" + header.toString() + ") " + ", MSS = " + mss + ", Data = " + new String(data);
    }
}
