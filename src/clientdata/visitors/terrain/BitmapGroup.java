package clientdata.visitors.terrain;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import utils.FileUtilities;

public class BitmapGroup {
	
	private Vector<BitmapFamily> bitmapFamilies;
	private Map<Integer, TargaBitmap> bitmaps;
	
	public BitmapGroup() {
		bitmapFamilies = new Vector<BitmapFamily>();
		bitmaps = new HashMap<Integer, TargaBitmap>();
	}

	public Vector<BitmapFamily> getBitmapFamilies() {
		return bitmapFamilies;
	}

	public Map<Integer, TargaBitmap> getBitmaps() {
		return bitmaps;
	}
	
	public void addBitmapFamily(BitmapFamily bitmapFamily) {
		if(FileUtilities.doesFileExist("clientdata/" + bitmapFamily.getFile())) {
			bitmapFamilies.add(bitmapFamily);
			bitmaps.put(bitmapFamily.getVar1(), bitmapFamily.getBitmap());
		}
	}

}
