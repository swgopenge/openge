package utils.scene;

import java.io.Serializable;

public class Quaternion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public float w, x, y, z;
	
	public Quaternion() { }

	public Quaternion(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Quaternion clone() {
		return new Quaternion(w, x, y, z);
	}
	
}
