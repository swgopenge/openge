package protocol.soe;

import java.nio.ByteBuffer;
import java.util.Vector;

import org.apache.mina.core.buffer.IoBuffer;

import protocol.swg.SWGMessage;


public class FragmentedChannelA extends SOEMessage implements ICombinable, ISequenced {

	private short sequence;
	private int length;
	private IoBuffer swgBuffer;
	
	public FragmentedChannelA() { }
	public FragmentedChannelA(IoBuffer data) {
		
		super(data);
		if(data.array().length < 8)
			return;
		sequence = data.getShort(2);
		length = data.getInt(4);
		
	}
	
	public FragmentedChannelA[] create(SWGMessage message) {
		return create(message.serialize().array());
	}
	
	public FragmentedChannelA [] create(byte [] message) {
		ByteBuffer buffer = ByteBuffer.wrap(message);
		Vector<FragmentedChannelA> fragChannelAs = new Vector<FragmentedChannelA>();
		
		while (buffer.remaining() > 0)
			fragChannelAs.add(createSegment(buffer));
		
		return fragChannelAs.toArray(new FragmentedChannelA[fragChannelAs.size()]);
	}
	
	private FragmentedChannelA createSegment(ByteBuffer buffer) {
		IoBuffer message = IoBuffer.allocate(Math.min(buffer.remaining() + 4, 493));
		
		message.putShort((short)13);
		message.putShort((short)0);
		if (buffer.position() == 0)
			message.putInt(buffer.capacity());
		byte[] messageData = new byte[message.remaining()];
		buffer.get(messageData, 0, message.remaining());
		
		message.put(messageData);
		message.flip();
		return new FragmentedChannelA(message);
	}
	
	@Override
	public void deserialize(IoBuffer data) {
		
	}
	
	public int getSize() { return data.array().length; }
	
	public short getSequence() { return sequence; }
	
	public boolean isComplete() { return length == data.array().length; }
	public int getOpcode() { return 0x0D; }
	
	public void setSequence(short sequence) {
		this.sequence = sequence;		
	}
	
	@Override
	public IoBuffer serialize() {
		if(data.array().length < 2)
			return data;
		data.putShort(2, sequence);
		return data;
	}
	
}
