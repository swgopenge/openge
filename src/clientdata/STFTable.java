package clientdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

public class STFTable {
	
	private static Map<String, STFTable> cachedSTFs = new HashMap<String, STFTable>();
	
	private int size;
	private int rowCount;
	private int entryCount;
	private byte unknown;	// TODO: What is this?
	private static Map<Integer, Map<String, String>> entries = new HashMap<Integer, Map<String, String>>();
	
	public int getSize() {
		return size;
	}
	
	public int getRowCount() {
		return rowCount;
	}
	
	public int getEntryCount() {
		return entryCount;
	}
	
	public byte getUnknown() {
		return unknown;
	}
	
	public static STFTable read(String stfPath) throws IOException {
		// If we have already loaded the STFTable, let's return that
		if(cachedSTFs.containsKey(stfPath))
			return cachedSTFs.get(stfPath);
		
		// Let's go ahead and load the STF since it isn't in the cache
		File stfFile = new File("clientdata/string/en/" + stfPath);
		FileInputStream fileStream = new FileInputStream(stfFile);
		IoBuffer buffer = IoBuffer.allocate(fileStream.available(), false);
		
		buffer.setAutoExpand(true);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.put(Files.readAllBytes(stfFile.toPath()));
		buffer.flip();
		
		int size = buffer.getInt();
		byte unknown = buffer.get();
		int rowCount = buffer.getInt();
		int entryCount = buffer.getInt();
		
		STFTable table = new STFTable(size, unknown, rowCount, entryCount);
		
		byte[] characterBuffer;
		String[] values = new String[entryCount];
		String[] keys = new String[entryCount];
		int entryNumbers[] = new int[entryCount];
		int entryNumber, characters;
		// Read values into array. Read entryNumbers into array.
		for(int i = 0; i < entryCount; i++)
		{
			entryNumber = buffer.getInt();
			buffer.skip(4);
			characters = buffer.getInt();
			
			characterBuffer = new byte[characters];
			
			for(int a = 0; a < characters; a++) {
				characterBuffer[a] = buffer.get();
				// Skip a byte because value is a unicode string
				buffer.skip(1); 
			}
			
			entryNumbers[i] = entryNumber;
			values[i] = new String(characterBuffer, StandardCharsets.UTF_8);
			
		}
		
		// Read keys into array
		for(int i = 0; i < entryCount; i++) {
			entryNumber = buffer.getInt();
			characters = buffer.getInt();
			characterBuffer = new byte[characters];
			
			for(int a = 0; a < characters; a++)
				characterBuffer[a] = buffer.get();
			// Keys are being read alphabetically and not by entry number like the values are
			keys[entryNumber - 1] = new String(characterBuffer, StandardCharsets.UTF_8);
		}
		
		Map<String, String> keySet = new HashMap<String, String>();
		
		for(int i = 0; i < entryCount; i++) {
			keySet.put(keys[i], values[i]);
			entries.put(entryNumbers[i], keySet);
		}
		
		// Add our Map to the cache to prevent the same STF file from being read unnecessarily
		fileStream.close();
		cachedSTFs.put(stfPath, table);
		return table;
	}
	
	private STFTable(int size, byte unknown, int rowCount, int entryCount) throws IOException {	// TODO: A boolean whether to cache the STFTable or not?
		this.size = size;
		this.unknown = unknown;
		this.rowCount = rowCount;
		this.entryCount = entryCount;
	}
	
	public String getValue(String key) throws IOException {
		for(int i = 1; i <= entries.size() ; i++)
			if (entries.get(i).containsKey(key))
				return entries.get(i).get(key);
		return null;
	}
}
