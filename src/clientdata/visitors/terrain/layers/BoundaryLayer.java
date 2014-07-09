package clientdata.visitors.terrain.layers;

import clientdata.visitors.TerrainVisitor;

public abstract class BoundaryLayer extends Layer {
	protected int feather_type;
	protected float feather_amount;
	
	public int getFeatherType() {
		return feather_type;
	}
	public void setFeatherType(int featherType) {
		this.feather_type = featherType;
	}
	
	public float getFeatherAmount() {
		return feather_amount;
	}
	public void setFeatherAmount(float featherAmount) {
		this.feather_amount = featherAmount;
	}
	
	public abstract boolean isContained(float x, float z);
	public abstract float process(float x, float z);
	
	public float process(float x, float y, float transform_value, float base_value, TerrainVisitor ti) {
		return 0.0f;
	}
	
	public abstract float getMinX();
	public abstract float getMinZ();
	public abstract float getMaxX();
	public abstract float getMaxZ();
	
}
