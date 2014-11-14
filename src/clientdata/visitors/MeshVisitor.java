package clientdata.visitors;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.mina.core.buffer.IoBuffer;

import toxi.geom.Vec3D;
import toxi.geom.mesh.TriangleMesh;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point3d;
import wblut.geom.WB_Triangle;
import wblut.hemesh.HEC_FromTriangles;
import wblut.hemesh.HE_Mesh;
import clientdata.VisitorInterface;
import utils.scene.u3d.Mesh3DTriangle;
import utils.scene.u3d.Triangle;
import utils.scene.Point3D;
import utils.unsafe.OffHeapMemory;

@SuppressWarnings("unused")
public class MeshVisitor implements VisitorInterface {

	public Box box = new Box();
	public Sphere sphere = new Sphere();
	public int vertexCount;
	public String collisionFilename;
	public Vector<Vertex> vertices = new Vector<Vertex>();
	public Vector<Triangle> tris = new Vector<Triangle>();
	public MeshData currentMesh;
	public Vector<MeshData> meshes = new Vector<MeshData>();
	private WB_AABBTree aabbTree;
	private List<Mesh3DTriangle> triangles;

	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {

		if(nodename.equals("BOX")) {
			// useless data
			//box.point1 = new Vector3D(data.getFloat(), data.getFloat(), data.getFloat());
			//box.point2 = new Vector3D(data.getFloat(), data.getFloat(), data.getFloat());

		} else if(nodename.contains("NAME")) {
			
			currentMesh = new MeshData();	// meshdata starts at name and ends at indx
			meshes.add(currentMesh);
			
		} else if(nodename.equals("0001SPHR")) {
			// useless data
			//sphere.center = new Vector3D(data.getFloat(), data.getFloat(), data.getFloat());
			//sphere.radius = data.getFloat();
			
		} else if(nodename.equals("0003INFO")) {
			
			data.getInt();
			vertexCount = data.getInt();
			
		} else if(nodename.equals("FLORDATA")) {
			
			if(data.get() == 1) {
				collisionFilename = data.getString(Charset.forName("US-ASCII").newDecoder());
			}
			
		} else if(nodename.equals("DATA")) {
			
			int bytesPerVertex = ((size / vertexCount) - 24) / 4;
			
			boolean hasColor = bytesPerVertex % 2 == 1;
			
			for(int i = 0; i < vertexCount; i++) {
				
				Vertex v = new Vertex();
				v.position = new Vector3D(data.getFloat(), data.getFloat(), data.getFloat());
				// useless data
				/*
				//v.normal = new Vector3D(data.getFloat(), data.getFloat(), data.getFloat());

				if(hasColor)
					v.color = data.getInt();
				
				for(int j = bytesPerVertex / 2; j > 0; --j) {
					
					UVPair uvPair = new UVPair();
					uvPair.x = data.getFloat();
					uvPair.y = data.getFloat();
					//v.uvs.add(uvPair);
				}*/
				currentMesh.vertices.add(v);
			}
			
		} else if(nodename.equals("INDX")) {
			
			int count = data.getInt();

			int bytesPerIndex = (size - 4) / count;
			
			for(int i = 1; i <= count; i += 3) {
				
				Triangle tri = (Triangle) OffHeapMemory.allocateObject(new Triangle()).object;
				
				if(bytesPerIndex == 4) {
					
					tri.a = data.getInt();
					tri.b = data.getInt();
					tri.c = data.getInt();

				} else if(bytesPerIndex == 2) {
					
					tri.a = data.getShort();
					tri.b = data.getShort();
					tri.c = data.getShort();

				}
				
				currentMesh.triangles.add(tri);
			}

		} else if(nodename.contains("CNT")) {
			
			//int count = data.getInt();
			
		}
		
	}
	
	public HE_Mesh createMesh() {
		
		if(meshes.isEmpty())
			return null;
		
		Vector<MeshTriangle> tris = new Vector<MeshTriangle>();
		int totalTris = 0;
		for(MeshData meshData : meshes) {
			totalTris += meshData.vertices.size() / 3;
			for(Triangle tri : meshData.triangles) {
									
				Vertex vert1 = meshData.vertices.get(tri.a);
				Vertex vert2 = meshData.vertices.get(tri.b);
				Vertex vert3 = meshData.vertices.get(tri.c);
				
				MeshTriangle meshTri = new MeshTriangle();
				meshTri.vertex1 = vert1;
				meshTri.vertex2 = vert2;
				meshTri.vertex3 = vert3;
				
				tris.add(meshTri);

			}
			
		}
		
		if(tris.isEmpty())
			return null;
		//System.out.println("Parsed tricount: " + totalTris);
		HEC_FromTriangles<MeshTriangle> creator = new HEC_FromTriangles<MeshTriangle>();
		creator.setTriangles(tris);
		//creator.
		return creator.create();
	}
	
	public TriangleMesh createMesh2() {
		
		TriangleMesh mesh = new TriangleMesh();
		
		if(meshes.isEmpty())
			return null;

		for(MeshData meshData : meshes) {
			
			for(Triangle tri : meshData.triangles) {
				
				if(tri.a >= meshData.vertices.size() || tri.b >= meshData.vertices.size() || tri.c >= meshData.vertices.size())
					continue;
					
				Vertex vert1 = meshData.vertices.get(tri.a);
				Vertex vert2 = meshData.vertices.get(tri.b);
				Vertex vert3 = meshData.vertices.get(tri.c);
				Vec3D vertex1 = new Vec3D();
				Vec3D vertex2 = new Vec3D();
				Vec3D vertex3 = new Vec3D();

				vertex1.x = (float) vert1.position.getX();
				vertex1.y = (float) vert1.position.getY();
				vertex1.z = (float) vert1.position.getZ();
				
				vertex2.x = (float) vert2.position.getX();
				vertex2.y = (float) vert2.position.getY();
				vertex2.z = (float) vert2.position.getZ();

				vertex3.x = (float) vert3.position.getX();
				vertex3.y = (float) vert3.position.getY();
				vertex3.z = (float) vert3.position.getZ();

				mesh.addFace(vertex1, vertex2, vertex3);
			}
			
		}
		
		
		return mesh;

	}
	
	public List<Mesh3DTriangle> getTriangles() {
		
		if(this.triangles == null) {
			List<Mesh3DTriangle> triangles = new ArrayList<Mesh3DTriangle>();
			
			for(MeshData meshData : meshes) {
				
				for(Triangle tri : meshData.triangles) {
					
					if(tri.a >= meshData.vertices.size() || tri.b >= meshData.vertices.size() || tri.c >= meshData.vertices.size())
						continue;
						
					Vertex vert1 = meshData.vertices.get(tri.a);
					Vertex vert2 = meshData.vertices.get(tri.b);
					Vertex vert3 = meshData.vertices.get(tri.c);
					Point3D vertex1 = new Point3D();
					Point3D vertex2 = new Point3D();
					Point3D vertex3 = new Point3D();
	
					vertex1.x = (float) vert1.position.getX();
					vertex1.y = (float) vert1.position.getY();
					vertex1.z = (float) vert1.position.getZ();
					
					vertex2.x = (float) vert2.position.getX();
					vertex2.y = (float) vert2.position.getY();
					vertex2.z = (float) vert2.position.getZ();
	
					vertex3.x = (float) vert3.position.getX();
					vertex3.y = (float) vert3.position.getY();
					vertex3.z = (float) vert3.position.getZ();
	
					Mesh3DTriangle triangle = new Mesh3DTriangle(vertex1, vertex2, vertex3);
					triangles.add(triangle);
				}
				
			}
			this.triangles = triangles;
		}
		return triangles;
	}
	
	public void createAABBTree(HE_Mesh mesh) {
		if(mesh == null)
			return;
		aabbTree = new WB_AABBTree(mesh, 100);
	}
	
	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public WB_AABBTree getAABBTree() {
		return aabbTree;
	}

	// used to link 3 vertices together by using the triangles as indexes for the vector 
	private class MeshData {
		
		public Vector<Triangle> triangles = new Vector<Triangle>();
		public Vector<Vertex> vertices = new Vector<Vertex>();

	}

	private class MeshTriangle implements WB_Triangle {
		
		public Vertex vertex1;
		public Vertex vertex2;
		public Vertex vertex3;
		@Override
		public WB_Point3d getBarycentric(WB_Point3d arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public WB_Point3d getCenter() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public WB_Point3d getCentroid() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public WB_Point3d getCircumcenter() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public WB_Point3d getOrthocenter() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public WB_Plane getPlane() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public WB_Point3d getPointFromBarycentric(double arg0, double arg1,
				double arg2) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public WB_Point3d getPointFromTrilinear(double arg0, double arg1,
				double arg2) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public WB_Point3d p1() {
			return new WB_Point3d(vertex1.position.getX(), vertex1.position.getY(), vertex1.position.getZ());
		}
		@Override
		public WB_Point3d p2() {
			return new WB_Point3d(vertex2.position.getX(), vertex2.position.getY(), vertex2.position.getZ());
		}
		@Override
		public WB_Point3d p3() {
			return new WB_Point3d(vertex3.position.getX(), vertex3.position.getY(), vertex3.position.getZ());
		}
		
	}
	
	private class Box {
		
		public Vector3D point1;
		public Vector3D point2;

	}
	
	private class Sphere {
		
		public Vector3D center;
		public float radius;
		
	}
	
	private class Vertex {
		
		public Vector3D position;
		public Vector3D normal;
		public int color;
		public Vector<UVPair> uvs = new Vector<UVPair>();
		
	}
	
	private class UVPair {
		
		public float x, y;
	}
	
	
}
