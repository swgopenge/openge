package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

import utils.Opcodes;


public class ErrorMessage extends SWGMessage {

	private String errorType;
	private String errorMessage;
	
	public ErrorMessage(String errorType, String errorMessage) {
		this.errorType = errorType;
		this.errorMessage = errorMessage;
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(11 + errorType.length() + errorMessage.length()).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)3);
		result.putInt(Opcodes.ErrorMessage);
		result.put(getAsciiString(errorType));
		result.put(getAsciiString(errorMessage));
		result.put((byte)0);
		
		return result;
	}
}
