package services.login;

import database.odb.PersistentObject;

public class Account implements PersistentObject {
	
	private static final long serialVersionUID = 1L;
	private long accountId;
	private String userName;
	private String passwordHash;

	@Override
	public int getPersistenceLevel() {
		return 1;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
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

}
