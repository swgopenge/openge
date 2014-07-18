package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

public class ClientRandomNameResponse extends SWGMessage {
	
	private String raceTemplate;
	private String name;

	public ClientRandomNameResponse(String raceTemplate, String name) {
		
		this.raceTemplate = raceTemplate;
		this.name = name;

	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		
		IoBuffer result = IoBuffer.allocate(35 + raceTemplate.length() + name.length() * 2).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)4);
		result.putInt(0xE85FB868);
		result.put(getAsciiString(raceTemplate));	// Race File
		result.put(getUnicodeString(name));		// Random Name 
		result.put(getAsciiString("ui"));				// STF File
		result.putInt(0);    							// Spacer/unk
		result.put(getAsciiString("name_approved"));	// Approves Name, for Random Name Generation this always needs to be "name_approved" 
		
		result.flip();
		return result;
	}
	
}
