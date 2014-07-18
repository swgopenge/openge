package protocol.swg;

import org.apache.mina.core.buffer.IoBuffer;


public class ClientVerifyAndLockNameRequest extends SWGMessage {
	
	private String name;
	private String raceTemplate;
	private String firstName;
	private String lastName;
	
	public ClientVerifyAndLockNameRequest() {
		
	}
	
	public void deserialize(IoBuffer buffer) {
		buffer.position(6); // Skips SOE and SWG opcodes
		raceTemplate = getNextAsciiString(buffer);
		name = getNextUnicodeString(buffer);
		System.out.println("msg: " + name);	
		// Gets the first and last names
		String [] splitName = name.split(" ");
		firstName = splitName[0];
		lastName  = (splitName.length == 1) ? "" : splitName[1];
	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
	}

	public String getFirstName()    { return firstName; }
	public String getLastName()     { return lastName; }
	public String getName()         { return name; }
	public String getRaceTemplate() { return raceTemplate; }
}
