package database.odb;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.CheckpointConfig;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.persist.EntityStore;

@SuppressWarnings("all")
public class ObjectDatabase implements Runnable {
	
	private Environment environment;
	private EnvironmentConfig EnvConfig;
	private DatabaseConfig dbConfig;
	private EntityStore entityStore;
	private Thread checkpointThread;
	private CheckpointConfig checkpointConfig;
	private EntryBinding dataBinding;
	private StoredClassCatalog classCatalog;
	private Database db;
	private Database classCatalogDb;
	private Vector<Cursor> cursors = new Vector<Cursor>();
	private static boolean debugObjects = false;
	
	public ObjectDatabase(String name, boolean allowCreate, boolean useCheckpointThread, boolean allowTransactional, Class targetClass) {
		
		EnvConfig = new EnvironmentConfig();
		EnvConfig.setAllowCreate(allowCreate);
		//EnvConfig.setTransactional(allowTransactional);
		
		/*EntityModel model = new AnnotationModel();
		model.registerClass(CopyOnWriteArrayListProxy.class);
		model.registerClass(MultimapProxy.class);
		model.registerClass(VectorProxy.class);

		Mutations mutation = new Mutations();
		mutation.addDeleter(new Deleter(CreatureObject.class.getName(), 0, "performanceAudience"));		
	    StoreConfig storeConfig = new StoreConfig();
	    storeConfig.setModel(model);
	    storeConfig.setAllowCreate(allowCreate);
	    storeConfig.setTransactional(allowTransactional);
	    storeConfig.setMutations(mutation);*/
	    
        environment = new Environment(new File(".", "odb/" + name), EnvConfig);
        //entityStore = new EntityStore(environment, "EntityStore." + name, storeConfig);
        
        dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
       // dbConfig.setTransactional(false);
        
		classCatalogDb =
        		environment.openDatabase(null,
                                   "ClassCatalogDB",
                                   dbConfig);

            // Create our class catalog
        classCatalog = new StoredClassCatalog(classCatalogDb);
        dataBinding = new SerialBinding(classCatalog, targetClass);
		db = environment.openDatabase(null, name, dbConfig);       
		if (useCheckpointThread) {
        	checkpointConfig = new CheckpointConfig();
        	checkpointThread = new Thread(this);       
        	checkpointThread.start();
        }


	}
	
	public void put(Long key, Object value) {
        DatabaseEntry theKey = new DatabaseEntry();    
        theKey.setData(ByteBuffer.allocate(8).putLong(key).array());
        DatabaseEntry theData = new DatabaseEntry();
        dataBinding.objectToEntry(value, theData);
		db.put(null, theKey, theData);
		if (debugObjects) {
			debugObject(value);
			
			if (get(key) == null) {
				System.out.println("Failed to save " + value.getClass().getSimpleName() + " to database (Key: " + key + ").");
			}
		}
	}
	
	public void put(String key, Object value) {
        DatabaseEntry theKey = new DatabaseEntry();    
        theKey.setData(key.getBytes());
        DatabaseEntry theData = new DatabaseEntry();
        dataBinding.objectToEntry(value, theData);
		db.put(null, theKey, theData);
		if (debugObjects) {
			debugObject(value);
			
			if (get(key) == null) {
				System.out.println("Failed to save " + value.getClass().getSimpleName() + " to database (Key: " + key + ").");
			}
		}
	}
	
	public Object get(Long key) {
		if(!contains(key))
			return null;
        DatabaseEntry theKey = new DatabaseEntry();    
        theKey.setData(ByteBuffer.allocate(8).putLong(key).array());
        DatabaseEntry theData = new DatabaseEntry();
        db.get(null, theKey, theData, LockMode.DEFAULT);
        // Recreate the object from the retrieved DatabaseEntry using the EntryBinding 
        Object obj = dataBinding.entryToObject(theData);
       /* if(obj instanceof SWGObject) {
        	((SWGObject) obj).initializeBaselines(); ((SWGObject) obj).initAfterDBLoad();
        	((SWGObject) obj).viewChildren((SWGObject) obj, true, true, child -> {  child.initializeBaselines(); child.initAfterDBLoad(); });
        }*/
        return obj;
	}
	
	public Object get(String key) {
		if(!contains(key))
			return null;
        DatabaseEntry theKey = new DatabaseEntry();    
        theKey.setData(key.getBytes());
        DatabaseEntry theData = new DatabaseEntry();
        db.get(null, theKey, theData, LockMode.DEFAULT);
        // Recreate the object from the retrieved DatabaseEntry using the EntryBinding 
        return dataBinding.entryToObject(theData);
	}

	public void remove(Long key) {
        DatabaseEntry theKey = new DatabaseEntry();    
        theKey.setData(ByteBuffer.allocate(8).putLong(key).array());
		db.removeSequence(null, theKey);
	}
	
	public ODBCursor getCursor() {
		Cursor cursor = db.openCursor(null, null);
		cursors.add(cursor);
		return new ODBCursor(cursor, dataBinding, this);
	}
	
	public boolean contains(Long key) {
        DatabaseEntry theKey = new DatabaseEntry();    
        theKey.setData(ByteBuffer.allocate(8).putLong(key).array());
        DatabaseEntry theData = new DatabaseEntry();
        return db.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS;
	}
	
	public boolean contains(String key) {
        DatabaseEntry theKey = new DatabaseEntry();    
        theKey.setData(key.getBytes());
        DatabaseEntry theData = new DatabaseEntry();
        return db.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS;
	}

	
	public Environment getEnvironment() { return environment; }
		
	public void compress() {
		environment.compress();
	}
	
    public void close() {
        if (environment != null) {
            try {
            	cursors.forEach(Cursor::close);
				environment.sync();          	
            	db.close();
            	classCatalogDb.close();
            	environment.close();
            } catch(DatabaseException dbe) {
                System.err.println("Error closing environment" + 
                     dbe.toString());
            }
        }
    }
    
	@Override
	public void run() {
		while(environment != null && environment.isValid()) {
			try {
				Thread.sleep(300000);
				environment.sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Vector<Cursor> getCursors() {
		return cursors;
	}
	
	/*
	 * @description Prints any classes that don't implement Serializable.
	 */
	public static void debugObject(Object object) {
		if (debugObjects) {
			Set<String> classes = new HashSet<String>();
			
			debugMapUnserializableClasses(classes, object.getClass());
			
			for (String type : classes) {
				System.err.println(type + " does not implement Serializable.");
			}
		}
	}
	
	public static void debugMapUnserializableClasses(Set<String> classes, Class<?> object) {
		boolean serializable = false;
		
		for (Type type : object.getGenericInterfaces()) {
			if (type != null && type.getTypeName().equals("Serializable")) {
				serializable = true;
			}
		}
		
		if (!serializable) {
			classes.add(object.getSimpleName());
			debugObjects = false;
		}
		
		for (Field field : object.getDeclaredFields()) {
			if (field.getGenericType().getTypeName().contains("<")) {
				try {
					String genericType = field.getGenericType().getTypeName().split("<", field.getGenericType().getTypeName().lastIndexOf("<"))[1].replace(">", "");
					debugMapUnserializableClasses(classes, Class.forName(genericType));
				} catch (Exception e) {
					
				}
			}
			
			if (!field.getClass().getPackage().getName().startsWith("engine") &&
			!field.getClass().getPackage().getName().startsWith("main") &&
			!field.getClass().getPackage().getName().startsWith("protocol") &&
			!field.getClass().getPackage().getName().startsWith("resources") &&
			!field.getClass().getPackage().getName().startsWith("service")) {
				continue;
			}
			
			debugMapUnserializableClasses(classes, field.getClass());
		}
	}
	
}
