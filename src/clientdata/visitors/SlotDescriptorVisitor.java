package clientdata.visitors;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.ClientFileManager;
import clientdata.VisitorInterface;
import clientdata.visitors.SlotDefinitionVisitor.SlotDefinition;

public class SlotDescriptorVisitor implements VisitorInterface {

	protected Map<Integer, Integer> slotLookup;
	protected List<String> availableSlots;
	
	private CharsetDecoder charsetDecoder;
	
	public SlotDescriptorVisitor() throws InstantiationException, IllegalAccessException {
		availableSlots = new ArrayList<String>();
		slotLookup = new HashMap<Integer, Integer>();
		charsetDecoder = Charset.forName("US-ASCII").newDecoder();
		
		int size = 0;
		SlotDefinitionVisitor defs = ClientFileManager.loadFile("abstract/slot/slot_definition/slot_definitions.iff", SlotDefinitionVisitor.class);
		for(SlotDefinition def : defs.getDefinitions().values()) {
				if(def.global == 1) {
					availableSlots.add(def.slotName);
					slotLookup.put(def.slotName.hashCode(), size);
					++size;
				}
			}
	}
	
	public List<String> getAvailableSlots() {
		return availableSlots;
	}
	
	public Integer getIndexOf(Integer slotNameHash) {
		return slotLookup.get(slotNameHash);
	}
	
	@Override 
	public void parseData(String name, IoBuffer data, int depth, int totalSize) {
		try {
			int size = availableSlots.size();
			while(data.hasRemaining()) {
				//Read in the name
				String slot_name = data.getString(charsetDecoder);
				charsetDecoder.reset();
				
				//Insert it into the lookups
				availableSlots.add(slot_name);
				slotLookup.put(slot_name.hashCode(), size);
				
				//Advance
				++size;
			}
		} catch (CharacterCodingException e) {
			e.printStackTrace();
		}
	}

	@Override 
	public void notifyFolder(String nodeName, int depth) {
		//We don't really care about this one.
	}

}
