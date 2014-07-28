package services.terrain;

class SWGTerrain {
	private boolean inhabitable;
	private String terrainFileName;
	private String name;
	
	public boolean isInhabitable() {
		return inhabitable;
	}
	
	public void isInhabitable(boolean inhabitable) {
		this.inhabitable = inhabitable;
	}
	
	public void setTerrainFileName(String terrainFileName) {
		this.terrainFileName = terrainFileName;
	}
	
	public String getTerrainFileName() {
		return terrainFileName;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}
