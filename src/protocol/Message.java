package protocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

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
		return new String(buffer.array(), buffer.position(), buffer.order(ByteOrder.LITTLE_ENDIAN).getShort(), StandardCharsets.US_ASCII);
	}
	
	protected String getUnicodeString(IoBuffer buffer) {
		return new String(buffer.array(), buffer.position(), buffer.order(ByteOrder.LITTLE_ENDIAN).getInt(), StandardCharsets.UTF_16LE);
	}
	
	protected byte[] getAsciiString(String string) {
		ByteBuffer result;
		
		result = ByteBuffer.allocate(2 + string.length()).order(ByteOrder.LITTLE_ENDIAN);
		result.putShort((short) string.length());
		result.put(string.getBytes(StandardCharsets.US_ASCII));
		
		return result.array();
	}
	
	protected byte[] getUnicodeString(String string) {
		ByteBuffer result;
		
		result = ByteBuffer.allocate((2 + string.length()) * 2).order(ByteOrder.LITTLE_ENDIAN);
		result.putInt(string.length());
		result.put(string.getBytes(StandardCharsets.UTF_16LE));
		
		return result.array();
	}
	
	/*
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
		short length = buffer.getShort();
		
		if (length > buffer.remaining())
			return "";
		
		byte [] data = new byte[length];
		buffer.get(data);
		
		return new String(data, StandardCharsets.US_ASCII);
		
	}
	
	protected String getNextUnicodeString(IoBuffer buffer) {
		int length = buffer.getInt() * 2;
		
		if (length > buffer.remaining())
			return "";
		
		byte [] data = new byte[length];
		buffer.get(data);
		
		return new String(data, StandardCharsets.UTF_16LE);
		
	}
	
}