package utils.stf;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;

public class STFReader 
{
	public static Map<String, Map<String, String>> cachedSTFs = new HashMap<String, Map<String, String>>();
	
	public static Map<String, String> read(String stfPath) throws IOException
	{
		// If we already loaded the STF file, lets return it's results
		if(cachedSTFs.get("stfPath") != null) return cachedSTFs.get("stfPath");

		// Lets go ahead and load the STF since it hasn't already been loaded
		FileInputStream file = new FileInputStream(stfPath);
		IoBuffer buffer = IoBuffer.allocate(file.available(), false);
		
		buffer.setAutoExpand(true);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		int entryCount = buffer.getInt();
		STFEntry[] entries = new STFEntry[entryCount];
		
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
			
			entries[i].value = new String(characterBuffer, "UTF-8");
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
			
			entries[i].key = new String(characterBuffer, "UTF-8");
		}
		
		// Convert STFEntry array to HashMap
		Map<String, String> map = new HashMap<String, String>();
		for(int i = 0; i < entryCount; i++) map.put(entries[i].key, entries[i].value);
		
		// Add our HashMap to cached hashmap to prevent reloading loading of STFs
		cachedSTFs.put(stfPath, map);
		return map;
	}
}
