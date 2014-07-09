package clientdata.visitors;

import java.nio.charset.Charset;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;

public class AppearanceVisitor implements VisitorInterface {
	
	private String childFilename;

	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {
		if(nodename.equals("0000NAME")) {
			setChildFilename(data.getString(Charset.forName("US-ASCII").newDecoder()));
		}
	}

	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public String getChildFilename() {
		return childFilename;
	}

	public void setChildFilename(String childFilename) {
		this.childFilename = childFilename;
	}

}
