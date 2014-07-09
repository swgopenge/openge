package utils.scene;

public class Point2D {
	
	public float x, z;
	
	public Point2D(float x, float z) {
		
		this.x = x;
		this.z = z;
		
	}
	
	public Point2D clone() {
		
		return new Point2D(x, z);
		
	}
	
	public float getDistance(Point2D target) {
		
		return (float)Math.sqrt(
			Math.pow(x - target.x, 2) + 
			Math.pow(z - target.z, 2));
		
	}

}
