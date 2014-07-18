package protocol.swg;

import org.apache.mina.core.buffer.IoBuffer;


public class CreateCharacterFailed extends SWGMessage {

	//TODO: research packet struct
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		return IoBuffer.allocate(0);
	}
}
