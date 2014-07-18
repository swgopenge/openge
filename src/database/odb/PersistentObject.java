package database.odb;

import java.io.Serializable;

public interface PersistentObject extends Serializable {
	
	public int getPersistenceLevel();

}
