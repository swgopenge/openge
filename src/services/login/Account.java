package services.login;

import java.util.Vector;

import database.odb.PersistentObject;

public class Account implements PersistentObject {
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String userName;
	private String passwordHash;
	private boolean isBanned = false;
	private Vector<Long> characters = new Vector<Long>();
	
	public Account(long id, String userName, String passwordHash) {
		this.id = id;
		this.userName = userName;
		this.passwordHash = passwordHash;		
	}

	@Override
	public int getPersistenceLevel() {
		return 1;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public int getNumberOfCharacters() {
		return characters.size();
	}
	
	public void addCharacter(long id) {
		characters.add(id);
	}
	
	public void removeCharacter(long id) {
		characters.remove(id);
	}

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}
	
	public Vector<Long> getCharacters() {
		return characters;
	}

}
