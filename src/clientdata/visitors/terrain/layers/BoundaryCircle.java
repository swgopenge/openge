package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;

public class BoundaryCircle extends BoundaryLayer {

	float x, z, rad;
	private float radiusSquared;
	
	public BoundaryCircle() {
		type = LayerType.BCIR;
	}
	
	@Override
	public boolean isContained(float px, float pz) {
		return (Math.pow(px-x, 2) + Math.pow(pz-z, 2)) < Math.pow(rad, 2);
	}

	@Override
	public float process(float px, float pz) {
		//float dist = (float) (Math.pow(px-x, 2) + Math.pow(pz-z, 2));
		//float r2 = (float) Math.pow(rad, 2);
		float v3 = x - px;
		float v4 = z - pz;
		float dist = v4 * v4 + v3 * v3;

		if( dist <= radiusSquared) {
		
			float fCircle = (float) Math.pow((1.0 - feather_amount) * rad, 2);
			
			if(dist > fCircle) {
				return 1.0f - (dist-fCircle) / (radiusSquared-fCircle);
			} else {
				return 1.0f;
			}
		} else {
			return 0.0f;
		}
	}

	@Override
	public void loadData(IoBuffer buffer) {
		x = buffer.getFloat();
		z = buffer.getFloat();
		
		rad = buffer.getFloat();
		radiusSquared = rad * rad;
		
		feather_type = buffer.getInt();
		feather_amount = buffer.getFloat();
		
		if (feather_amount < 0)
			feather_amount = 0;
		else if (feather_amount > 1)
			feather_amount = 1;
	}

	@Override
	public float getMinX() {
		return x - rad;
	}

	@Override
	public float getMinZ() {
		return z - rad;
	}

	@Override
	public float getMaxX() {
		return x + rad;
	}

	@Override
	public float getMaxZ() {
		return z + rad;
	}

}
