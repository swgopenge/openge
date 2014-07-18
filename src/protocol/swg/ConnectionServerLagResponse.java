package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

import protocol.swg.SWGMessage;

public class ConnectionServerLagResponse extends SWGMessage{
	
	public ConnectionServerLagResponse() {
		operandCount	= 1;
		opcode			= 0x1590F63C;
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort(operandCount);
		result.putInt(opcode);
		
		return result.flip();
	}
}
