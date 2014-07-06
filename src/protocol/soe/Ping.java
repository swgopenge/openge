package protocol.soe;

import org.apache.mina.core.buffer.IoBuffer;

public class Ping extends SOEMessage {

	public Ping() {
		
		
	}
	

	@Override
	public void deserialize(IoBuffer data) {
		
	}
	
	@Override
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(2);
		
		result.putShort((short)6);
		result.flip();
		return result;
	}
}
