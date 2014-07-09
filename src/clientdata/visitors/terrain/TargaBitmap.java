package clientdata.visitors.terrain;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import org.apache.mina.core.buffer.IoBuffer;

@SuppressWarnings("unused")
public class TargaBitmap {
	
	private byte idlength, colourmaptype, datatypecode, colourmapdepth, bitsperpixel, imagedescriptor;
	private short colourmaporigin, colourmaplength, x_origin, y_origin, width, height;
	private TargaPixel[] pixelData;
	private String fileName;
	
	public class TargaColorPixel extends TargaPixel {
		public char r, g, b, a;

		@Override
		public void read(IoBuffer buffer, int bytes) throws IOException {
			switch (bytes) {
				case 4:
					r = (char) buffer.get();
					g = (char) buffer.get();
					b = (char) buffer.get();
					a = (char) buffer.get();
					break;
				case 3:
					r = (char) buffer.get();
					g = (char) buffer.get();
					b = (char) buffer.get();
					a = 0;
					break;
				case 2:
					char first = (char) buffer.get();
					char second = (char) buffer.get();
					r = (char) ((first & 0x7c) << 1);
					g = (char) (((first & 0x03) << 6) | ((second & 0xe0) << 2));
					b = (char) ((second & 0x1f) << 3);
					a = (char) (first & 0x80);
			}
		}
	}
	
	public class TargaBlackPixel extends TargaPixel {
		public char val;

		@Override
		public void read(IoBuffer buffer, int bytes) throws IOException {
			val = (char) buffer.get();
			if(val < 0)
				System.out.println("negative value");
		}
	}
	
	public abstract class TargaPixel {
		public abstract void read(IoBuffer buffer, int bytes) throws IOException;
	}

	public void readFile(String filePath) throws IOException {
		
		fileName = filePath;
		FileInputStream inputStream = new FileInputStream("clientdata/" + filePath);
		IoBuffer buffer = IoBuffer.allocate(inputStream.available(), false);
		
		buffer.setAutoExpand(true);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		byte[] buf = new byte[1024];
		
		for (int i = inputStream.read(buf); i != -1; i = inputStream.read(buf)) {
			buffer.put(buf, 0, i);
		}
		
		buffer.flip();
		
		idlength = buffer.get();
		colourmaptype = buffer.get();
		datatypecode = buffer.get();
		colourmaporigin = buffer.getShort();
		colourmaplength = buffer.getShort();
		colourmapdepth = buffer.get();
		x_origin = buffer.getShort();
		y_origin = buffer.getShort();
		width = buffer.getShort();
		height = buffer.getShort();
		bitsperpixel = buffer.get();
		imagedescriptor = buffer.get();
		
		int length = 0;
		
		if(width == height)
			length = width * height;
		else if(width > height)
			length = width * width;
		else if(height > width)
			length = height * height;

		pixelData = new TargaPixel[length];
			
		for (int i=0; i < length; i++) {
			TargaPixel pixel = null;
			switch(datatypecode) {
				case 3:
					pixel = new TargaBlackPixel();
					break;
				case 2:
				case 10:
					pixel = new TargaColorPixel();
					break;
			}
			pixelData[i] = pixel;
		}
		
		int skiplength = idlength + (colourmaptype * colourmaplength);
		buffer.skip(skiplength);
		System.out.println("Skipping: " + skiplength + " file: " + filePath);
		int bytes = bitsperpixel / 8;
		//System.out.println(pixelData.length);
		if(width <= height) {
			for (int i = width - 1; i >= 0; --i) {
				for (int j = 0; j < height; ++j) {
					pixelData[i * width + j].read(buffer, bytes);
				}
			}
		} else {
			for (int i = height - 1; i >= 0; --i) {
				for (int j = 0; j < width; ++j) {
					pixelData[i * height + j].read(buffer, bytes);
				}
			}

		}
		inputStream.close();
	}
	
	public short getWidth() {
		return width;
	}
	
	public short getHeight() {
		return height;
	}
	
	public char getData(int offset) {
		
		if (offset < 0 || offset >= width * height)
			throw new ArrayIndexOutOfBoundsException(offset);
		char value = ((TargaBlackPixel) pixelData[offset]).val;
		//System.out.println("TGA pixel value: " + (byte) value);
		return value;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
