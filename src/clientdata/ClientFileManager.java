package clientdata;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class ClientFileManager {

	private static ClientFileManager instance;
	
	private java.io.File baseFolder;
	private ConcurrentHashMap<String, VisitorInterface> loadedFiles;
	
	protected ClientFileManager(java.io.File baseFolder) {
		this.baseFolder = baseFolder;
		loadedFiles = new ConcurrentHashMap<String, VisitorInterface>();
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
	
	/*@SuppressWarnings("unchecked")
	public static <T extends VisitorInterface> T getFile(String filename) {
		return (T) instance.loadedFiles.get(filename);
	}*/
	
	/**
	 * 
	 * Loads an entire folder of files from the disk using the factory to create needed visitors
	 * 
	 * @param foldername the swg pathname of the folder to load.
	 * @param recursive if true, files in sub folders will be processed
	 * @param factory the factory which creates the visitors required
	 * @param extensions a list of strings of the extensions to include. Ex "iff" and "msh"
	 * @return a list of the visitors created during the loading.
	 */
	/*@SuppressWarnings("unchecked")
	public static <T extends VisitorInterface> List<T> loadFiles(String foldername, boolean recursive, VisitorFactoryInterface<T> factory, String... extensions) {
		
		if(instance == null) {
			instance = new FileManager(new java.io.File(".", "clientdata"));
		}
		
		ArrayList<T> visitors = new ArrayList<T>();
		
		java.io.File location = new java.io.File(instance.baseFolder, foldername);
		if(location.exists() && location.isDirectory()) {
			for(java.io.File f : FileUtils.listFiles(location, extensions, recursive)) {
				String name = f.getPath().replace("\\", "/").replace(instance.baseFolder+"/", "");
				
				T i = (T) instance.loadedFiles.get(name);
				if(i == null) {
					i = factory.getInstance();
					IffFile.readFile(f.getAbsolutePath(), i);
					instance.loadedFiles.put(name, i);
				}
				
				visitors.add(i);
			}
		}
		
		return visitors;
	}
	*/
	/**
	 * 
	 * Loads an entire folder of files from the disk using reflection to create needed visitors
	 * 
	 * @param foldername the swg pathname of the folder to load.
	 * @param recursive if true, files in sub folders will be processed
	 * @param type the class of the interpreter to use
	 * @param extensions a list of strings of the extensions to include. Ex "iff" and "msh"
	 * @return a list of the visitors created during the loading.
	 */
	/*@SuppressWarnings("unchecked")
	public static <T extends VisitorInterface> List<T> loadFiles(String foldername, boolean recursive, Class<T> type, String... extensions) throws InstantiationException, IllegalAccessException {
		
		if(instance == null) {
			instance = new FileManager(new java.io.File(".", "clientdata"));
		}
		
		ArrayList<T> visitors = new ArrayList<T>();
		
		java.io.File location = new java.io.File(instance.baseFolder, foldername);
		if(location.exists() && location.isDirectory()) {
			for(java.io.File f : FileUtils.listFiles(location, extensions, recursive)) {
				String name = f.getPath().replace("\\", "/").replace(instance.baseFolder+"/", "");
				
				T i = (T) instance.loadedFiles.get(name);
				if(i == null) {
					i = type.newInstance();
					IffFile.readFile(f.getAbsolutePath(), i);
					instance.loadedFiles.put(name, i);
				}
				
				visitors.add(i);
			}
		}
		
		return visitors;
	}*/
	
	/**
	 * @param basefolder the folder to use as the base folder if the file manager instance does not exist.
	 * @return the filemanager instance. If it does not exist, it will created using the given location
	 */
	public static ClientFileManager getInstance(java.io.File basefolder) {
		if(instance == null) {
			instance = new ClientFileManager(basefolder);
		}
		return instance;
	}
}
