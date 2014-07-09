package clientdata.visitors;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.mina.core.buffer.IoBuffer;


import clientdata.VisitorInterface;
import clientdata.visitors.terrain.BitmapFamily;
import clientdata.visitors.terrain.BitmapGroup;
import clientdata.visitors.terrain.FractalFamily;
import clientdata.visitors.terrain.layers.AffectorHeightConstant;
import clientdata.visitors.terrain.layers.AffectorHeightFractal;
import clientdata.visitors.terrain.layers.AffectorHeightTerrace;
import clientdata.visitors.terrain.layers.BoundaryCircle;
import clientdata.visitors.terrain.layers.BoundaryLayer;
import clientdata.visitors.terrain.layers.BoundaryPolyLine;
import clientdata.visitors.terrain.layers.BoundaryPolygon;
import clientdata.visitors.terrain.layers.BoundaryRectangle;
import clientdata.visitors.terrain.layers.FilterBitmap;
import clientdata.visitors.terrain.layers.FilterDirection;
import clientdata.visitors.terrain.layers.FilterFractal;
import clientdata.visitors.terrain.layers.FilterHeight;
import clientdata.visitors.terrain.layers.FilterLayer;
import clientdata.visitors.terrain.layers.FilterRectangle;
import clientdata.visitors.terrain.layers.FilterShader;
import clientdata.visitors.terrain.layers.FilterSlope;
import clientdata.visitors.terrain.layers.HeightLayer;
import clientdata.visitors.terrain.layers.Layer;
import clientdata.visitors.terrain.layers.LayerType;
import clientdata.visitors.terrain.layers.ListLayer;
import clientdata.visitors.terrain.layers.NullLayer;

public class TerrainVisitor implements VisitorInterface {

	//Temporary Data
	private Deque<Pair<Layer, Integer>> layer_stack;
	private Deque<String> foldername_stack;
	private CharsetDecoder ascii_charset;
	FractalFamily lastFamily;
	private BitmapGroup bitmapGroup;
	
	//Static Data
	private static Map<String, Class<? extends Layer>> layerLookup;
	
	//Header Data
	private String filename;
	private float map_width;
	private float chunk_width;
	private int   tiles_per_chunk;
	private int   use_global_water_height;
	private float global_water_height;

	Map<Integer,FractalFamily> fractal_families;
	
	
	ListLayer head_nodes;
	List<BoundaryPolygon> water_boundaries;
	
	public TerrainVisitor() {
		
		//Static Init
		if(layerLookup == null) {
			Map<String, Class<? extends Layer>> map = new TreeMap<String, Class<? extends Layer>>();
			map.put("LAYRFORM", ListLayer.class);
			
			map.put("AHCNFORM", AffectorHeightConstant.class);
			map.put("AHFRFORM", AffectorHeightFractal.class);
			map.put("AHTRFORM", AffectorHeightTerrace.class);
			
			map.put("BCIRFORM", BoundaryCircle.class);
			map.put("BPOLFORM", BoundaryPolygon.class);
			map.put("BPLNFORM", BoundaryPolyLine.class);
			map.put("BRECFORM", BoundaryRectangle.class);
			
			map.put("FBITFORM", FilterBitmap.class);
			map.put("FDIRFORM", FilterDirection.class);
			map.put("FFRAFORM", FilterFractal.class);
			map.put("FHGTFORM", FilterHeight.class);
			map.put("FSLPFORM", FilterSlope.class);
			map.put("FSHDFORM", FilterShader.class);
			
			layerLookup = map;
		}
		
		//These are only used during input processing.
		layer_stack = new LinkedList<Pair<Layer, Integer>>();
		foldername_stack = new LinkedList<String>();
		ascii_charset = Charset.forName("US-ASCII").newDecoder();
		
		//These contain the data required.
		fractal_families = new TreeMap<Integer, FractalFamily>();
		water_boundaries = new ArrayList<BoundaryPolygon>();
		bitmapGroup = new BitmapGroup();
		
		head_nodes = new ListLayer();
		layer_stack.push(new Pair<Layer, Integer>(head_nodes, -1));
	}
	
	public void cleanup() {
		//Clean up leftover data.
		foldername_stack = null;
		layer_stack = null;
		ascii_charset = null;
		
		//Prune all layers that are not related to height.
		prune_layers(head_nodes);
	}
	
	private int prune_layers(ListLayer l) {
		Iterator<Layer> itr = l.getChildren().iterator();
		Layer lay;
		LayerType lt;
		
		int count = 0;
		while(itr.hasNext()) {
			lay = itr.next();
			
			lt = lay.getType();
			if(lt == LayerType.CONTAINER) {
				int inner_count = prune_layers((ListLayer)lay);
				if(inner_count > 0) {
					count += inner_count;
				} else {
					itr.remove();
				}
			} else if(	lt == LayerType.AHCN || lt == LayerType.AHFR || lt == LayerType.AHTR || 
						lt == LayerType.FHGT || lt == LayerType.FFRA || 
						lt == LayerType.FSLP || lt == LayerType.FDIR || lt == LayerType.FBIT) {
				++count;
			} else if(	lt == LayerType.BCIR || lt == LayerType.BPLN ||
						lt == LayerType.BPOL || lt == LayerType.BREC) {
				//We want to keep this unless no heights were found, so 
				//NOP
			} else {
				itr.remove();
			}
		}
		return count;
	}
	
	private void popToParent(int depth) {
		while(depth < foldername_stack.size()) {
			foldername_stack.pop();
		}
		
		while(layer_stack.size() > 0 && depth < layer_stack.peek().second) {
			layer_stack.pop();
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public float getMapWidth() {
		return map_width;
	}

	public void setMapWidth(float map_width) {
		this.map_width = map_width;
	}

	public float getChunkWidth() {
		return chunk_width;
	}

	public void setChunkWidth(float chunk_width) {
		this.chunk_width = chunk_width;
	}

	public int getTilesPerChunk() {
		return tiles_per_chunk;
	}

	public void setTilesPerChunk(int tiles_per_chunk) {
		this.tiles_per_chunk = tiles_per_chunk;
	}

	public int getUseGlobalWaterHeight() {
		return use_global_water_height;
	}

	public void setUseGlobalWaterHeight(int use_global_water_height) {
		this.use_global_water_height = use_global_water_height;
	}

	public float getGlobalWaterHeight() {
		return global_water_height;
	}

	public void setGlobalWaterHeight(float global_water_height) {
		this.global_water_height = global_water_height;
	}

	public FractalFamily getFractal(int id) {
		return fractal_families.get(id);
	}
	
	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {
		popToParent(depth);
		
		String last_folder = foldername_stack.peekFirst();
		//System.out.println(last_folder);
		if(nodename.endsWith("DATA")) {
			if("IHDRFORM".equals(last_folder)) {
				
				//Find out what type to make
				Iterator<String> it = foldername_stack.iterator();
				String layerType = null;
				for(int i = 0; i < 3; ++i) {
					layerType = it.next();
				}
				
				//Make that type.
				Class<? extends Layer> cla = layerLookup.get(layerType);
				if(cla != null) {
					Layer newLayer = cla.newInstance();
					
					newLayer.loadHeader(data);
					
					((ListLayer)layer_stack.peek().first).addChild(newLayer);
					layer_stack.push(new Pair<Layer, Integer>(newLayer, depth-3));
					
					if(newLayer instanceof BoundaryPolygon) {
						if(((BoundaryPolygon)newLayer).useWaterHeight()) {
							water_boundaries.add((BoundaryPolygon) newLayer);
						}
					}
				} else {
					Layer newLayer = new NullLayer();
					layer_stack.push(new Pair<Layer, Integer>(newLayer, depth-3));
				}
				
			} else if("PTATFORM".equals(last_folder)) {
				//Header
				filename = data.getString(ascii_charset);
				ascii_charset.reset();
				
				map_width = data.getFloat();
				chunk_width = data.getFloat();
				tiles_per_chunk = data.getInt();
				use_global_water_height = data.getInt();
				global_water_height = data.getFloat();
				
			} /*else if("MGRPFORM".equals(last_folder)) {
				
				if("MFAMDATA".equals(nodename)) {
					BitmapFamily family = new BitmapFamily();
					family.loadData(data);
					bitmapGroup.addBitmapFamily(family);
				}
				
			}*/ else if("MFAMDATA".equals(nodename)) {
				FractalFamily fractal = new FractalFamily();
				int position = data.position();
				fractal.setFractal_id(data.getInt());
				fractal.setFractal_label(data.getString(ascii_charset));
				ascii_charset.reset();
				if(data.hasRemaining()) {
					data.position(position);
					BitmapFamily family = new BitmapFamily();
					family.loadData(data);
					bitmapGroup.addBitmapFamily(family);
					ascii_charset.reset();
				} else {
					fractal_families.put(fractal.getFractal_id(), fractal);
					lastFamily = fractal;
				}
			} else if("MFRCFORM".equals(last_folder)) {
				FractalFamily f = lastFamily;
				
				f.setSeed(data.getInt());
				f.setUse_bias(data.getInt());
				f.setBias(data.getFloat());
				
				f.setUse_gain(data.getInt());
				f.setGain(data.getFloat());
				
				f.setOctaves(data.getInt());
				f.setOctaves_arg(data.getFloat());
				
				f.setAmplitude(data.getFloat());
				
				f.setFreq_x(data.getFloat());
				f.setFreq_z(data.getFloat());
				
				f.setOffset_x(data.getFloat());
				f.setOffset_y(data.getFloat());
				
				f.setCombination_type(data.getInt());
				
			} else if(nodename.equals("DATA")) {
				if(data.remaining() >= 4)
					layer_stack.peek().first.loadData(data);
			}
		} else if(nodename.equals("ADTA")) {
			layer_stack.peek().first.loadData(data);
		} else if(nodename.equals("DATAPARM")) {
			layer_stack.peek().first.loadData(data);
		}
	}
	
	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		popToParent(depth);
		
		foldername_stack.push(nodeName);
	}
	
	public float getWaterHeight(float x, float z)
	{
		for (BoundaryPolygon boundary : water_boundaries)
		{
			if (boundary.isContained(x, z))
			{
				return boundary.getWaterHeight();
			}
		}
	
		if (use_global_water_height != 0)
		{
			return global_water_height;
		}
		
		return Float.NaN;
	}
	
	public boolean isWater(float x, float z)
	{
		float water_height;
		float height;
	
		if ((water_height = getWaterHeight(x, z)) != Float.NaN)
		{
			height = getHeight(x, z);
			if (height <= water_height)
				return true;
		}
	
		return false;
	}
	
	@SuppressWarnings("unused")
	private Layer findLayer(float x, float z)
	{
		for (Layer layer : head_nodes.getChildren())
		{
			if(layer instanceof ListLayer) {
				ListLayer ll = (ListLayer) layer;
				for (BoundaryLayer boundary : ll.getBoundaries())
				{
					if (boundary.isContained(x, z))
						return findLayerRecursive(x, z, ll);
				} 
			}
		}
	
		return head_nodes.getChildren().get(0);
	}
	
	private Layer findLayerRecursive(float x, float z, ListLayer rootLayer)
	{
		for (Layer layer : rootLayer.getChildren())
		{
			if(layer instanceof ListLayer) {
				ListLayer ll = (ListLayer)layer;
				for(BoundaryLayer boundary : ll.getBoundaries())
				{
					if (boundary.isContained(x, z))
						return findLayerRecursive(x, z, ll);
				}
			}
		}
	
		return rootLayer;
	}
	
	@SuppressWarnings("unused")
	public float getHeight(float x, float z)
	{
		float affector_transform = 1.0f;
		float transform_value = 0.0f;
		float height_result = 0.0f;
			
		for(Layer layer : head_nodes.getChildren()) {
	
			if (layer.isEnabled()) {
				if(layer instanceof ListLayer) {
					Pair<Float, Float> res = processLayerHeight((ListLayer)layer, x, z, height_result, affector_transform);
					
					transform_value = res.first;
					height_result = res.second;
				}
			}
		}
	
		return height_result;
	}
	
	private Pair<Float, Float> processLayerHeight(ListLayer layer, float x, float z, float base_value, float affector_transform)
	{
		List<BoundaryLayer> boundaries = layer.getBoundaries();
		List<HeightLayer> heights = layer.getHeights();
		List<FilterLayer> filters = layer.getFilters();
	
		float transform_value = 0.0f;
		boolean has_boundaries = false;
		//float result = 0.0f;
		FilterRectangle rectangle = new FilterRectangle();
		rectangle.minX = Float.MAX_VALUE;
		rectangle.minZ = Float.MAX_VALUE;
		//rectangle.maxX = Float.MIN_VALUE;
		//rectangle.maxZ = Float.MIN_VALUE;
		rectangle.maxX = -Float.MAX_VALUE;
		rectangle.maxZ = -Float.MAX_VALUE;
		
		for (BoundaryLayer boundary : boundaries)
		{
				
			if (!boundary.isEnabled())
				continue;
			else
				has_boundaries = true;
	
			float boundaryResult = boundary.process(x, z);
			
			if(boundaryResult != 0) {
				if(boundary.getMinX() < rectangle.minX)
					rectangle.minX = boundary.getMinX();
				if(boundary.getMinZ() < rectangle.minZ)
					rectangle.minZ = boundary.getMinZ();
				if(boundary.getMaxX() > rectangle.maxX)
					rectangle.maxX = boundary.getMaxX();
				if(boundary.getMaxZ() > rectangle.maxZ)
					rectangle.maxZ = boundary.getMaxZ();
			}
			
			float r = calculateFeathering(boundaryResult, boundary.getFeatherType());
	
			if (r > transform_value)
				transform_value = r;
	
			if (transform_value >= 1)
				break;
		}
	
		if (has_boundaries == false)
			transform_value = 1.0f;
	
		if (layer.boundariesInverted())
			transform_value = 1.0f - transform_value;
	
		if (transform_value != 0) 
		{
			for(FilterLayer filter : filters) {
					
				if (!filter.isEnabled())
					continue;
	
				float r = calculateFeathering(filter.process(x, z, transform_value, base_value, this, rectangle), filter.getFeatherType());
	
				if (transform_value > r)
					transform_value = r;
	
				if (transform_value == 0)
					break;
			}
			
			//System.out.println("Tranform value after filter: " + transform_value);
	
			if (layer.filtersInverted())
				transform_value = 1.0f - transform_value;
				
			if (transform_value != 0)
			{
				for(HeightLayer affector : heights) 
				{
					if (affector.isEnabled())
					{
						base_value = affector.process(x, z, transform_value * affector_transform, base_value, this);
						//System.out.println("Affector Height: " + base_value);
					}
				}
	
				List<Layer> children = layer.getChildren();
				for(Layer child : children)
				{
					if (child.isEnabled() && child instanceof ListLayer) {
						Pair<Float, Float> pair = processLayerHeight((ListLayer)child, x, z, base_value, affector_transform * transform_value);
						//transform_value = pair.first;
						base_value = pair.second;
						//System.out.println("Child Layer Height: " + base_value);
					} /*else {
						break;
					}*/
				}
			}
		}
	
		return new Pair<Float, Float>(transform_value, base_value);
	}
	
	private float calculateFeathering(float value, int featheringType) {
		switch (featheringType) {
		case 0:
			return value;
		case 1:
			return value * value;
		case 2:
			return (float) Math.sqrt(value);
		case 3:
			return value * value * (3 - 2 * value);
		default:
			return 0;
		}
	}
	
	public BitmapGroup getBitmapGroup() {
		return bitmapGroup;
	}

	public void setBitmapGroup(BitmapGroup bitmapGroup) {
		this.bitmapGroup = bitmapGroup;
	}

	public class Pair<T,V> {

		public T first;
		public V second;
		
		public Pair(T first, V second) {
			this.first = first;
			this.second = second;
		}
		
	}
}


