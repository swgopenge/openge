package protocol.soe;

import java.nio.ByteBuffer;
import java.util.Vector;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;

import protocol.swg.SWGMessage;

public class MultiProtocol extends SOEMessage {
	
	private Vector<SOEMessage> messages;
	private Vector<SWGMessage> messages2;
	private Vector<IoBuffer> swgMessages;
	public Vector<SOEMessage> Sequencedmessages;

	private short sequencedMessageCount = 0;
	
	public MultiProtocol(SimpleBufferAllocator bufferPool) { 
		this.bufferPool = bufferPool;
	}
	public MultiProtocol(IoBuffer data) { 
		super(data); 
	}
	
	public boolean addMessage(SOEMessage message) {
		int messageLength;

		if (messages == null)
			messages = new Vector<SOEMessage>();
		
		if (messages.size() > 0) {
			messageLength = message.getSize();
			if (messageLength + 1 > getRemainingSize())
				return false;
		}
		
		messages.add(message);
		if(message instanceof ISequenced){
			sequencedMessageCount ++;
		}
		for (int i = 0; i < messages.size(); i++) {

			if(messages.get(i) instanceof ISequenced) {
				
				if(Sequencedmessages == null) {
					Sequencedmessages = new Vector<SOEMessage>();
					Sequencedmessages.add(messages.get(i));
				}
				
				else {
					Sequencedmessages.add(messages.get(i));
				}
			}
		}
		return true;
	}
	public boolean addMessage(SWGMessage message) {
		int messageLength;

		if (messages == null)
			messages2 = new Vector<SWGMessage>();
		
		if (messages2.size() > 0) {
			messageLength = message.getSize();
			if (messageLength + 1 > getRemainingSize())
				return false;
		}
		
		messages2.add(message);
		return true;
	}
	
	public boolean addSWGMessage(IoBuffer message) {
		
		if(swgMessages == null)
			swgMessages = new Vector<IoBuffer>();
		
		int messageLength = message.array().length;
		
		if(messageLength + 1 > getRemainingSize())
			return false;
		
		swgMessages.add(message);
		return true;
		
	}
	
	public Vector<SOEMessage> getSequencedmessages() {
		return Sequencedmessages;
	}

	public short getSequencedMessageCount() { return sequencedMessageCount; }
	
	public void deserialize(IoBuffer buffer) {
		// TODO: Implement This.
	}
	
	@Override
	public IoBuffer serialize() {
		if (swgMessages.size() == 0)
			return IoBuffer.allocate(0);
		
		IoBuffer message = bufferPool.allocate(getMessagesSize(), false);
		message.putShort((short)3);
		
		for(IoBuffer swgMsg : swgMessages) {
			
			byte[] packet = swgMsg.array();
			int length = packet.length;
						
			message.put((byte) length);
			message.put(packet);
			
		}
		
		return message.flip();
	}
	
	public int getMessagesSize() {
		int size = 2;
		for(IoBuffer swgMsg : swgMessages) {
			byte[] packet = swgMsg.array();
			int length = packet.length;
			size += length + 1;
		}
		return size;
	}
	
	public IoBuffer[] getMessages() { 
		
		Vector<IoBuffer> msgs = new Vector<IoBuffer>();
		IoBuffer buffer = data;
		buffer.position(2);

		while (buffer.position() < buffer.limit()) {
			
			short length = (short)(buffer.get() & 0xFF);
			
			if (length == 255)
				length = buffer.getShort();
			//if (length > Utilities.getActiveLengthOfBuffer(buffer) - buffer.position())
			//	break;
			if (buffer.remaining() < length || !buffer.hasArray())
				break;
			
			if(length < 0)
				break;

			IoBuffer packet = IoBuffer.allocate(length);
			packet.setAutoExpand(true);
						
			packet.put(buffer.array(), buffer.position(), length);
			packet.flip();
			//System.out.println("RECV: "+ packet.getHexDump());
			msgs.add(packet);
			buffer.position(buffer.position() + length);
						
		}
		
		IoBuffer [] tmp = new IoBuffer[msgs.size()];
		msgs.toArray(tmp);

		return tmp; 
	}
	public boolean hasMessages() { return swgMessages != null && swgMessages.size() > 0; }

	public byte[] GetData() { return null; }
	private int getRemainingSize() { return 493 - getSize(); }
	public int getSize() { 
		int size = 2; // header
		int currentLength;
		
		for (int i = 0; i < swgMessages.size(); i++) {
			currentLength = swgMessages.get(i).array().length;
			size += currentLength + 1;
		}
		
		return size; 
	}

}
