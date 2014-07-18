package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;


public class ParametersMessage extends SWGMessage {

	public ParametersMessage() {
		
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer packet = IoBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN);
		
		packet.putShort((short) 2);
		packet.putInt(0x487652DA);
		packet.putInt(0x00000384);
		
		return packet.flip();
	}
}
