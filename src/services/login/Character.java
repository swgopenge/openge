package services.login;

import protocol.swg.EnumerateCharacterId.CharacterType;
import database.odb.PersistentObject;

public class Character implements PersistentObject {
	
	private long objectID;
	private int appearanceCRC;
	private long accountID;
	private CharacterType type;
	private String name;
	
	public Character(long objectID, int appearanceCRC, long accountID, CharacterType type, String name) {
		this.setObjectID(objectID);
		this.setAppearanceCRC(appearanceCRC);
		this.setAccountID(accountID);
		this.setType(type);
		this.setName(name);
	}
	
	@Override
	public int getPersistenceLevel() {
		return 1;
	}

	public long getObjectID() {
		return objectID;
	}

	public void setObjectID(long objectID) {
		this.objectID = objectID;
	}

	public int getAppearanceCRC() {
		return appearanceCRC;
	}

	public void setAppearanceCRC(int appearanceCRC) {
		this.appearanceCRC = appearanceCRC;
	}

	public long getAccountID() {
		return accountID;
	}

	public void setAccountID(long accountID) {
		this.accountID = accountID;
	}

	public CharacterType getType() {
		return type;
	}

	public void setType(CharacterType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
