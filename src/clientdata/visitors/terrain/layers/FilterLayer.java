package clientdata.visitors.terrain.layers;

import clientdata.visitors.TerrainVisitor;

public abstract class FilterLayer extends Layer {

	protected int feather_type;
	protected float feather_amount;
	
	public int getFeatherType() {
		return feather_type;
	}
	
	public float getFeatherAmount() {
		return feather_amount;
	}
	
	public abstract float process(float x, float y, float transform_value, float base_value, TerrainVisitor ti, FilterRectangle rectangle);

	
}
