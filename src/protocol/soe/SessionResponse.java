package protocol.soe;

import org.apache.mina.core.buffer.IoBuffer;

public class SessionResponse extends SOEMessage {
	
	private int connectionId;
	private int crcSeed;
	private int crcLength;
	private int encryptionFlag;
	private int serverUDPSize;
	

	public SessionResponse(int connectionId, int crcSeed, int crcLength, int encryptionFlag, int serverUDPSize) {
		
		this.connectionId = connectionId;
		this.crcSeed = crcSeed;
		this.crcLength = crcLength;
		this.encryptionFlag = encryptionFlag;
		this.serverUDPSize = serverUDPSize;

		
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
		result.put((byte) 1);
		result.put((byte) 4);
		result.putInt(serverUDPSize);

		return result.flip();
		
	}

	@Override
	public void deserialize(IoBuffer data) {
		// TODO Auto-generated method stub
		
	}




	
	

	
	
	
}
