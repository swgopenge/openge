package database.odb;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class ODBCursor {
	
	private Cursor cursor;
	private EntryBinding dataBinding;
	private ObjectDatabase odb;
	
	public ODBCursor(Cursor cursor, EntryBinding dataBinding, ObjectDatabase odb) {
		this.cursor = cursor;
		this.dataBinding = dataBinding;
		this.odb = odb;
	}
	
	public Object next() {
        DatabaseEntry theKey = new DatabaseEntry();    
        DatabaseEntry theData = new DatabaseEntry();
		if(cursor.getNext(theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			Object obj = dataBinding.entryToObject(theData);
	       /* if(obj instanceof SWGObject) {
	        	((SWGObject) obj).initializeBaselines(); ((SWGObject) obj).initAfterDBLoad();
	        	((SWGObject) obj).viewChildren((SWGObject) obj, true, true, child -> { 
	        		if(child != null) {
		        		child.initializeBaselines(); 
		        		child.initAfterDBLoad(); 
	        		}
	        	});
	        }*/
			return obj;
		}
		else 
			return null;
		
	}
	
	public void close() {
		odb.getCursors().remove(cursor);
		cursor.close();
	}
	
	public boolean hasNext() {
        DatabaseEntry theKey = new DatabaseEntry();    
        DatabaseEntry theData = new DatabaseEntry();
		if(cursor.getNext(theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
			cursor.skipPrev(1, theKey, theData, LockMode.DEFAULT);
			return true;
		} else {
			return false;
		}

	}

}
