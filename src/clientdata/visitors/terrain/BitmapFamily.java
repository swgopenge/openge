package clientdata.visitors.terrain;

import java.nio.charset.Charset;
import org.apache.mina.core.buffer.IoBuffer;
import utils.FileUtilities;

public class BitmapFamily {
	
	private int var1; // id?
	private String name;
	private String file;
	private TargaBitmap bitmap;
	
	public int getVar1() {
		return var1;
	}
	
	public void setVar1(int var1) {
		this.var1 = var1;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public TargaBitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(TargaBitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public void loadData(IoBuffer buffer) throws Exception {
		this.var1 = buffer.getInt();
		this.name = buffer.getString(Charset.forName("US-ASCII").newDecoder());
		this.file = buffer.getString(Charset.forName("US-ASCII").newDecoder());
		if(FileUtilities.doesFileExist("clientdata/" + file)) {
			TargaBitmap bitmap = new TargaBitmap();
			bitmap.readFile(file);
			this.bitmap = bitmap;
		}

	}

	
	

}
