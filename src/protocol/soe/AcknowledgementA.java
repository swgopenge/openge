package protocol.soe;

import java.lang.reflect.Array;

import org.apache.mina.core.buffer.IoBuffer;

public class AcknowledgementA extends SOEMessage implements ICombinable {
	
	
	
	public void setSequence(short sequence) { this.sequence = sequence; }
	
	
	public int getSize() { return 4; }
	public int getOpcode() { return (short) 21; }
	public short getSequence() { return Array.getShort(data, 2); }
	
	@Override
	public void deserialize(IoBuffer data) {
		
	}
	
	@Override
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(4);
		result.putShort((short)21);
		result.putShort((short)sequence);
		result.flip();
		System.out.println(result.getHexDump());

		return result;
	}
	
}
