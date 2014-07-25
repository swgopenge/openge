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

public class STFReader 
{
	public static Map<String, Map<String, String>> cachedSTFs = new HashMap<String, Map<String, String>>();
	
	public static Map<String, String> read(String stfPath) throws IOException
	{
		// If we already loaded the STF file, lets return it's results
		if(cachedSTFs.get(stfPath) != null) return cachedSTFs.get(stfPath);

		// Lets go ahead and load the STF since it hasn't already been loaded
		File stfFile = new File("./clientdata/string/en/" + stfPath);
		FileInputStream fileStream = new FileInputStream(stfFile);
		IoBuffer buffer = IoBuffer.allocate(fileStream.available(), false);
		
		buffer.setAutoExpand(true);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		buffer.put(Files.readAllBytes(stfFile.toPath()));
		
		buffer.flip();
		
		int size = buffer.getInt();
		buffer.get();	// unknown
		int rowCount = buffer.getInt();	// amount of rows in the file
		int entryCount = buffer.getInt();	// amount of entries
		
		// Create STFEntries
		STFEntry[] entries = new STFEntry[entryCount];
		STFEntry newEntry = null;
		
		for(int i = 0; i < entryCount; i++) {
			newEntry = new STFEntry();
			entries[i] = newEntry;
		}
		
		// Read values
		for(int i = 0; i < entryCount; i++)
		{
			int entryNumber = buffer.getInt();
			buffer.skip(4);
			int characters = buffer.getInt();
			
			byte[] characterBuffer = new byte[characters];
			
			for(int a = 0; a < characters; a++)
			{
				characterBuffer[a] = buffer.get();
				// Skip a byte because value is a unicode string
				buffer.skip(1); 
			}
			
			try {
				entries[entryNumber - 1].value = new String(characterBuffer, StandardCharsets.UTF_8);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Read keys
		for(int i = 0; i < entryCount; i++)
		{
			int entryNumber = buffer.getInt();
			int characters = buffer.getInt();
			byte[] characterBuffer = new byte[characters];
			
			for(int a = 0; a < characters; a++)
			{
				characterBuffer[a] = buffer.get();
			}
			
			entries[entryNumber - 1].key = new String(characterBuffer, StandardCharsets.UTF_8);
			
		}
		
		// Convert STFEntry array to HashMap
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < entryCount; i++) map.put(entries[i].key, entries[i].value);
		
		// Add our HashMap to cached hashmap to prevent reloading loading of STFs
		cachedSTFs.put(stfPath, map);
		fileStream.close();
		
		return map;
	}
	
	public static String getString(String stfPath, String key) throws IOException
	{
		return read(stfPath).get(key);
	}
}
