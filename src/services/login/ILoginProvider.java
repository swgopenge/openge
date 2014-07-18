package services.login;

public interface ILoginProvider {

	/*
	 * Returns the account ID.
	 * -1 is "DB error"
	 * -2 is "invalid user"
	 * -3 is "invalid password"
	 * -4 is "banned"
	 */
	public int getAccountId(String username, String password, String remoteAddress);
	
}
