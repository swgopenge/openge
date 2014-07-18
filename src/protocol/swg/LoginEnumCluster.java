package protocol.swg;

import java.nio.ByteOrder;
import java.util.TimeZone;

import org.apache.mina.core.buffer.IoBuffer;

public class LoginEnumCluster extends SWGMessage {

	private byte[] servers;
	private int serverCount = 0;
	private int maxCharacters = 8;
	private int maxPopulation = 250;
	private int maxConcurrent = 12;
	private TimeZone timeZone = TimeZone.getDefault();
	
	public LoginEnumCluster(int maxCharacters) {
		this.maxCharacters = maxCharacters;
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		if (servers == null || servers.length == 0)
			return IoBuffer.allocate(0);

		IoBuffer result = IoBuffer.allocate(22 + servers.length).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)3);
		result.putInt(0xC11C63B9);
		result.putInt(serverCount);
		result.put(servers);
		result.putInt(maxCharacters);
		result.putInt(maxPopulation);
		result.putInt(maxConcurrent);
		
		int size = result.position();
		return IoBuffer.allocate(size).put(result.array(), 0, size).flip();
		
	}
	
	public void addServer(int galaxyID, String serverName) {
		IoBuffer result = IoBuffer.allocate(10 + serverName.length()).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putInt(galaxyID);
		result.put(getAsciiString(serverName));
		result.putInt(timeZone.getRawOffset() / 3600000);
		result.flip();
		
		if (servers == null)
			servers = result.array();
		else
			servers = IoBuffer.allocate(servers.length + result.capacity())
			.put(servers)
			.put(result.array())
			.flip()
			.array();
		serverCount++;
	}
	
	public int getSize() {
		return 22 + servers.length;
	}
}
