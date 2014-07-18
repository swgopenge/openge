package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;




public class DeleteCharacterReplyMessage extends SWGMessage {
	
	private int FailureFlag;

	public DeleteCharacterReplyMessage(int FailureFlag) {
			
		this.FailureFlag = FailureFlag;
		
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short) 2);
		result.putInt(0x8268989B);
		result.putInt(FailureFlag);
		
		result.flip();
		return result;
	}
}
