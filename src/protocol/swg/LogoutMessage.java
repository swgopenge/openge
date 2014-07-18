package protocol.swg;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;
import utils.Opcodes;


public class LogoutMessage extends SWGMessage{
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
		result.putShort((short) 2);
		result.putInt(Opcodes.LogoutMessage);
		return result.flip();
	}
}
