package protocol.soe;

import java.nio.ByteBuffer;
import java.util.Vector;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;

import protocol.swg.SWGMessage;

public class DataChannelA extends SOEMessage implements ISequenced, ICombinable {
	
	private Vector<IoBuffer> messages;
	private short sequence;
	
	public DataChannelA(SimpleBufferAllocator bufferPool) { 
		this.bufferPool = bufferPool;
	}
	
	public DataChannelA(IoBuffer data, SimpleBufferAllocator bufferPool) { 
		super(data); 
		sequence = data.getShort(2);
		this.bufferPool = bufferPool;
	}
	
	public boolean addMessage(SWGMessage message) {
		return addMessage(message.serialize());
	}
	
	public boolean addMessage(IoBuffer buffer) {
		byte[] messageData = buffer.array();
		int messageLength = messageData.length;
		//System.out.println(messageLength);

		if(messageLength > 487)
			return false;
		
		if (messages == null)
			messages = new Vector<IoBuffer>();
			
		if (messages.size() == 1) {
			if (messageLength + (messageLength > 254 ? 3 : 1) + 2 > getRemainingSize())
				return false;
		} else if(messages.size() > 1) {
			if (messageLength + (messageLength > 254 ? 3 : 1) > getRemainingSize())
				return false;
		}
		

		messages.add(buffer);
		if(getRemainingSize() < 1) {
			messages.remove(buffer);
			return false;
		}
			
		return true;
	}
	
	@Override
	public void deserialize(IoBuffer data) {
		
	}
	
	@Override
	public IoBuffer serialize() {
		IoBuffer message = bufferPool.allocate(496, false);
		message.putShort((short)9);
		message.putShort((short)sequence);
		if (messages.size() > 1) {
			message.putShort((short)0x0019);
			for (int i = 0; i < messages.size(); i++) {
				message.put(getMessageSize(i));
				message.put(messages.get(i).array());
			}
		}
		else {
			if(message.remaining() >= messages.get(0).array().length)
				message.put(messages.get(0).array());
		}
		
		int size = message.position();
		message = IoBuffer.allocate(size).put(message.array(), 0, size);
		return message.flip();
	}
	
	public IoBuffer[] getMessages() { 
		messages = new Vector<IoBuffer>();
		IoBuffer buffer = data;
		buffer.position(4);
		if (buffer.getShort() == 0x19) {
			while (buffer.position() < data.array().length) {
				short length = (short)(buffer.get() & 0xFF);
				if (length == 255)
					length = buffer.getShort();
				//if (length > Utilities.getActiveLengthOfBuffer(buffer) - buffer.position())
				//	break;
				if (length > buffer.remaining() || !buffer.hasArray() || length < 0)
					break;

				messages.add(bufferPool.allocate(length, false).put(buffer.array(), buffer.position(), length));
				buffer.position(buffer.position() + length);
			}
		}
		else {
			int length = data.array().length - 4;
			messages.add(bufferPool.allocate(length, false).put(buffer.array(), 4, length));
		}
		
		IoBuffer [] tmp = new IoBuffer[messages.size()];
		messages.toArray(tmp);
		return tmp;
	}
	
	private byte[] getMessageSize(int index) {
		int messageLength = messages.get(index).array().length;
		ByteBuffer result = ByteBuffer.allocate(3);
		
		if (messageLength > 254)
			result.put((byte)0xff).putShort((short)messageLength);
		else
			result.put((byte)messageLength);
		
		int size = result.position();
		return ByteBuffer.allocate(size).put(result.array(), 0, size).array();
	}
	
	public int getSize() {
		int size = 4;  // header
		int currentLength;
		
		if (messages.size() > 1) {
			size += 2;
			for (int i = 0; i < messages.size(); i++) {
				currentLength = messages.get(i).array().length;
				size += currentLength + (currentLength > 254 ? 3 : 1);
			}
		}
		else {
			size += messages.get(0).array().length;
		}
		return size;
	}

	public short getSequence() { 
		return data.getShort(2);	
	}
	
	public void setSequence(short sequence) {
		this.sequence = sequence;		
	}

	private int getRemainingSize() { return 493 - getSize(); }
	public boolean hasMessages() { return messages != null && messages.size() > 0; }
	
}