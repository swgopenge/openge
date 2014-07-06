package protocol.soe;

import java.nio.ByteBuffer;
import java.util.Vector;

import org.apache.mina.core.buffer.IoBuffer;

import protocol.swg.SWGMessage;

public class MultiProtocol extends SOEMessage {
	
	private Vector<SOEMessage> messages;
	private Vector<SWGMessage> messages2;
	private Vector<IoBuffer> swgMessages;
	public Vector<SOEMessage> Sequencedmessages;

	private short sequencedMessageCount = 0;
	
	public MultiProtocol() { }
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
		
		/*if (messages.size() == 1) {
			if (messages.get(0) instanceof ISequenced)
				((ISequenced)messages.get(0)).setSequence((short)sequence);
			return messages.get(0).serialize();
		}*/
		
		ByteBuffer message = ByteBuffer.allocate(496);
		message.putShort((short)3);
		
		/*for (int i = 0; i < messages.size(); i++) {
			short sequence2 = sequence;
			int messageLength = messages.get(i).getSize();
			message.put((byte)messageLength);
			if (messages.get(i) instanceof ISequenced)
				((ISequenced)messages.get(i)).setSequence(sequence2);
			message.put(messages.get(i).serialize().array());
			if(messages.get(i).getData() != null) {
				if(messages.get(i) instanceof ISequenced) {
					sequence2++;
	
				}
			}
		}*/
		
		for(IoBuffer swgMsg : swgMessages) {
			
			byte[] packet = swgMsg.array();
			int length = packet.length;
						
			message.put((byte) length);
			message.put(packet);
			
		}
		
		int size = message.position();
		IoBuffer message2 = IoBuffer.allocate(size).put(message.array(), 0, size);
		return message2.flip();
		
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
