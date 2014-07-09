package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;
import clientdata.visitors.terrain.FractalFamily;

public class AffectorHeightFractal extends HeightLayer {

	int   fractal_id;
	int   transform_type;
	//float height_val;
	
	@Override
	public float process(float x, float z, float transform_value, float base_height, TerrainVisitor ti) {
		
		if(transform_value == 0)
			return base_height;
		
		FractalFamily fractal = ti.getFractal(fractal_id);

		float noise_result = fractal.getNoise(x, z) * height_val;
		float result;
		switch (transform_type)
		{
		case 1:
			result = base_height + noise_result * transform_value;
			break;
		case 2:
			result = base_height - noise_result * transform_value;
			break;
		case 3:
			result = base_height + (noise_result * base_height - base_height) * transform_value;
			break;
		case 4:
			result = base_height;
			break;
		default:
			result = base_height + (noise_result - base_height) * transform_value;
			break;
		}
	
		return result;
	}

	@Override
	public void loadData(IoBuffer buffer) {
		//System.out.println(Utilities.getHexString(buffer.array()));
		fractal_id = buffer.getInt();
		transform_type = buffer.getInt();
		height_val = buffer.getFloat();
	}

}
