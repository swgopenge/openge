package utils.scene.u3d;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import utils.scene.Point3D;

public class Mesh3DTriangle {
	
    private Point3D[] points = new Point3D[3];
    private Vector3D edge1;
    private Vector3D edge2;
    private Vector3D n;
    private double norm;
   
    public Mesh3DTriangle(Point3D point1, Point3D point2, Point3D point3) {
        points[0] = point1;
        points[1] = point2;
        points[2] = point3;
    	Vector3D vert0 = new Vector3D(getPointOne().x, getPointOne().y, getPointOne().z);
    	Vector3D vert1 = new Vector3D(getPointTwo().x, getPointTwo().y, getPointTwo().z);
    	Vector3D vert2 = new Vector3D(getPointThree().x, getPointThree().y, getPointThree().z);
    	edge1 = vert1.subtract(vert0);
    	edge2 = vert2.subtract(vert0);
    	n = Vector3D.crossProduct(edge1, edge2);
    	setNorm(n.getNorm());
    }
    
    public Point3D getPointOne() {
        return points[0];
    }
    
    public Point3D getPointTwo() {
        return points[1];
    }
    
    public Point3D getPointThree() {
        return points[2];
    }

	public Vector3D getEdge1() {
		return edge1;
	}

	public void setEdge1(Vector3D edge1) {
		this.edge1 = edge1;
	}

	public Vector3D getEdge2() {
		return edge2;
	}

	public void setEdge2(Vector3D edge2) {
		this.edge2 = edge2;
	}

	public Vector3D getN() {
		return n;
	}

	public void setN(Vector3D n) {
		this.n = n;
	}

	public double getNorm() {
		return norm;
	}

	public void setNorm(double norm) {
		this.norm = norm;
	}
    
}
