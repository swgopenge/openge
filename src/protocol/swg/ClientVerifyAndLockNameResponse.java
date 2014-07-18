package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

public class ClientVerifyAndLockNameResponse extends SWGMessage {
	
	private String firstName;
	private String approved_flag;

	public ClientVerifyAndLockNameResponse(String firstName, String approved_flag) {
		
		this.firstName = firstName;
		this.approved_flag = approved_flag;

		
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(20 + firstName.length() * 2 + approved_flag.length()).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short) 9);
		result.putInt(0x9B2C6BA7);
		result.put(getUnicodeString(firstName));
		result.put(getAsciiString("ui"));		
		result.putInt(0);					
		result.put(getAsciiString(approved_flag));
		
		return result.flip();
	}
	
}
