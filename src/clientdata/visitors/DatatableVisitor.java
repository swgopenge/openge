package clientdata.visitors;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;

public class DatatableVisitor implements VisitorInterface {

	String[] columnNames;
	byte[] columnTypes;
	Object[][] table;
	
	public int getRowCount() {
		if(table == null)
			return 0;
		return table.length;
	}
	
	public int getColumnCount() {
		if(table == null)
			return 0;
		return columnNames.length;
	}
	
	public Object getObject(int row, int column) {
		return table[row][column];
	}
	
	public Object[] getRowsByColumnName(String columnName) {
		
		for(int i = 0; i < table.length; i++) {
			for(int j=0; j < columnTypes.length; ++j) {
				String currentName = columnNames[j];
				if(currentName.equals(columnName))
					return table[i];
			}
		}
		return null;
		
	}
	
	public Object getObjectByColumnNameAndIndex(String columnName, int rowIndex) {
		for(int j=0; j < columnTypes.length; ++j) {
			String currentName = columnNames[j];
			if(currentName.equals(columnName))
				return table[rowIndex][j];
		}
		return null;
	}
	
	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {
		if("0001COLS".equals(nodename)) {
			columnNames = new String[data.getInt()];
			
			CharsetDecoder cd = Charset.forName("US-ASCII").newDecoder();
			for(int i=0; i < columnNames.length; ++i) {
				columnNames[i] = data.getString(Charset.forName("US-ASCII").newDecoder());
				cd.reset();
			}
			
		} else if("TYPE".equals(nodename)) {
			
			columnTypes = new byte[columnNames.length];
			
			for(int i=0; i < columnTypes.length; ++i) {
				columnTypes[i] = (byte) data.getString(Charset.forName("US-ASCII").newDecoder()).charAt(0);
			}
			
		} else if("ROWS".equals(nodename)) {
			
			int rowCount = data.getInt();
			table = new Object[rowCount][columnTypes.length];
			
			CharsetDecoder cd = Charset.forName("US-ASCII").newDecoder();
			for(int i=0; i < rowCount; ++i) {
				for(int j=0; j < columnTypes.length; ++j) {
					switch(columnTypes[j]) {
					case 's':
					case 'S':
						try {
							table[i][j] = data.getString(Charset.forName("US-ASCII").newDecoder());
							cd.reset();
						} catch(MalformedInputException e) {
							cd.reset();
						}
						break;
					case 'f':
					case 'F':
						table[i][j] = data.getFloat();
						break;
					case 'h':
					case 'z':
					case 'p':
					case 'c':
					case 'e':
					case 'E':
					case 'I':
					case 'i':
						table[i][j] = data.getInt();
						break;
					case 'b':
					case 'B':
						table[i][j] = data.getInt() != 0;
						break;
					default:
						table[i][j] = data.getInt();
						break;
						
					}
				}
			}
		}
	}

	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
	}

}
