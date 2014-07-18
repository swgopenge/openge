package protocol.swg;

import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;


public class ClientPermissionsMessage extends SWGMessage {
	
	private int characterSlotsOpen;

	public ClientPermissionsMessage(int characterSlotsOpen) {
		if(characterSlotsOpen < 0)
			characterSlotsOpen = 0;
		this.characterSlotsOpen = characterSlotsOpen;
		operandCount = 5;
		opcode = 0xE00730E5;
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(10).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort(operandCount);
		result.putInt(opcode);
		result.put((byte) 1);
		result.put((byte) characterSlotsOpen);
		result.put((byte) 0);
		result.put((byte) 1);

		return result.flip();
	}
}
