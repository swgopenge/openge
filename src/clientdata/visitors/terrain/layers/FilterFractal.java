package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;
import clientdata.visitors.terrain.FractalFamily;

public class FilterFractal extends FilterLayer {

	int fractal_id;
	float min;
	float max;
	float step;
	
	public FilterFractal() {
		type = LayerType.FFRA;
	}
	
	@Override
	public float process(float x, float z, float transform_value, float base_value, TerrainVisitor ti, FilterRectangle rectangle) {
		FractalFamily fractal = ti.getFractal(fractal_id);

		float noise_result = fractal.getNoise(x, z) * step;
		float result = 0;
	
		if (noise_result > min && noise_result < max) {
			float feather_result = (float) ((max - min) * feather_amount * 0.5);
	
			if (min + feather_result <= noise_result) {
				if (max - feather_result >= noise_result)
					result = 1.0f;
				else
					result = (max - noise_result) / feather_result;
			} else
				result = (noise_result - min) / feather_result;
		} else
			result = 0;
	
		return result;
	}

	@Override
	public void loadData(IoBuffer buffer) throws Exception {
		fractal_id = buffer.getInt();
		feather_type = buffer.getInt();
		
		feather_amount = buffer.getFloat();
		min = buffer.getFloat();
		max = buffer.getFloat();
		step = buffer.getFloat();
	}

	@Override
	public float process(float x, float y, float transform_value,
			float base_value, TerrainVisitor ti) {
		// TODO Auto-generated method stub
		return 0;
	}

}
