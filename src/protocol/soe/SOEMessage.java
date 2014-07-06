package protocol.soe;

import java.nio.ByteBuffer;

import org.apache.mina.core.buffer.IoBuffer;

import protocol.Message;


public abstract class SOEMessage extends Message {

	public SOEMessage(IoBuffer data) {
		super(data);
		
	}
	public SOEMessage() { 
		
//		super(new byte[] { }); 
		
	}
		
	
}
