package protocol.swg;

import java.nio.ByteOrder;
import java.util.TimeZone;
import org.apache.mina.core.buffer.IoBuffer;
import network.Client;

public class LoginClusterStatus extends SWGMessage {

	private byte[] servers;
	private int serverCount = 0;
	private TimeZone timeZone = TimeZone.getDefault();
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(10 + servers.length).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)2);
		result.putInt(0x3436AEB6);
		result.putInt(serverCount);
		result.put(servers);
		result.flip();
		return result;
	}
	public void addServer(int galaxyID, String serverIP, int serverPort, int pingPort, int maxCharacters, int status, int recommended, int population, Client client) {
		IoBuffer result = IoBuffer.allocate(39 + serverIP.length()).order(ByteOrder.LITTLE_ENDIAN);
		
		int populationStatus = 0;
		result.putInt(galaxyID);
		result.put(getAsciiString(serverIP));
		result.putShort((short)serverPort);
		result.putShort((short)pingPort);
		result.putInt(population); // ServerPopulation, if not 0xFFFFFFFF then it will show the population in brackets 
		if (population >= 300 && population < 600) populationStatus = 1;
		else if (population >= 600 && population < 900) populationStatus = 2;
		else if (population >= 900 && population < 1200) populationStatus = 3;
		else if (population >= 1200 && population < 1500) populationStatus = 4;
		else if (population >= 1500 && population < 3000) populationStatus = 5;
		else if (population == 3000) {
			populationStatus = 6;
			status = 3;
		}
		result.putInt(populationStatus); 	// 0 = very light, 1 = light, 2 = medium , 3 = heavy, 4 = very heavy, 5 = extremely heavy, 6 = full
		result.putInt(maxCharacters);
		//result.putInt(0xFFFF8F80); 	// Distance?
		result.putInt(timeZone.getRawOffset() / 3600000);
		result.putInt(status);
		result.put((byte)recommended);
		result.putInt(2500);		// Unknown
		result.putInt(250);			// Unknown
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
}
