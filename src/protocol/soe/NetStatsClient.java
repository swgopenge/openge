package protocol.soe;

import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;



public class NetStatsClient extends SOEMessage {

	private short tickCount;
	private long sent;
	private long received;
	
	public NetStatsClient() {
	}

	public short getClientTickCount() { return tickCount; }
	public long getClientPacketsSent() { return sent; }
	public long getClientPacketsReceived() { return received; }

	public int getSize() { return 40; }

	@Override	
	public void deserialize(IoBuffer data) {
		tickCount = data.getShort(2);
		sent = data.getLong(24);
		received = data.getLong(32);
	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
	}

}

