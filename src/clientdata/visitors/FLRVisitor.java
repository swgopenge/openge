package clientdata.visitors;

import java.util.Vector;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;
import utils.scene.u3d.Triangle;

@SuppressWarnings("unused")
public class FLRVisitor implements VisitorInterface {
	
	public Vector<Vector3D> vertices = new Vector<Vector3D>();
	public Vector<CollisionTriangle> triangles = new Vector<CollisionTriangle>();

	public int version;

	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {
		
		if(nodename.length() >= 8) {
			if(nodename.substring(4, 8).equals("VERT")) {
				
				switch(String.valueOf(nodename.charAt(3))) {
			
					case "5": version = 5;
					case "6": version = 6;
					
					default: System.out.println("Unknown .flr version");

				}
				
				for(int i = size/3; i > 0; --i) {
					
					float x = data.getFloat();
					if(!data.hasRemaining())
						break;
					float y = data.getFloat();
					float z = data.getFloat();
					Vector3D vec = new Vector3D(x, y, z);
					vertices.add(vec);
				}
			}
		} else if(nodename.equals("TRIS")) {
			
			int count = 0;
			
			if(version == 5) {
				
				count = size / 60;
				
			} else if(version == 6) {
			
				count = data.getInt();
				
			} else {
				
				System.out.println("Unknown .flr version");
				
			}
			
			for(int i = 0; i < count; i++) {
				
				CollisionTriangle collisionTri = new CollisionTriangle();
				
				collisionTri.tri.a = data.getInt();
				collisionTri.tri.b = data.getInt();
				collisionTri.tri.c = data.getInt();
				int id = data.getInt();
				collisionTri.adjacentTris = new int[3];
				collisionTri.adjacentTris[0] = data.getInt();
				collisionTri.adjacentTris[1] = data.getInt();
				collisionTri.adjacentTris[2] = data.getInt();

				data.getFloat();
				data.getFloat();	// unks
				data.getFloat();
				
				collisionTri.edge1 = data.get();
				collisionTri.edge2 = data.get();
				collisionTri.edge3 = data.get();

				
				collisionTri.freeYAxis = data.get();
				collisionTri.groupNumber = data.getInt();
				
				data.getInt();
				data.getInt();	// unks
				data.getInt();
				
				triangles.add(collisionTri);
			}
			
		} 
		
		


	}

	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private class CollisionTriangle {
		
		public Triangle tri = new Triangle();
		int[] adjacentTris;
		//0 = cannot exit, 1 = can enter+exit, 2 = catches falls. Used on player structure stairs
		byte edge1, edge2, edge3;
		byte freeYAxis; // 0 = constrained to floor, 1 = free
		// isolates sections of collision floor
		int groupNumber;

	}

}
