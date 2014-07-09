package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;

public class FilterHeight extends FilterLayer {

	float minHeight;
	float maxHeight;
	
	@Override
	public float process(float x, float z, float transform_value, float base_value, TerrainVisitor ti, FilterRectangle rectangle) {
		float result;

		if ((base_value > minHeight) && (base_value < maxHeight)) {
			float feather_result = (float) ((maxHeight - minHeight) * feather_amount * 0.5);
	
			if (minHeight + feather_result <= base_value) {
				if (maxHeight - feather_result >= base_value) {
					result = 1.0f;
				} else
					result = (maxHeight - base_value) / feather_result;
	
			} else {
				result = (base_value - minHeight) / feather_result;
			}
		} else
			result = 0;
	
		return result;
	}

	@Override
	public void loadData(IoBuffer buffer) throws Exception {
		minHeight = buffer.getFloat();
		maxHeight = buffer.getFloat();
		feather_type = buffer.getInt();
		feather_amount = buffer.getFloat();
	}

	@Override
	public float process(float x, float y, float transform_value,
			float base_value, TerrainVisitor ti) {
		// TODO Auto-generated method stub
		return 0;
	}

}
