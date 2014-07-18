package clientdata;

import java.io.File;
import java.util.Map;
import utils.collections.NonBlockingHashMap;

public class ClientFileManager {

	private static ClientFileManager instance;
	
	private java.io.File baseFolder;
	private Map<String, VisitorInterface> loadedFiles;
	
	protected ClientFileManager(java.io.File baseFolder) {
		this.baseFolder = baseFolder;
		loadedFiles = new NonBlockingHashMap<String, VisitorInterface>();
	}
	
	/**
	 * 
	 * Loads a file from the disk using reflection with the given class.
	 * @param <T>
	 * 
	 * @param filename the swg pathname of the file to load
	 * @param type the class of the interpreter to use.
	 * 
	 * @return the interpreter created
	 */
	@SuppressWarnings("unchecked")
	public static <T extends VisitorInterface> T loadFile(String filename, Class<T> type) throws InstantiationException, IllegalAccessException {
		
		if(instance == null) {
			instance = new ClientFileManager(new java.io.File(".", "clientdata"));
		}
		
		T interpreter = (T) instance.loadedFiles.get(filename);
		if(interpreter == null) {
			interpreter = type.newInstance();
			File file = new File(instance.baseFolder.getAbsolutePath(), filename);
			file.setReadable(true);
			file.setExecutable(true);
			file.setWritable(true);
			IffFile.readFile(file.getAbsolutePath(), interpreter);
			instance.loadedFiles.put(filename, interpreter);
		}
		
		return interpreter;
	}
	
}
