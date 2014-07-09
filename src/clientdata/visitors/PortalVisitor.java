package clientdata.visitors;

import java.nio.charset.Charset;
import java.util.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;
import utils.scene.u3d.Triangle;

public class PortalVisitor implements VisitorInterface {
	
	public int cellCount, portalCount;
	public Vector<Portal> portals = new Vector<Portal>();
	public Vector<Cell> cells = new Vector<Cell>();

	@Override
	@SuppressWarnings("unused")
	public void parseData(String nodename, IoBuffer data, int depth, int totalSize) throws Exception {

		if(nodename.equals("0003DATA")) {
			
			int size = data.getInt(); // number of portals
			int size2 = data.getInt(); // number of cells
			
		} else if(nodename.equals("PRTL")) {
			
			Portal portal = new Portal();
			
			int size = data.getInt();
			
			for(int i = 0; i < size; i++) {
				
				float x = data.getFloat();
				float y = data.getFloat();
				float z = data.getFloat();
				Vector3D vec = new Vector3D(x, y, z);
				portal.vertices.add(vec);
				
			}
			
			portals.add(portal);
			
			
		} else if(nodename.equals("0005DATA")) {
			
			Cell cell = new Cell();
			data.getInt(); // size
			cell.unkFlag2 = data.get();
			cell.name = data.getString(Charset.forName("US-ASCII").newDecoder());
			
			cell.mesh = data.getString(Charset.forName("US-ASCII").newDecoder());
			cell.unkFlag3 = data.get();
			cell.collision = data.getString(Charset.forName("US-ASCII").newDecoder());
			cells.add(cell);
			
		} else if(nodename.equals("0000VERT")) {
			
			if(!(cells.size() > 0)) 
				return;
			
			Cell cell = cells.get(cells.size() - 1);
			
			int count = totalSize / 12;
			
			for(int i = 0; i < count; i++) {
				
				if(!data.hasRemaining())
					return;
				float x = data.getFloat();
				float y = data.getFloat();
				float z = data.getFloat();
				Vector3D vec = new Vector3D(x, y, z);

				cell.vertices.add(vec);
			}
			
		} else if(nodename.equals("INDX")) {
			
			if(!(cells.size() > 0)) 
				return;
			
			Cell cell = cells.get(cells.size() - 1);
			
			int count = totalSize / 12;

			for(int i = 0; i < count; i++) {
				if(!data.hasRemaining())
					return;

				Triangle tri = new Triangle();

				tri.a = data.getInt();
				tri.b = data.getInt();
				tri.c = data.getInt();

				cell.triangles.add(tri);
			}

		} else if(nodename.equals("PRTL0004")) {
			
			if(!(cells.size() > 0)) 
				return;
			
			Cell cell = cells.get(cells.size() - 1);

			Link link = new Link();
			
			data.get(); // some unk
			
			link.portalId = data.getInt();
			link.unkFlag2 = data.get();
			link.dst_cellId = data.getInt();
			
			cell.links.add(link);
			
		}
		
		

		
		
		
	}

	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	private class Portal {
		
		public Vector<Vector3D> vertices = new Vector<Vector3D>();
		
	}
	
	/**
	 * 
	 * Class to link Portals and Cells
	 *
	 */
	@SuppressWarnings("unused")
	private class Link {
		
		public int portalId;
		public byte unkFlag2;
		public int dst_cellId;
		// we dont need the data below here
		public String doorname;
		public Vector3D bitmask;
		public float unk2;
		public Vector3D bitmask2;
		public float unk3;
		public Vector3D bitmask3;
		public float unk4;

	}
	
	public class Cell {
		
		public byte unkFlag2;
		public String name;
		public byte unkFlag3;
		public String mesh;
		public byte unkFlag4;
		public String collision;
		public Vector<Vector3D> vertices = new Vector<Vector3D>();
		public Vector<Triangle> triangles = new Vector<Triangle>();
		public Vector<Link> links = new Vector<Link>();
		
	}

}
