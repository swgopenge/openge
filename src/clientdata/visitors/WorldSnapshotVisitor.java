package clientdata.visitors;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;

public class WorldSnapshotVisitor implements VisitorInterface {

	private Map<Integer, String> names = new HashMap<Integer, String>();
	private Vector<SnapshotChunk> chunks = new Vector<SnapshotChunk>();
	
	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {

		if(nodename.equals("OTNL")) {
			int counter = data.getInt();
			//System.out.println(counter);
			for(int i = 0; i < counter; i++) {
				names.put(i, data.getString(Charset.forName("US-ASCII").newDecoder()));
				//System.out.println(names.get(i));
			}
		} else if(nodename.equals("0000DATA")) {
			SnapshotChunk chunk = new SnapshotChunk();
			chunk.id = data.getInt();
			chunk.parentId = data.getInt();
			chunk.nameId = data.getInt();
			chunk.cellNumber = data.getInt();
			chunk.orientationW = data.getFloat();
			chunk.orientationX = data.getFloat();
			chunk.orientationY = data.getFloat();
			chunk.orientationZ = data.getFloat();
			chunk.xPosition = data.getFloat();
			chunk.yPosition = data.getFloat();	// IMPORTANT: This uses SWG's weird coordinate system where y is height!
			chunk.zPosition = data.getFloat();
			chunk.gameObjectType = data.getInt(); // could also be something else but the ints are consistent for same object types
			chunk.PortalCRC = data.getInt(); // CRC to the .pob File of the object, used for interiors of buildings/structures
			chunks.add(chunk);
		} else {
			System.out.println("Error: Unhandled World Snapshot Chunk!");
		}
		
	}
	
	public String getName(int nameId) {
		return names.get(nameId);
	}
	
	public Vector<SnapshotChunk> getChunks() { return chunks; }
	
	public void dispose() {
		names = null;
		chunks = null;
	}

	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public class SnapshotChunk {
		
		public int id;
		public int parentId;
		public int nameId;
		public int cellNumber;
		public float orientationX;
		public float orientationY;
		public float orientationZ;
		public float orientationW;
		public float xPosition;
		public float yPosition;
		public float zPosition;
		public int gameObjectType;
		public int PortalCRC;
		

	}
	
	public float getHeight(float x, float z) {
		// TODO Auto-generated method stub
		return 1;
	}

	public boolean isWater(float x, float z) {
		// TODO Auto-generated method stub
		return false;
	}
}
