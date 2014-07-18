package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

public class LoginClientToken extends SWGMessage {
	
	private byte[] sessionKey;
	private int stationID;
	private String accountName;

	public LoginClientToken(byte[] sessionKey, int stationID, String accountName) {
		this.sessionKey = sessionKey;
		this.stationID = stationID;
		this.accountName = accountName;

	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		
		IoBuffer result = IoBuffer.allocate(16 + sessionKey.length + accountName.length()).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)4);
		result.putInt(0xAAB296C6);
		result.putInt(sessionKey.length);
		result.put(sessionKey);
		result.putInt(stationID);
		result.put(getAsciiString(accountName));
		result.flip();
		return result;

	}
}
