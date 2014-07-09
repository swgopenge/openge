package clientdata.visitors;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;

public class LevelOfDetailVisitor implements VisitorInterface {
	
	public Map<Integer, LodChild> children = new HashMap<Integer, LodChild>();
	public String collisionFilename;
	private LodChild firstChild;
	private String firstMesh = null;
	
	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {

		if(nodename.equals("INFO")) {
			
			int childNumber = data.getInt();

			if(children.containsKey(childNumber)) {
				
				children.get(childNumber).in_range = data.getFloat();
				children.get(childNumber).out_range = data.getFloat();

			}
			
			else {
				
				LodChild lodChild = new LodChild();
				
				lodChild.in_range = data.getFloat();
				lodChild.out_range = data.getFloat();

				children.put(childNumber, lodChild);
				
			}
			
		} else if(nodename.contains("CHLD")) {
			
			int childNumber = data.getInt();

			if(children.containsKey(childNumber)) {
				
				children.get(childNumber).meshFilename = data.getString(Charset.forName("US-ASCII").newDecoder());
				if(firstMesh == null && !children.get(childNumber).meshFilename.contains(".cmp"))
					firstMesh = children.get(childNumber).meshFilename;

			}
			
			else {
				LodChild lodChild = new LodChild();
				lodChild.meshFilename = data.getString(Charset.forName("US-ASCII").newDecoder());
				
				if(firstMesh == null && !lodChild.meshFilename.contains(".cmp"))
					firstMesh = lodChild.meshFilename;
				

				children.put(childNumber, lodChild);

			}
			

		} else if(nodename.equals("FLORDATA")) {
			
			if(data.get() == 1) {
				
				collisionFilename = data.getString(Charset.forName("US-ASCII").newDecoder());

			}
			
		}
		
		
		
		
	}

	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public String getFirstMesh() {

		/*if(children.get(1) != null) {
			//System.out.println(children.get(1).meshFilename);
			return children.get(1).meshFilename;
		}*/
		if(firstMesh == null)
			return null;
		
		if(!firstMesh.endsWith("l0.msh")) {
			String correctMesh = firstMesh.replace(firstMesh.substring(firstMesh.length() - 6, firstMesh.length()), "l0.msh");
			//System.out.println(correctMesh);
			//File file = new File(correctMesh);
			//if(file.exists())
			firstMesh = correctMesh;
		}
		return firstMesh;
		
	}

	private class LodChild {
		
		float in_range;
		float out_range;
		String meshFilename;
	}

}
