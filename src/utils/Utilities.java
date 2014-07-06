package utils;

import org.apache.mina.core.buffer.IoBuffer;

public class Utilities {
	
	public static String getHexString(byte [] array) {
		String dataString = "";
		for (byte currentByte: array) {
			dataString += String.format("%02X", currentByte)  + " ";
		}
		return dataString;
	}
	
	public static int getActiveLengthOfBuffer(IoBuffer buffer) {
		int length = 0;
		int i = 1;
		for (byte b : buffer.array()) {
			if (b != 0) {
				length = i;
			}
			i++;
		}
		return length;
	}
	
	public static boolean IsSOETypeMessage(byte[] data) {
		try {
			
			if ((data[0] == 0x00 && data[1] > 0x00 && data[1] < 0x1D))
				return true;
			
		} catch (java.lang.ArrayIndexOutOfBoundsException e) { }
		
		return false;
	}
	public static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
	
	public static byte[] shortToByteArray(short value) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }
}
