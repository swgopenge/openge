package protocol.swg;

import org.apache.mina.core.buffer.IoBuffer;


public class SelectCharacter extends SWGMessage {

	private long characterId;
	
	public SelectCharacter() {
		
	}
	
	public void deserialize(IoBuffer buffer) {
		buffer.position(6);
		characterId = buffer.getLong();
	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
	}

	public long getCharacterId() { return characterId; }
}
