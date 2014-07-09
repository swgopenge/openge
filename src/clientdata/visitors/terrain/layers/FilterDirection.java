package clientdata.visitors.terrain.layers;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;

public class FilterDirection extends FilterLayer {

	@Override
	public void loadData(IoBuffer buffer) throws Exception {

		buffer.getFloat();
		buffer.getFloat();
		feather_type = buffer.getInt();
		feather_amount = buffer.getFloat();

	}

	@Override
	public float process(float x, float y, float transform_value, float base_value, TerrainVisitor ti, FilterRectangle rectangle) {
		return base_value;
	}

	@Override
	public float process(float x, float y, float transform_value,
			float base_value, TerrainVisitor ti) {
		return base_value;
	}

}
