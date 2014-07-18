package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;


public class LoginServerString extends SWGMessage {
	
	private String loginServerString;
	
	public LoginServerString(String loginServerString) { 
		this.loginServerString = loginServerString;
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(8 + loginServerString.length()).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)2);
		result.putInt(0x0E20D7E9);
		result.put(getAsciiString(loginServerString));
		result.flip();
		
		return result;
	}
}
