package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;

public class AffectorHeightTerrace extends HeightLayer {

	float flat_ratio;
	float height_delta;
	
	@Override
	public void loadData(IoBuffer buffer) throws Exception {
		flat_ratio = buffer.getFloat();
		height_delta = buffer.getFloat();
		height_val = height_delta;
	}

	@Override
	public float process(float x, float y, float transform_value, float base_value, TerrainVisitor ti) {
		if (transform_value == 0)
			return base_value;
 
		if (height_delta <= 0)
			return base_value;
 
		float var1 = base_value % height_delta;
 
		if (base_value == 0) {
			var1 += height_delta;
		}
 
		float var2 = base_value - var1;
		float var3 = height_delta * flat_ratio + var2;
		float var4 = height_delta + var2;
 
		if (base_value > var3) {
			var2 = (base_value - var3) / (var4 - var3) * (var4 - var2) + var2;
		}
 
		return (var2 - base_value) * transform_value + base_value;
	}

}
