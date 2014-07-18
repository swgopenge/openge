package protocol.swg;

import org.apache.mina.core.buffer.IoBuffer;


public class ClientIdMsg extends SWGMessage {

	private byte[]	sessionKey;
	private String	version;
	
	public ClientIdMsg() {
		
	}
	
	
	public void deserialize(IoBuffer buffer) {
		buffer.position(6); // Skips SOE and SWG opcodes
		buffer.getInt();
		int keyLength = buffer.getInt();
		sessionKey = new byte[keyLength];
		buffer.get(sessionKey, 0, keyLength);
	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
	}

	public byte[] getSessionKey() { return sessionKey; }
	public String getVersion()    { return version; }
}
