package protocol.soe;

import org.apache.mina.core.buffer.IoBuffer;
import protocol.soe.SOEMessage;


public class SessionRequest extends SOEMessage {

	private int crcLength;
	private int connectionId;
	private int clientUDPSize;
	
	public SessionRequest() {
		
	}

	public int getCRCLength() { return crcLength; }
	public int getConnectionId() { return connectionId; }
	public int getClientUDPSize() { return clientUDPSize; }
	
	@Override
	public void deserialize(IoBuffer buffer) {
		crcLength = buffer.getInt(2);
		connectionId = buffer.getInt(6);
		clientUDPSize = buffer.getInt(10);
	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
	}



}
