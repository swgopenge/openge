package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;


public class CmdSceneReady extends SWGMessage {
	
	private static final byte[] packetData = new byte[] { (byte)0x01, (byte)0x00, (byte)0x22, (byte)0x1C, (byte)0xFD, (byte)0x43 };

	public CmdSceneReady() {
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN).put(packetData).flip();
	}
}
