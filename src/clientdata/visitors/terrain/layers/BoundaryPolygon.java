package clientdata.visitors.terrain.layers;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.mina.core.buffer.IoBuffer;

public class BoundaryPolygon extends BoundaryLayer {

	
	final static float eps = 0.000001f;
	
	List<Vector2D> verts;
	
	int use_water_height;
	float water_height;
	float water_shader_size;
	String water_shader;
	
	public boolean useWaterHeight() {
		return use_water_height != 0;
	}

	public float getWaterHeight() {
		return water_height;
	}

	public float getWaterShaderSize() {
		return water_shader_size;
	}

	public String getWaterShader() {
		return water_shader;
	}

	float max_x, max_z, min_x, min_z;
	
	public BoundaryPolygon() {
		type = LayerType.BPOL;
		verts = new ArrayList<Vector2D>();
	}
	
	@Override
	public boolean isContained(float x, float y) {
		int j;
		//boolean odd_nodes = false;
		float x1, x2;
		int crossings = 0;
	
		for ( int i = 0; i < verts.size(); ++i )
		{
			j = (i+1) % verts.size();
	
			if ( verts.get(i).getX() < verts.get(j).getX())
			{
				x1 = (float)verts.get(i).getX();
				x2 = (float)verts.get(j).getX();
			} 
			else 
			{
				x1 = (float)verts.get(j).getX();
				x2 = (float)verts.get(i).getX();
			}

			/* First check if the ray is possible to cross the line */
			if ( x > x1 && x <= x2 && ( y < (float)verts.get(i).getY() || y <= (float)verts.get(j).getY() ) ) {
				

				/* Calculate the equation of the line */
				float dx = (float)verts.get(j).getX() - (float)verts.get(i).getX();
				float dz = (float)verts.get(j).getY() - (float)verts.get(i).getY();
				float k;

				if ( Math.abs(dx) < eps ){
					k = Float.POSITIVE_INFINITY;
				} else {
					k = dz/dx;
				}

				float m = (float)verts.get(i).getY() - k * (float)verts.get(i).getX();

				/* Find if the ray crosses the line */
				float z2 = k * x + m;
				if ( y <= z2 )
				{
					//odd_nodes=!odd_nodes;
					crossings++;
				}
			}
		}
		if(crossings % 2 == 1)
			return true;
		return false;
		//return odd_nodes;
	}

	@Override
	public float process(float px, float py) {
		float result;
		Vector2D last = verts.get(verts.size() - 1);
		boolean odd_nodes = false;
	
		if (px < min_x || px > max_x || py < min_z || py > max_z)
			return 0.0f;
	
		if (verts.size() <= 0)
			return 0.0f;
	
		for (int i = 0; i < verts.size(); i++)
		{
			Vector2D point = verts.get(i);
	
			if ((point.getY() <= py && py < last.getY()) || (last.getY() <= py && py < point.getY()))
				if ((py - point.getY()) * (last.getX() - point.getX()) / (last.getY() - point.getY()) + point.getX() > (double)px) 
					odd_nodes = !odd_nodes;
	
			last = point;
		}
	
		double feather2, new_feather;
	
		if (odd_nodes)
		{
			if (feather_amount == 0)
				return 1.0f;
	
			feather2 = Math.pow(feather_amount,2);
			new_feather = feather2;
			double diffz, diffx, dist;
	
			for (int i = 0; i < verts.size(); ++i) 
			{
				Vector2D point = verts.get(i);
	
				diffz = py - point.getY();
				diffx = px - point.getX();
				dist = Math.pow(diffz,2) + Math.pow(diffx,2);
	
				if ( dist < feather2 ) 
					feather2 = dist;
			}
	
			double ltp_x, ltp_z, ptl_z, ptl_x, diff, new_dist, newX, newZ;
	
			last = verts.get(verts.size() - 1);
	
			for (int i = 0; i < verts.size(); ++i) 
			{
				Vector2D point = verts.get(i);
	
				ltp_x = last.getX() - point.getX();
				ltp_z = last.getY() - point.getY();
				ptl_z = point.getY() - last.getY();
				ptl_x = point.getX() - last.getX();
				diff = ((px - last.getX()) * ptl_x + (py - last.getY()) * ptl_z) / (ltp_z * ptl_z + ltp_x * ltp_x);
				if ( diff >= 0.0 ) 
				{
					if ( diff <= 1.0 ) 
					{
						newX = px - (ptl_x * diff + last.getX());
						newZ = py - (ptl_z * diff + last.getY());
						new_dist = newZ * newZ + newX * newX;
						if ( new_dist < feather2 ) 
						{
							feather2 = new_dist;
						}
					}
				}
	
				last = point;
			}
	
			if ( feather2 >= new_feather - 0.00009999999747378752 && feather2 <= new_feather + 0.00009999999747378752 )
				result = 1.0f;
			else
				result = (float) (Math.sqrt(feather2) / feather_amount);
		} 
		else 
		{
			result = 0.0f;
		}
	
		return result;
	}

	@Override
	public void loadData(IoBuffer buffer) throws Exception {
		int sizeTemp = buffer.getInt();
		
		min_x = Float.MAX_VALUE;
		max_x = Float.MIN_VALUE;
		
		min_z = Float.MAX_VALUE;
		max_z = Float.MIN_VALUE;
		
		for(int j =0; j < sizeTemp; ++j) {
			float tempX = buffer.getFloat();
			float tempZ = buffer.getFloat();
			
			verts.add(new Vector2D(tempX, tempZ));
			
			if(tempX > max_x) {
				max_x = tempX;
			} else if(tempX < min_x) {
				min_x = tempX;
			}
			
			if (tempZ > max_z) {
				max_z = tempZ;
			} else if (tempZ < min_z) {
				min_z = tempZ;
			}
		}
		
		feather_type = buffer.getInt();
		feather_amount = buffer.getFloat();
		use_water_height = buffer.getInt();
		water_height = buffer.getFloat();
		water_shader_size = buffer.getFloat();
		water_shader = buffer.getString(Charset.forName("US-ASCII").newDecoder());
	}

	@Override
	public float getMinX() {
		return min_x;
	}

	@Override
	public float getMinZ() {
		return min_z;
	}

	@Override
	public float getMaxX() {
		return max_x;
	}

	@Override
	public float getMaxZ() {
		return max_z;
	}

}
