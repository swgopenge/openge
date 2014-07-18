package protocol.swg;

import org.apache.mina.core.buffer.IoBuffer;


public class ClientRandomNameRequest extends SWGMessage {

	private String sharedRaceTemplate;
	
	public ClientRandomNameRequest() {
		
	}
	
	public void deserialize(IoBuffer buffer) {
		buffer.position(6); // Skips SOE and SWG opcodes
		sharedRaceTemplate = getNextAsciiString(buffer);
	}

	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
	}
	
	public String getSharedRaceTemplate() { return sharedRaceTemplate; }

}
