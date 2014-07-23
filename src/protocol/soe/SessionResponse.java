package protocol.soe;

import org.apache.mina.core.buffer.IoBuffer;

public class SessionResponse extends SOEMessage {
	
	private int connectionId;
	private int crcSeed;
	private int crcLength;
	private EncryptionType encryption;
	private int serverUDPSize;
	private boolean useCompression;
	
	public enum EncryptionType {
		NONE,
		USERSUPPLIED,
		USERSUPPLIED2,
		XORBUFFER,
		XOR
	}
	

	public SessionResponse(int connectionId, int crcSeed, int crcLength, boolean useCompression, EncryptionType encryption, int serverUDPSize) {
		this.connectionId = connectionId;
		this.crcSeed = crcSeed;
		this.crcLength = crcLength;
		this.encryption = encryption;
		this.serverUDPSize = serverUDPSize;
		this.useCompression = useCompression;
	}

	public int size() { return -1; }


	
	public void deserialize(byte[] data) {
	}

	
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(17);
		
		result.putShort((short)2);
		result.putInt(connectionId);
		result.putInt(crcSeed);
		result.put((byte)crcLength);
		result.put((byte) (useCompression ? 1 : 0));
		result.put((byte) encryption.ordinal());
		result.putInt(serverUDPSize);

		return result.flip();
		
	}

	@Override
	public void deserialize(IoBuffer data) {
		// TODO Auto-generated method stub
		
	}




	
	

	
	
	
}
