package protocol.swg;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;
import utils.Opcodes;


public class CreateCharacterSuccess extends SWGMessage {

	
	private long charId;

	public CreateCharacterSuccess(long charId) {
		this.charId = charId;
	}
	
	public void deserialize(IoBuffer data) {
		
	}

	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(14).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short) 2);
		result.putInt(Opcodes.CreateCharacterSuccess);
		result.putLong(charId);
		result.flip();
		return result;
	}
}
