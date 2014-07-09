package clientdata.visitors;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.ClientFileManager;
import clientdata.VisitorInterface;

public class ObjectVisitor implements VisitorInterface {

	private Map<String, Object> attributes;
	private String name;
	
	public ObjectVisitor() {
		attributes = new HashMap<String, Object>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T)attributes.get(name);
	}
	
	public Map<String, Object> getAttributes() { return attributes; }
	
	@Override 
	public void parseData(String name, IoBuffer data, int depth, int size) throws Exception {
		if("DERVXXXX".equals(name)) {
			String file = data.getString(Charset.forName("US-ASCII").newDecoder());
			
			ObjectVisitor objI = ClientFileManager.loadFile(file, ObjectVisitor.class);
			
			if(objI != null) {
				attributes.putAll(objI.attributes);
			}
		} else if("XXXX".equals(name)) {
			String attributeName = data.getString(Charset.forName("US-ASCII").newDecoder());
			if(attributeName.equals("slotDescriptorFilename") || attributeName.equals("arrangementDescriptorFilename") || attributeName.equals("appearanceFilename") || attributeName.equals("portalLayoutFilename")) {
				if(data.get() != 0) {
					attributes.put(attributeName, data.getString(Charset.forName("US-ASCII").newDecoder()));
				}
			} else if(attributeName.equals("collisionActionBlockFlags") || attributeName.equals("collisionMaterialFlags") || attributeName.equals("collisionMaterialBlockFlags") || attributeName.equals("collisionMaterialPassFlags")
					|| attributeName.equals("collisionActionPassFlags") || attributeName.equals("collisionActionFlags") || attributeName.equals("gameObjectType") || attributeName.equals("containerVolumeLimit")) {
				byte byteVal = data.get();
				if(byteVal != 0) {
					data.skip(1); // unk byte mostly 0x20
					int value = data.getInt();
					attributes.put(attributeName, new Integer(value));
				}
			} else if(attributeName.equals("objectName")) {
				attributes.put("stfFilename", data.getString(Charset.forName("US-ASCII").newDecoder()));
				attributes.put("stfName", data.getString(Charset.forName("US-ASCII").newDecoder()));
			} else if(attributeName.equals("detailedDescription")) {
				attributes.put("detailFilename", data.getString(Charset.forName("US-ASCII").newDecoder()));
				attributes.put("detailName", data.getString(Charset.forName("US-ASCII").newDecoder()));
			} else if(attributeName.equals("speed") || attributeName.equals("scale") || attributeName.equals("turnRate")) {
				byte byteVal = data.get();
				if(byteVal != 0) {
					int value = data.getInt();
					attributes.put(attributeName, new Float(value));
				}
			}
		}
	}

	@Override 
	public void notifyFolder(String nodeName, int depth) throws Exception {
		
	}
	
	public String toString() {
		return name;
	}

	public void dispose() {
		attributes.clear();
	}
}
