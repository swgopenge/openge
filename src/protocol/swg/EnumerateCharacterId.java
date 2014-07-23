package protocol.swg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;


public class EnumerateCharacterId extends SWGMessage {
	
	private byte[] characters;
	private int characterCount = 0;
	
	public enum CharacterType {
		INVALID,
		NONE,
		JEDI,
		SPECTRAL
	}
	
	public EnumerateCharacterId() {
		IoBuffer result = IoBuffer.allocate(6).order(ByteOrder.LITTLE_ENDIAN);
		
		result.putShort((short)2);
		result.putInt(0x65EA4574);
		
		data = result;
	}
	
	public void deserialize(IoBuffer data) {
		
	}
	
	public IoBuffer serialize() {
		if (characters == null) {
			IoBuffer result = IoBuffer.allocate(9 + data.array().length).order(ByteOrder.LITTLE_ENDIAN);
			result.put(data);
			
			result.putInt(0);
			result.put((byte) 0);
			result.flip();

			return result;
		} else {
			IoBuffer result = IoBuffer.allocate(4 + data.array().length + characters.length).order(ByteOrder.LITTLE_ENDIAN);
			result.put(data);
			result.putInt(characterCount);
			result.put(characters);
			result.flip();
			return result;
		}
		
	}
	
	public int getSize() {
		return (data == null) ? 0 : data.array().length + characters.length + 4;
	}
	
	public void addCharacter(String character, int speciesCRC, long characterID, int galaxyID, CharacterType type) {
		IoBuffer result = IoBuffer.allocate(24 + character.length() * 2).order(ByteOrder.LITTLE_ENDIAN);
		
		result.put(getUnicodeString(character));
		result.putInt(speciesCRC);
		result.putLong(characterID);
		result.putInt(galaxyID);
		result.putInt(type.ordinal());
		result.flip();
		
		if (characters == null)
			characters = result.array();
		else
			characters = IoBuffer.allocate(characters.length + result.capacity())
			.put(characters)
			.put(result.array())
			.flip()
			.array();
		characterCount++;
	}
}
