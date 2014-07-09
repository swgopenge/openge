package clientdata.visitors;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;

public class SlotArrangementVisitor implements VisitorInterface {
	
	protected List<List<Integer>> slots_used;
	private CharsetDecoder charsetDecoder;
	
	public SlotArrangementVisitor() {
		slots_used = new ArrayList<List<Integer>>();
		charsetDecoder = Charset.forName("US-ASCII").newDecoder();
	}
	
	public List<List<Integer>> getArrangement() {
		charsetDecoder = null;
		return slots_used;
	}
	
	@Override 
	public void parseData(String name, IoBuffer data, int depth, int size) {
		try {
			List<Integer> innerList = new ArrayList<Integer>();
			while(data.hasRemaining()) {
				String c = data.getString(charsetDecoder);
				charsetDecoder.reset();
				
				innerList.add(c.hashCode());
			}
			slots_used.add(innerList);
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
	}

	@Override 
	public void notifyFolder(String nodeName, int depth) {
		//We don't really care about this one.
	}

}
