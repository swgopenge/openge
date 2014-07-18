package protocol.swg;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;
import utils.Opcodes;


public class LoginServerId extends SWGMessage {
	
	private int loginServerID;
	
	public LoginServerId(int loginServerID) { 
		this.loginServerID = loginServerID;
	}
	
	public void deserialize(IoBuffer buffer) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer buffer = IoBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN);
		
		buffer.putShort((short)2);
		buffer.putInt(Opcodes.LoginServerId);
		buffer.putInt(loginServerID);
		buffer.flip();

		return buffer;
	}
}
