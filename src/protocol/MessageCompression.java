package protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class MessageCompression {
	
	public byte[] compress(byte[] data) {
		byte[] result = new byte[512];
		byte[] dataToCompress = ByteBuffer.allocate(data.length - 2).put(data, 2, data.length - 2).array();
		Deflater compressor = new Deflater();
		compressor.setInput(dataToCompress);
		compressor.setLevel(java.util.zip.Deflater.DEFAULT_COMPRESSION);
		compressor.finish();
		
		int length = compressor.deflate(result);
		if (length < dataToCompress.length)
			return ByteBuffer.allocate(length + 3).put(data[0]).put(data[1]).put(result, 0, length).put((byte)1).array();
		else
			return ByteBuffer.allocate(data.length + 1).put(data).put((byte)0).array();
		
	}
	
	public byte[] decompress(byte[] data) {
		if (data.length == 0)
			return data;
		else if (data[data.length - 1] == 0)
			return ByteBuffer.allocate(data.length - 1).put(data, 0, data.length - 1).array();
		
		int startingIndex = (data[0] > 0x00 && data[0] < 0x10) ? 1 : 2;
		
		byte[] result = new byte[512];
		Inflater decompressor = new Inflater();
		decompressor.setInput(data, startingIndex, data.length - startingIndex);
		
		int length;
		try {
			
			length = decompressor.inflate(result);
			decompressor.end();
			ByteBuffer resultBuffer = ByteBuffer.allocate(length + startingIndex);
			resultBuffer.put(data[0]);
			if (startingIndex > 1)
				resultBuffer.put(data[1]);
			
			resultBuffer.put(result, 0, length).array();
			return resultBuffer.array();
			
		} catch (DataFormatException e) {
			
			e.printStackTrace();
			System.out.println(Arrays.toString(data));
		
		}
		
		return new byte[] { };
	
	}
	
}
