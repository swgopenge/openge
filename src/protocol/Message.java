package protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;

public abstract class Message {
	
	protected IoBuffer data;
	protected int opcode;
	protected short sequence;
	protected SimpleBufferAllocator bufferPool;

	public Message() {
		
	}
	
	public Message(IoBuffer data) { 
		this.data = data; 
	}
	
	public int getOpcode() {
		return opcode;
	}
	
	public int getSize() {
		return (data == null) ? 0 : data.array().length;
	}
	
	public IoBuffer getData() {
		return data;
	}
	
	public abstract void    deserialize(IoBuffer data);
	public abstract IoBuffer	serialize();
	
	protected String getAsciiString(IoBuffer buffer) {
		return getString(buffer, "US-ASCII");
	}
	
	protected String getUnicodeString(IoBuffer buffer) {
		return getString(buffer, "UTF-16LE");
	}
	
	protected byte[] getAsciiString(String string) {
		return getString(string, "US-ASCII");
	}
	
	protected byte[] getUnicodeString(String string) {
		return getString(string, "UTF-16LE");
	}
	
	protected String getString(IoBuffer buffer, String charFormat) {
		
		String result;
		
		int length;
		
		if (charFormat == "UTF-16LE") length = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
		else length = buffer.order(ByteOrder.LITTLE_ENDIAN).getShort();
		
		int bufferPosition = buffer.position();
		
		try {
			result = new String(buffer.array(), bufferPosition, length, charFormat);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
		
     	//TODO: Verify if character position is set correctly after reading a UTF-16 string
		buffer.position(bufferPosition + length);
		
		return result;
		
	}
	
	private byte[] getString(String string, String charFormat) {
		
		ByteBuffer result;
		int length = 2 + string.length();
		
		if (charFormat == "UTF-16LE") {
			result = ByteBuffer.allocate(length * 2).order(ByteOrder.LITTLE_ENDIAN);
			result.putInt(string.length());
		} else {
			result = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
			result.putShort((short) string.length());
		}
		
		try {
			result.put(string.getBytes(charFormat));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new byte[] {};
		}
		
		return result.array();
		
	}

	//FIXME: this is redundant and was also bugged. At some point, get rid of the redundant functions.
	/**
	 * Reads the next string in. It gets the length of the
	 * string from the short/int at the beginning of it
	 * @param bb The ByteBuffer to read from
	 * @param charset Charset of the string
	 * @return The read string. Returns "" if exception is thrown
	 */
	protected String getNextString(IoBuffer bb, String charset) {
		try {
			int length = 0;
			if (charset.equals("US-ASCII") || charset.equals("UTF-8")) {
				length = bb.getShort();
			} else {
				length = bb.getInt() * 2;
			}
			if (length > bb.remaining())
				return "";
			byte [] data = new byte[length];
			bb.get(data);
			return new String(data, charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * Reads the next array with specified size and returns it
	 * @param bb The ByteBuffer to read from
	 * @param size Size of the array
	 * @return The array that has been read, or an empty array if an exception
	 */
	protected byte [] getNextArray(IoBuffer bb, int size) {
		try {
			if (bb.remaining() < size)
				return new byte[0];
			byte [] tmp = new byte[size];
			bb.get(tmp);
			return tmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	
	protected String getNextAsciiString(IoBuffer buffer) {
		return getNextString(buffer, "US-ASCII");
	}
	
	protected String getNextUnicodeString(IoBuffer buffer) {
		return getNextString(buffer, "UTF-16LE");
	}
	
}