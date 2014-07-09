package clientdata.visitors.terrain.layers;

import java.util.ArrayList;
import java.util.List;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.visitors.TerrainVisitor;

public class ListLayer extends Layer {

	private List<Layer> children;
	
	private List<BoundaryLayer> boundaries;
	private List<FilterLayer> filters;
	private List<HeightLayer> heights;
	
	private int invert_boundaries;
	private int invert_filters;
	private int unk4;
	private String description;
	
	@Override
	public LayerType getType() {
		return LayerType.CONTAINER;
	}
	
	public ListLayer() {
		children = new ArrayList<Layer>();
		boundaries = new ArrayList<BoundaryLayer>();
		filters = new ArrayList<FilterLayer>();
		heights = new ArrayList<HeightLayer>();
	}
	
	public List<Layer> getChildren() {
		return children;
	}
	
	public List<BoundaryLayer> getBoundaries() {
		return boundaries;
	}
	
	public List<FilterLayer> getFilters() {
		return filters;
	}
	
	public List<HeightLayer> getHeights() {
		return heights;
	}
	
	public void addChild(Layer l) {
		if(l instanceof BoundaryLayer) {
			boundaries.add((BoundaryLayer) l);
		} else if(l instanceof FilterLayer) {
			filters.add((FilterLayer)l);
		} else if(l instanceof HeightLayer) {
			heights.add((HeightLayer)l);
		} else {
			children.add((ListLayer) l);
		}
	}
	
	@Override
	public void loadData(IoBuffer buffer) throws Exception {
		invert_boundaries = buffer.getInt();
		invert_filters = buffer.getInt();
		unk4 = buffer.getInt();
		
		//description = buffer.getString(Charset.forName("US-ASCII").newDecoder());
	}
	
	public boolean boundariesInverted() {
		return invert_boundaries != 0;
	}

	public boolean filtersInverted() {
		return invert_filters != 0;
	}

	public int getUnk4() {
		return unk4;
	}

	public String getDescription() {
		return description;
	}

	public float process(float x, float y, float transform_value, float base_value, TerrainVisitor ti) {
		return 0.0f;
	}
	
}
