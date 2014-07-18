package protocol.swg;

import org.apache.mina.core.buffer.IoBuffer;

public class LoginClientId extends SWGMessage {

	private String accountName;
	private String password;
	private String version;
	
	public LoginClientId() {
		
	}
	
	public void deserialize(IoBuffer buffer) {
		buffer.position(6);
		accountName = getNextAsciiString(buffer);
		password = getNextAsciiString(buffer);
		version = getNextAsciiString(buffer);
	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
	}
	
	public String getAccountName() { return accountName; }
	public String getPassword() { return password; }
	public String getVersion() { return version; }

}
