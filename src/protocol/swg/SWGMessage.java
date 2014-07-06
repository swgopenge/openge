package protocol.swg;

import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;

import protocol.Message;



public abstract class SWGMessage extends Message {

	protected short	operandCount;
	
	public SWGMessage() {
		
	}
	
	public SWGMessage(IoBuffer data) { 
		super(data);
	}
}
