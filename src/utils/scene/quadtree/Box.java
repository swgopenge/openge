package utils.scene.quadtree;

public class Box {
	public final float minX;
	public final float minY;
	public final float maxX;
	public final float maxY;
	public final float centreX;
	public final float centreY;

	public Box(float minX, float minY, float maxX, float maxY) {
		this.minX = Math.min(minX, maxX);
		this.minY = Math.min(minY, maxY);
		this.maxX = Math.max(minX, maxX);
		this.maxY = Math.max(minY, maxY);
		this.centreX = (minX + maxX) / 2;
		this.centreY = (minY + maxY) / 2;
	}

	public boolean contains(float x, float y) {
		return (x >= this.minX &&
				y >= this.minY &&
				x < this.maxX &&
				y < this.maxY);
	}

	public boolean containsOrEquals(Box box) {
		return (box.minX >= this.minX &&
				box.minY >= this.minY &&
				box.maxX <= this.maxX &&
				box.maxY <= this.maxY);
	}

	public Box intersection(Box r) {
		float tempX1 = this.minX;
		float tempY1 = this.minY;
		float tempX2 = this.maxX;
		float tempY2 = this.maxY;
		if (this.minX < r.minX) tempX1 = r.minX;
		if (this.minY < r.minY) tempY1 = r.minY;
		if (tempX2 > r.maxX) tempX2 = r.maxX;
		if (tempY2 > r.maxY) tempY2 = r.maxY;
		if(tempX2-tempX1 <=0.f || tempY2-tempY1 <= 0.f) return null;

		return new Box(tempX1, tempY1, tempX2, tempY2);
	}
	
	public boolean intersects(Box other) {
		if ((this.maxX-this.minX) <= 0 || (this.maxY-this.minY) <= 0) {
			return false;
		}
		return (other.maxX > this.minX &&
				other.maxY > this.minY &&
				other.minX < this.maxX &&
				other.minY < this.maxY);
	}

	public Box union(Box b) {
		return new Box( Math.min(this.minX, b.minX),
				Math.min(this.minY, b.minY),
				Math.max(this.maxX, b.maxX),
				Math.max(this.maxY, b.maxY));
	}
	
	public float calcDist(float x, float y) {
		float distanceX;
		float distanceY;

		if (this.minX <= x && x <= this.maxX) {
			distanceX = 0;
		} else {
			distanceX = Math.min(Math.abs(this.minX - x), Math.abs(this.maxX - x));
		}
		if (this.minY <= y && y <= this.maxY) {
			distanceY = 0;
		} else {
			distanceY = Math.min(Math.abs(this.minY - y), Math.abs(this.maxY - y));
		}

		return (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
	}
	
	public Box scale(float scaleX, float scaleY) {
		scaleY *= this.centreY - this.minY;
		scaleX *= this.centreX - this.minX;
		return new Box(this.minX - scaleX, this.minY-scaleY, this.maxX + scaleX, this.maxY + scaleY);
	}
	
	@Override
	public String toString() {
		return "upperLeft: (" + minX + ", " + minY + ") lowerRight: (" + maxX + ", " + maxY + ")";
	}
	
}