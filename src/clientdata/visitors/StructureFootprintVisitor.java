package clientdata.visitors;

import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;

public class StructureFootprintVisitor implements VisitorInterface {
	
	private int columnLength;
	private int rowLength;
	private int centerX;
	private int centerZ;
	private float rowChunkSize;
	private float columnChunkSize;

	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {
		if(nodename.equals("INFO")) {
			columnLength = data.getInt();
			rowLength = data.getInt();
			centerX = data.getInt();
			centerZ = data.getInt();
			columnChunkSize = data.getFloat();
			rowChunkSize = data.getFloat();
		}
	}

	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		
	}

	public int getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(int columnLength) {
		this.columnLength = columnLength;
	}

	public int getRowLength() {
		return rowLength;
	}

	public void setRowLength(int rowLength) {
		this.rowLength = rowLength;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterZ() {
		return centerZ;
	}

	public void setCenterZ(int centerZ) {
		this.centerZ = centerZ;
	}

	public float getRowChunkSize() {
		return rowChunkSize;
	}

	public void setRowChunkSize(float rowChunkSize) {
		this.rowChunkSize = rowChunkSize;
	}

	public float getColumnChunkSize() {
		return columnChunkSize;
	}

	public void setColumnChunkSize(float columnChunkSize) {
		this.columnChunkSize = columnChunkSize;
	}

}
