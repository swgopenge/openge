package protocol;

import java.util.ArrayList;
import java.util.List;

import network.Client;

import org.apache.mina.core.buffer.IoBuffer;

public interface ProtocolHandler {
		
	public default IoBuffer encode(Client client, IoBuffer buffer) {
		return buffer;
	}
		
	public default List<IoBuffer> encode(Client client, List<IoBuffer> buffer) {
		return buffer;
	}
	
	public default List<IoBuffer> decode(Client client, IoBuffer buffer) {
		List<IoBuffer> list = new ArrayList<IoBuffer>();
		list.add(buffer);
		return list;
	}


}
