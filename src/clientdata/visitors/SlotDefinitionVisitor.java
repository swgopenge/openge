package clientdata.visitors;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;

public class SlotDefinitionVisitor implements VisitorInterface {

	public static class SlotDefinition {
		public String slotName;
		public byte global;
		public byte canMod;
		public byte exclusive;
		public String hardpointName;
		public int unk1;
	}
	
	public SlotDefinitionVisitor() {
		definitions = new HashMap<String, SlotDefinition>();
	}
	
	private Map<String, SlotDefinition> definitions;
	
	public Map<String, SlotDefinition> getDefinitions() {
		return definitions;
	}
	
	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {
		if(nodename.endsWith("DATA")) {
			CharsetDecoder cd = Charset.forName("US-ASCII").newDecoder();
			while(data.hasRemaining()) {
				SlotDefinition next = new SlotDefinition();
				
				next.slotName = data.getString(cd);
				cd.reset();
				
				next.global = data.get();
				next.canMod = data.get();
				next.exclusive = data.get();
				
				next.hardpointName = data.getString(cd);
				cd.reset();
				
				next.unk1 = data.getInt();
				
				definitions.put(next.slotName, next);
			}
		}
	}
	
	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {}

}
