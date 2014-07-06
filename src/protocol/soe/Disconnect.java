package protocol.soe;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

public class Disconnect extends SOEMessage {

	private int connectionId;

	public Disconnect(int connectionId, int reason) {
		this.connectionId = connectionId;
	}
	
	public int getSize() {
		return 0;
	}
	
	@Override
	public void deserialize(IoBuffer data) {
		
	}
	
	@Override
	public IoBuffer serialize() {

		IoBuffer result = IoBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)5);
		result.putInt(connectionId);
		result.putShort((short)6);
		result.flip();
		return result;

	}

}
