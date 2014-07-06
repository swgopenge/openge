package protocol.soe;

import org.apache.mina.core.buffer.IoBuffer;


public class NetStatsServer extends SOEMessage {
	
	private short clientTick;
	private int serverTick;
	private long clientSent;
	private long clientReceived;
	private long serverSent;
	private long serverReceived;


	
	public NetStatsServer (short clientTick, int serverTick, long clientSent, long clientReceived, long serverSent, long serverReceived) {
		
		this.clientTick = clientTick;
		this.serverTick = serverTick;
		this.clientSent = clientSent;
		this.clientReceived = clientReceived;
		this.serverSent = serverSent;
		this.serverReceived = serverReceived;

	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	@Override
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(40);
		result.putShort((short)8);
		result.putShort(clientTick);
		result.putInt(serverTick);
		result.putLong(clientSent);
		result.putLong(clientReceived);
		result.putLong(serverSent);
		result.putLong(serverReceived);

		result.flip();
		return result;
	}

	public int getSize() { return 40; }
}

