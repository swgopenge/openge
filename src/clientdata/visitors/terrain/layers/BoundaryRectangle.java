package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;

public class BoundaryRectangle extends BoundaryLayer {

	float x1, z1;
	float x2, z2;
	
	@Override
	public boolean isContained(float px, float pz) {
		
		float w = Math.abs(x2 - x1);
		float h = Math.abs(z2 - z1);
		
		if(px < x1 || pz < z1)
			return false;
		
		w += x1;
		h += z1;
		
		return ((w < x1 || w > px) && (h < z1 || h > pz));
		//return (x2 >= px && x1 <= px && z2 >= pz && z1 <= pz);
	}

	@Override
	public float process(float px, float pz) {
		float result;

		if (!isContained(px, pz))
			result = 0.0f;
		else
		{
			float min_distx = px - x1;
			float max_distx = x2 - px;
			float min_distz = pz - z1;
			float max_distz = z2 - pz;
			float x_length = x2 - x1;
			float length = z2 - z1;
	
			if (x_length <= length)
				length = x_length;
	
			float feather_length = feather_amount * length * 0.5f;
			float feather_result = feather_length;
	
			float newX0 = x1 + feather_length;
			float newX1 = x2 - feather_length;
			float newZ0 = z1 + feather_length;
			float newZ1 = z2 - feather_length;
	
			if (px < newX1 || px > newX0 || pz < newZ1 || pz > newZ0)
				return 1.0f;
	
			if (min_distx < feather_length)
				feather_result = min_distx;
			if (max_distx < feather_result)
				feather_result = max_distx;
			if (min_distz < feather_result)
				feather_result = min_distz;
			if (max_distz < feather_result)
				feather_result = max_distz;
	
			result = feather_result / feather_length;
		}
	
		return result;
	}

	@Override
	public void loadData(IoBuffer buffer) {
		x1 = buffer.getFloat();
		z1 = buffer.getFloat();
		
		x2 = buffer.getFloat();
		z2 = buffer.getFloat();
		
		feather_type = buffer.getInt();
		feather_amount = buffer.getFloat();
		
		if (feather_amount < 0)
			feather_amount = 0;
		else if (feather_amount > 1)
			feather_amount = 1;
		
		float temp;
		
		if(x1 > x2) {
			temp = x1;
			x1 = x2;
			x2 = temp;
		}
		
		if(z1 > z2) {
			temp = z1;
			z1 = z2;
			z2 = temp;
		}
	}

	@Override
	public float getMinX() {
		return x1;
	}

	@Override
	public float getMinZ() {
		return z1;
	}

	@Override
	public float getMaxX() {
		return x2;
	}

	@Override
	public float getMaxZ() {
		return z2;
	}

}
