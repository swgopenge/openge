package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;

public class AffectorHeightConstant extends HeightLayer {

	int   transform_type;
	//float height_val;
	
	@Override
	public float process(float x, float z, float transform_value, float base_height, TerrainVisitor ti) {

		if(transform_value == 0)
			return base_height;
		
		float result;
		switch (transform_type) 
		{
		case 1:
			result = transform_value * height_val + base_height;
			break;
		case 2:
			result = base_height - transform_value * height_val;
			break;
		case 3:
			result = base_height + (base_height * height_val - base_height) * transform_value;
			break;
		case 4:
			result = 0;
			break;
		default:
			result = (float) ((1.0 - transform_value) * base_height + transform_value * height_val);
		}
	
		return result;
	}

	@Override
	public void loadData(IoBuffer buffer) {
		transform_type = buffer.getInt();
		height_val = buffer.getFloat();
	}

}
