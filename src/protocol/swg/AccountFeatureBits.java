package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;


public class AccountFeatureBits extends SWGMessage {
	
	private static final byte[] unk = new byte[] { 
				(byte)0x31, (byte)0x82, (byte)0x5C, (byte)0x02, 
				(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, 
				(byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, 
				(byte)0x8A, (byte)0xC0, (byte)0xEA, (byte)0x4E };
	
	public AccountFeatureBits() { 
		
		operandCount	= 2;
		opcode			= 0x979F0279;
			
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() { 
		IoBuffer result = IoBuffer.allocate(22).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort(operandCount);
		result.putInt(opcode);
		result.put(unk);
		result.flip();
		
		return result;
	}
}
