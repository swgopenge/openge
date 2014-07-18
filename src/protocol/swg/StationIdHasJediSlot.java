package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;


public class StationIdHasJediSlot extends SWGMessage {
	
	private boolean hasJedi;
	
	public StationIdHasJediSlot(boolean hasJedi) { 
		this.hasJedi = hasJedi;
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)2);
		result.putInt(0xCC9FCCF8);
		result.putInt(hasJedi ? 1 : 0);
		result.flip();
		return result;
	}
}
