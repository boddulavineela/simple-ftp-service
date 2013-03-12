/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ncsu.csc573.project2.util;

/**
 *
 * @author svpendse1
 */
public class Constants {
    public static final int kMaxBufferSize = 1024;
    public static final int kServerPortNumber = 7735;
    public static final int kSegmentHeaderSize = 8;
    public static final int kSegmentBufferSize = 100;
    public static final int kSoTimeout = 20;
    
    public static String kServerHostName = "127.0.0.1";

    public static final char kAckType = 43690;
    public static final char kDataType = 21845;
    public static final char kFinType = 65535;
}
