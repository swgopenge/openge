package services.terrain;

import utils.FileUtilities;
import clientdata.ClientFileManager;
import clientdata.visitors.TerrainVisitor;
import clientdata.visitors.WorldSnapshotVisitor;

public class Planet extends SWGTerrain {
	private WorldSnapshotVisitor snapshotVisitor;
	private TerrainVisitor terrainVisitor = new TerrainVisitor();
	
	public TerrainVisitor getTerrainVisitor() {
		return terrainVisitor;
	}
	
	public void setTerrainVisitor(TerrainVisitor terrainVisitor) {
		this.terrainVisitor = terrainVisitor;
	}
	
	public WorldSnapshotVisitor getSnapshotVisitor() {
		return snapshotVisitor;
	}
	
	public Planet(String terrainFileName) {
		setTerrainFileName(terrainFileName);
		
		try {
			terrainVisitor = ClientFileManager.loadFile("terrain/" + terrainFileName + ".trn", TerrainVisitor.class);
			
			if(FileUtilities.doesFileExist("snapshot/" + terrainFileName + ".ws")) {
				snapshotVisitor = new WorldSnapshotVisitor();
				snapshotVisitor = ClientFileManager.loadFile("snapshot/" + terrainFileName + ".ws", WorldSnapshotVisitor.class);
			}
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
