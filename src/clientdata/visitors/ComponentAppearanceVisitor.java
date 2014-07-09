package clientdata.visitors;

import java.nio.charset.Charset;
import org.apache.mina.core.buffer.IoBuffer;
import clientdata.VisitorInterface;

public class ComponentAppearanceVisitor implements VisitorInterface {

	private String firstMesh;
	
	@Override
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception {
		if(nodename.equals("PART") && firstMesh == null) {
			firstMesh = data.getString(Charset.forName("US-ASCII").newDecoder());
		}
	}

	@Override
	public void notifyFolder(String nodeName, int depth) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public String getFirstMesh() {
		return firstMesh;
	}

	public void setFirstMesh(String firstMesh) {
		this.firstMesh = firstMesh;
	}

}
