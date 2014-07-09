package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;

public class FilterSlope extends FilterLayer {

	public static float default_value = 1.5707964f;
	
	float min_angle;
	float min;
	float max_angle;
	float max;

	void setMaxAngle(float new_angle) {
		if (new_angle >= 0) {
			if (new_angle <= default_value) {
				max_angle = new_angle;
				min = (float) Math.sin(default_value - new_angle);
			} else {
				max_angle = default_value;
				min = 0;
			}
		} else {
			max_angle = 0;
			min = (float) Math.sin(default_value);
		}
	}
	
	void setMinAngle(float new_angle) {
		if (new_angle >= 0) {
			if (new_angle <= default_value) {
				min_angle = new_angle;
				max = (float) Math.sin(default_value - new_angle);
			} else {
				min_angle = default_value;
				max = 0;
			}
		} else {
			min_angle = 0;
			max = (float) Math.sin(default_value);
		}
	}
	
	@Override
	public float process(float x, float y, float transform_value, float base_value, TerrainVisitor ti, FilterRectangle rectangle) {
		float result;

		if (base_value > min && base_value < max) {
			float feather_result = (float) (max - min * feather_amount * 0.5);
	
			if (min + feather_result <= base_value) {
				if (max - feather_result >= base_value) {
					result = 1.0f;
				} else {
					result = (max - base_value) / feather_result;
				}
			} else
				result = (base_value - min) / feather_result;
		} else
			result = 0;
	
		return result;
	}

	@Override
	public void loadData(IoBuffer buffer) throws Exception {
		min_angle = buffer.getFloat();
		setMinAngle((float) (Math.PI * min_angle * 0.005555555690079927));
		
		max_angle = buffer.getFloat();
		setMaxAngle((float) (Math.PI * max_angle * 0.005555555690079927));
		
		feather_type = buffer.getInt();
		feather_amount = buffer.getFloat();
		
		if (feather_amount > 1)
			feather_amount = 1;
		else if (feather_amount < 0)
			feather_amount = 0;	
	}

	@Override
	public float process(float x, float y, float transform_value,
			float base_value, TerrainVisitor ti) {
		// TODO Auto-generated method stub
		return 0;
	}

}
