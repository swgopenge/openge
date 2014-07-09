package utils.scene;

import java.io.Serializable;
//import resources.objects.cell.CellObject;

public class Point3D implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public float z;
	public float x;
	public float y;
	//private transient CellObject cell;
	
	public Point3D() { }

	public Point3D(float x, float y, float z) {
		
		this.x = x;
		this.y = y;
		this.z = z;
		
	}
	
	public Point3D clone() {
		
		return new Point3D(x, y, z);
		
	}
	
	public float getDistance(Point3D target) {
		
		return (float)Math.sqrt(
			Math.pow(x - target.x, 2) + 
			Math.pow(y - target.y, 2) +
			Math.pow(z - target.z, 2));
		
	}

	public float getDistance2D(Point3D target) {
		
		return (float)Math.sqrt(
				Math.pow(x - target.x, 2) + 
				Math.pow(z - target.z, 2));
		
	}

	/*public CellObject getCell() {
		return cell;
	}

	public void setCell(CellObject cell) {
		this.cell = cell;
	}

	public Point3D getWorldPosition() {
		
		if(cell == null)
			return this;
		
		Point3D cellPos = this;
		Point3D buildingPos = cell.getContainer().getPosition();
		float length = (float) Math.sqrt(cellPos.x * cellPos.x + cellPos.z * cellPos.z);
		float angle = (float) (cell.getContainer().getRadians() + Math.atan2(cellPos.x, cellPos.z));
		return new Point3D(buildingPos.x + (float) (Math.sin(angle) * length), buildingPos.y + cellPos.y,  buildingPos.z + (float) (Math.cos(angle) * length));

	}*/

}
