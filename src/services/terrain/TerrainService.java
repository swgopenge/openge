package services.terrain;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Vector;

import utils.FileUtilities;
import clientdata.STFTable;
import network.PacketHandler;
import network.Service;

public class TerrainService implements Service {
	
	private Vector<SWGTerrain> terrainList = new Vector<SWGTerrain>();
	
	public TerrainService() {
		loadTerrains();
	}
	
	private void loadTerrains() {
		String planetFolder = "scripts/terrain/planets/";
		String spaceFolder = "scripts/terrain/space/";
	    Path planets = Paths.get(planetFolder);
	    Path space = Paths.get(spaceFolder);
	    
	    FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
	    	
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	        	String terrainName = file.getFileName().toString().replace(".py", "");
	        	if(!FileUtilities.doesFileExist("clientdata/terrain/" + terrainName + ".trn")) {
	        		System.out.println("Terrain: \"" + terrainName + "\" was not loaded: Terrain file not found.");
	        		return FileVisitResult.CONTINUE;
	        				
	        	}
	        	
	        	SWGTerrain newTerrain = null;
	        	String scriptPath = "";
	        	
	    		if( file.getParent().equals(planets)) {
	    			newTerrain = new Planet(terrainName);
	    			scriptPath = planetFolder;
	    			
	    		} else if(file.getParent().equals(space)) {
	    			newTerrain = new SpaceSector(terrainName);
	    			scriptPath = spaceFolder;
	    		}
	    		
	    		String stfName = STFTable.read("planet_n.stf").getValue(terrainName);
	    		
	    		scripting.ScriptingManager.callScript(scriptPath, terrainName, "setup", newTerrain);
	    		
	    		if (stfName != null)	// If the name isn't in planet_n.stf, it will have to be set manually in the terrain script using setName()
	    			newTerrain.setName(stfName);
	    		else if (newTerrain.getName() == null) {	// If the name isn't set in the script either, the terrain will not be created.
	    			System.out.println("Terrain: \"" + terrainName + "\" was not loaded: Missing name.");
	    			return FileVisitResult.CONTINUE;
	    		}
	    		
	    		terrainList.add(newTerrain);
	    		
	        	return FileVisitResult.CONTINUE;
	        }
	    };
	    
	    try {
			Files.walkFileTree(planets, fv);
			Files.walkFileTree(space, fv);
		} catch (IOException e) {
        	e.printStackTrace();
        	
        }
	}
	
	public boolean isPlanet(SWGTerrain terrain) {
		for(SWGTerrain storedTerrain : terrainList)
			if(storedTerrain.equals(terrain))
				if(storedTerrain instanceof Planet)
					return true;
		return false;
	}
	
	public int getIndexForTerrain(SWGTerrain terrain) {
		return terrainList.indexOf(terrain);
	}
	
	public SWGTerrain getTerrainForIndex(int index) {
		return terrainList.get(index);
	}
	
	@Override
	public void handlePackets(Map<Integer, PacketHandler> handlers) {
		
	}

	@Override
	public void handleObjControllerPackets(Map<Integer, PacketHandler> handlers) {

	}

}
