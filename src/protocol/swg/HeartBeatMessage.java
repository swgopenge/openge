package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;


public class HeartBeatMessage extends SWGMessage {

	private short operandCount;
	
	public HeartBeatMessage() {
		operandCount = 1;
		opcode = 0xA16CF9AF;
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {	
		IoBuffer result = IoBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort(operandCount);
		result.putInt(opcode);
		result.flip();
		return result;
	}
}
