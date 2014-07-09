package utils.scene;

import clientdata.ClientFileManager;
import clientdata.visitors.TerrainVisitor;
import clientdata.visitors.WorldSnapshotVisitor;

public class Planet {
	
	public int ID;
	public String name;
	public String path;
	public TerrainVisitor terrainVisitor;
	public WorldSnapshotVisitor snapshotVisitor;
	
	public Planet(int ID, String name, String path, boolean loadSnapshot) {
		this.ID = ID;
		this.name = name;
		this.path = path;
		
		try {
			terrainVisitor = ClientFileManager.loadFile(path, TerrainVisitor.class);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		if(loadSnapshot) {
			
			String wsPath = path.split("terrain", 2)[1].split(".trn", 2)[0];
			wsPath = new String("snapshot/" + wsPath +".ws");
			try {
				snapshotVisitor = ClientFileManager.loadFile(wsPath, WorldSnapshotVisitor.class);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}
	

	public int getID() {
		return ID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}

	public TerrainVisitor getTerrainVisitor() {
		return terrainVisitor;

	}

	public WorldSnapshotVisitor getSnapshotVisitor() {
		return snapshotVisitor;

	}
	
	
}
