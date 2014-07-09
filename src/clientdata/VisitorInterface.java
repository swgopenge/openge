package clientdata;

import org.apache.mina.core.buffer.IoBuffer;

public interface VisitorInterface {

	/**
	 * 
	 * Called when a new node that contains data is found in the client file.
	 * 
	 * @param nodename the name of the node found
	 * @param data the data found inside the node
	 * @param depth the current depth inside the client file's tree
	 */
	public void parseData(String nodename, IoBuffer data, int depth, int size) throws Exception;
	
	/**
	 * 
	 * Called when a new node that contains nodes is found in the client file.
	 * 
	 * @param nodeName the name of the node found
	 * @param depth the current depth inside a client file's tree
	 */
	public void notifyFolder(String nodeName, int depth) throws Exception;
	
}
