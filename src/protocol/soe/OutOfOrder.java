package protocol.soe;

import java.lang.reflect.Array;
import org.apache.mina.core.buffer.IoBuffer;

public class OutOfOrder extends SOEMessage{

	private short sequence;


	public OutOfOrder(short sequence) {
		
		this.sequence = sequence;
		
	}

	public OutOfOrder(IoBuffer data) { 
		super(data); 
		}
	
	
	public void deserialize(IoBuffer data) {
		
	}
	
	@Override
	public IoBuffer serialize() {
		IoBuffer result = IoBuffer.allocate(4);
		
		result.putShort((short)17);
		result.putShort((short)sequence);
		
		result.flip();
		return result;
	}
	
	public int getOpcode() { return (short) 17; }
}
