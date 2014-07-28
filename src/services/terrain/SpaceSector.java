package services.terrain;

import java.util.HashMap;
import java.util.Map;

import utils.scene.Point3D;

public class SpaceSector extends SWGTerrain {
	Map<String, Point3D> routes = new HashMap<String, Point3D>();	// String: Full name for hyperspace route. Point3D: spawn point for the route.
	
	public Map<String, Point3D> getRoutes() {
		return routes;
	}
	
	public SpaceSector(String terrainFileName) {
		setTerrainFileName(terrainFileName);
	}
		
}
