package protocol.swg;

import org.apache.mina.core.buffer.IoBuffer;

public class DeleteCharacterMessage extends SWGMessage {

	
	private int galaxyId;
	private long charId;


	public DeleteCharacterMessage() {
				
		
	}
	
	public int getgalaxyId() { return galaxyId; }
	public long getcharId() { return charId; }
	
	public void deserialize(IoBuffer data) {
		
		data.position(6);
		galaxyId = data.getInt();
		charId = data.getLong();

	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
		
	}
}
