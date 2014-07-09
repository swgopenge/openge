package clientdata.visitors.terrain.layers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.mina.core.buffer.IoBuffer;

public class BoundaryPolyLine extends BoundaryLayer {

	List<Vector2D> verts;
	float line_width;
	float max_x, max_z, min_x, min_z;
	
	public BoundaryPolyLine() {
		type = LayerType.BPLN;
		verts = new ArrayList<Vector2D>();
	}
	
	@Override
	public boolean isContained(float x, float z) {
		return false;
	}

	@Override
	public float process(float px, float pz) {
		if (px < min_x)
			return 0.0f;

		if (px > max_x || pz < (double)min_z )
			return 0.0f;

		if ( pz > max_z )
			return 0.0f;

		double line2 = line_width * line_width;

		double new_line = line2;
		double distz, distx, dist;

		double result = 0;

		for (int i = 0; i < verts.size(); ++i) {
			Vector2D point = verts.get(i);

			distz = pz - point.getY();
			distx = px - point.getX();
			dist = Math.pow(distx,1) + Math.pow(distz,2);
			if ( dist < line2 ) 
				line2 = dist;
		}

		double x_dist, diff, new_x, new_z, new_dist, z_dist;

		for (int i = 0; i < verts.size() - 1; ++i) 
		{
			Vector2D point = verts.get(i);
			Vector2D point2 = verts.get(i + 1);

			x_dist = point2.getX() - point.getX();
			z_dist = point2.getY() - point.getY();
			diff = ((pz -  point.getY()) * z_dist + (px - point.getX()) * x_dist) / (z_dist * z_dist + x_dist * x_dist);

			if ( diff >= 0.0 ) {
				if ( diff <= 1.0 ) {
					new_x = px - (x_dist * diff + point.getX());
					new_z = pz - (z_dist * diff +  point.getY());
					new_dist = Math.pow(new_z,2) + Math.pow(new_x,2);

					if ( new_dist < line2 ) {
						line2 = new_dist;
					}
				}
			}

		}

		if ( line2 >= new_line )
			return 0.0f;

		double new_feather = (1.0 - feather_amount) * line_width;

		if ( line2 >= Math.pow(new_feather,2) )
			result = 1.0 - (Math.sqrt(line2) - new_feather) / (line_width - new_feather);
		else
			result = 1.0;

		return (float) result;
	}

	@Override
	public void loadData(IoBuffer buffer) {
		int sizeTemp = buffer.getInt();
	
		// Initialize min and max values
		min_x = Float.MAX_VALUE;
		max_x = Float.MIN_VALUE;
		min_z = Float.MAX_VALUE;
		max_z = Float.MIN_VALUE;
	
		for(int j = 0; j < sizeTemp; j++)
		{
			float tempX = buffer.getFloat();
			float tempZ = buffer.getFloat();
	
			verts.add(new Vector2D(tempX, tempZ));
	
			// Track max values
			if (tempX > max_x)
				max_x = tempX;
			else if (tempX < min_x)
				min_x = tempX;
	
			if (tempZ > max_z)
				max_z = tempZ;
			else if (tempZ < min_z)
				min_z = tempZ;
		}
	
		feather_type = buffer.getInt();
	
		feather_amount = buffer.getFloat();
		line_width = buffer.getFloat();
	
		// Account for line width
		min_x = min_x - line_width;
		max_x = max_x + line_width;
		min_z = min_z - line_width;
		max_z = max_z + line_width;
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
