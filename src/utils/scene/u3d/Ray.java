package utils.scene.u3d;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_Point3d;
import utils.scene.Point3D;

public class Ray {
	
	Point3D origin = null;
    Vector3D dir = null;

    public static final float SMALL_NUM = 0.000001f;
    
    public Ray(Point3D origin, Vector3D dir) {
        this.origin = origin;
        this.dir = dir;
    }
    
    public Point3D getOrigin() {
        return origin;
    }
    
    public Vector3D getDirection() {
        return dir;
    }
    
    /**
     * Algorithm from Moller, Trumbore, "Fast, Minimum Storage
	 *  Ray / Triangle Intersection", Journal of Graphics Tools, Volume 2,
	 *  Number 1, 1997, pp. 21-28. 
    */
    public Point3D intersectsTriangle(Mesh3DTriangle triangle) {
    	
    	Vector3D vert0 = new Vector3D(triangle.getPointOne().x, triangle.getPointOne().y, triangle.getPointOne().z);
   	
    	Vector3D edge1 = triangle.getEdge1();
    	Vector3D edge2 = triangle.getEdge2();
    	
    	if(triangle.getNorm() == 0)
    		return null;

        // Begin calculating determinant -- also used to calculate U parameter
    	Vector3D pvec = getDirection().crossProduct(edge2);
        
        // If determinant is near zero, ray lies in plane of triangle
        float det = (float) edge1.dotProduct(pvec);

        if (det > -SMALL_NUM && det < SMALL_NUM)
          return null;

        float invDet = 1.0f / det;

        // Calculate distance from vert0 to ray origin
        Vector3D tvec = new Vector3D(origin.x, origin.y, origin.z).subtract(vert0);
        float a = (float) -(triangle.getN().dotProduct(tvec));
        float b = (float) triangle.getN().dotProduct(getDirection());
        
        if(a / b < 0)
        	return null;

        // Calculate U parameter and test bounds
        float u = (float) (tvec.dotProduct(pvec) * invDet);
        if (u < 0.0f || u > 1.0f)
          return null;

        // Prepare to test V parameter
        Vector3D qvec = tvec.crossProduct(edge1);

        // Calculate V parameter and test bounds
        float v = (float) (getDirection().dotProduct(qvec) * invDet);
        if (v < 0.0f || (u + v) > 1.0f)
          return null;

        // Calculate t, ray intersects triangle
        float t = (float) (edge2.dotProduct(qvec) * invDet);

        return new Point3D(t, u, v);
    }
    
    /**
     * Algorithm from Moller, Trumbore, "Fast, Minimum Storage
	 *  Ray / Triangle Intersection", Journal of Graphics Tools, Volume 2,
	 *  Number 1, 1997, pp. 21-28. 
    */
    public Point3D intersectsTriangle(Mesh3DTriangle triangle, float distance) {
    	
    	WB_Point3d closestPoint = WB_Intersection.closestPointToTriangle(new WB_Point3d(getOrigin().x, getOrigin().y, getOrigin().z), new WB_Point3d(triangle.getPointOne().x, triangle.getPointOne().y, triangle.getPointOne().z), new WB_Point3d(triangle.getPointTwo().x, triangle.getPointTwo().y, triangle.getPointTwo().z), new WB_Point3d(triangle.getPointThree().x, triangle.getPointThree().y, triangle.getPointThree().z));
    	if((origin.x - closestPoint.x) * (origin.x - closestPoint.x) + (origin.y - closestPoint.y) * (origin.y- closestPoint.y) + (origin.z - closestPoint.z) * (origin.z - closestPoint.z) > distance * distance)
    		return null;
    	
    	//if(new Point3D(getOrigin().x, getOrigin().y, getOrigin().z).getDistance(new Point3D((float) closestPoint.x, (float) closestPoint.y, (float) closestPoint.z)) > distance)
    	//	return null;
    	
    	Vector3D vert0 = new Vector3D(triangle.getPointOne().x, triangle.getPointOne().y, triangle.getPointOne().z);
       	
    	Vector3D edge1 = triangle.getEdge1();
    	Vector3D edge2 = triangle.getEdge2();
    	
    	if(triangle.getNorm() == 0)
    		return null;

        // Begin calculating determinant -- also used to calculate U parameter
    	Vector3D pvec = getDirection().crossProduct(edge2);
        
        // If determinant is near zero, ray lies in plane of triangle
        float det = (float) edge1.dotProduct(pvec);

        if (det > -SMALL_NUM && det < SMALL_NUM)
          return null;

        float invDet = 1.0f / det;

        // Calculate distance from vert0 to ray origin
        Vector3D tvec = new Vector3D(origin.x, origin.y, origin.z).subtract(vert0);
        float a = (float) -(triangle.getN().dotProduct(tvec));
        float b = (float) triangle.getN().dotProduct(getDirection());
        
        if(a / b < 0)
        	return null;

        // Calculate U parameter and test bounds
        float u = (float) (tvec.dotProduct(pvec) * invDet);
        if (u < 0.0f || u > 1.0f)
          return null;

        // Prepare to test V parameter
        Vector3D qvec = tvec.crossProduct(edge1);

        // Calculate V parameter and test bounds
        float v = (float) (getDirection().dotProduct(qvec) * invDet);
        if (v < 0.0f || (u + v) > 1.0f)
          return null;

        // Calculate t, ray intersects triangle
        float t = (float) (edge2.dotProduct(qvec) * invDet);

        return new Point3D(t, u, v);
    }


    /*public Point3D intersectsTriangle(Mesh3DTriangle triangle) {
    	
    	Point3D I = new Point3D();
        Vector3D    u, v, n;
        Vector3D    dir, w0, w, p1;
        float     r, a, b;
        
        p1 = new Vector3D(triangle.getPointOne().x, triangle.getPointOne().y, triangle.getPointOne().z);
        u = new Vector3D(triangle.getPointTwo().x, triangle.getPointTwo().y, triangle.getPointTwo().z);
        u = u.subtract(p1);
        v = new Vector3D(triangle.getPointThree().x, triangle.getPointThree().y, triangle.getPointThree().z);
        v = v.subtract(p1);
        n = Vector3D.crossProduct(u, v);
        
        if (n.getNorm() == 0) {
            return null;
        }
        
        dir = getDirection();
        w0 = new Vector3D(getOrigin().x, getOrigin().y, getOrigin().z);
        w0 = w0.subtract(p1);
        a = (float) -(new Vector3D(n.getX(), n.getY(), n.getZ()).dotProduct(w0));
        b = (float) new Vector3D(n.getX(), n.getY(), n.getZ()).dotProduct(dir);
        
        if ((float)Math.abs(b) < SMALL_NUM) {
            return null;
        }
        
        r = a / b;
        if (r < 0.0) {
            return null;
        }
        
        I = getOrigin().clone();
        I.x += r * dir.getX();
        I.y += r * dir.getY();
        I.z += r * dir.getZ();
        
        float    uu, uv, vv, wu, wv, D;
        
        uu = (float) Vector3D.dotProduct(u,u);
        uv = (float) Vector3D.dotProduct(u,v);
        vv = (float) Vector3D.dotProduct(v,v);
        w = new Vector3D(I.x - triangle.getPointOne().x, I.y - triangle.getPointOne().y, I.z - triangle.getPointOne().z);
        wu = (float) Vector3D.dotProduct(w,u);
        wv = (float) Vector3D.dotProduct(w,v);
        D = uv * uv - uu * vv;
        
        // get and test parametric coords
        float s, t;
        s = (uv * wv - vv * wu) / D;
        if (s < 0.0 || s > 1.0)         // I is outside T
            return null;
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0 || (s + t) > 1.0)  // I is outside T
            return null;
        
        return I;          // I is in T

    	
    }
    
    public Point3D intersectsTriangle(Mesh3DTriangle triangle, float distance) {

    	WB_Point3d closestPoint = WB_Intersection.closestPointToTriangle(new WB_Point3d(getOrigin().x, getOrigin().y, getOrigin().z), new WB_Point3d(triangle.getPointOne().x, triangle.getPointOne().y, triangle.getPointOne().z), new WB_Point3d(triangle.getPointTwo().x, triangle.getPointTwo().y, triangle.getPointTwo().z), new WB_Point3d(triangle.getPointThree().x, triangle.getPointThree().y, triangle.getPointThree().z));
    	if(new Point3D(getOrigin().x, getOrigin().y, getOrigin().z).getDistance(new Point3D((float) closestPoint.x, (float) closestPoint.y, (float) closestPoint.z)) > distance)
    		return null;
    	
    	Point3D I = new Point3D();
        Vector3D    u, v, n;
        Vector3D    dir, w0, w, p1;
        float     r, a, b;
        
        p1 = new Vector3D(triangle.getPointOne().x, triangle.getPointOne().y, triangle.getPointOne().z);
        u = new Vector3D(triangle.getPointTwo().x, triangle.getPointTwo().y, triangle.getPointTwo().z);
        u = u.subtract(p1);
        v = new Vector3D(triangle.getPointThree().x, triangle.getPointThree().y, triangle.getPointThree().z);
        v = v.subtract(p1);
        n = Vector3D.crossProduct(u, v);
        
        if (n.getNorm() == 0) {
            return null;
        }
        
        dir = getDirection();
        w0 = new Vector3D(getOrigin().x, getOrigin().y, getOrigin().z);
        w0 = w0.subtract(p1);
        a = (float) -(new Vector3D(n.getX(), n.getY(), n.getZ()).dotProduct(w0));
        b = (float) new Vector3D(n.getX(), n.getY(), n.getZ()).dotProduct(dir);
        
        if ((float)Math.abs(b) < SMALL_NUM) {
            return null;
        }
        
        r = a / b;
        if (r < 0.0) {
            return null;
        }
        
        I = getOrigin().clone();
        I.x += r * dir.getX();
        I.y += r * dir.getY();
        I.z += r * dir.getZ();
        
        float    uu, uv, vv, wu, wv, D;
        
        uu = (float) Vector3D.dotProduct(u,u);
        uv = (float) Vector3D.dotProduct(u,v);
        vv = (float) Vector3D.dotProduct(v,v);
        w = new Vector3D(I.x - triangle.getPointOne().x, I.y - triangle.getPointOne().y, I.z - triangle.getPointOne().z);
        wu = (float) Vector3D.dotProduct(w,u);
        wv = (float) Vector3D.dotProduct(w,v);
        D = uv * uv - uu * vv;
        
        // get and test parametric coords
        float s, t;
        s = (uv * wv - vv * wu) / D;
        if (s < 0.0 || s > 1.0)         // I is outside T
            return null;
        t = (uv * wu - uu * wv) / D;
        if (t < 0.0 || (s + t) > 1.0)  // I is outside T
            return null;
        
        return I;          // I is in T

    	
    }*/

}
