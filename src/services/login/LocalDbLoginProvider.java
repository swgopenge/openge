package services.login;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import utils.PasswordEncryption;
import database.odb.ObjectDatabase;
import main.Core;

public class LocalDbLoginProvider implements ILoginProvider {
	
	private ObjectDatabase accountODB;

	public LocalDbLoginProvider(ObjectDatabase accountODB) {
		this.accountODB = accountODB;
	}

	@Override
	public long getAccountId(String username, String password, String remoteAddress) {
		Core core = Core.getInstance();
		boolean autoReg = core.loginService.isAutoRegistration();
		Account account = (Account) accountODB.get(username);
		String hash;
		try {
			hash = PasswordEncryption.generatePasswordHash(password);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return -3;
		}
		
		if(account == null && !autoReg) {
			return -2; 
		} else if(account != null) {
			if(account.isBanned())
				return -4;
			boolean passMatch = false;
			try {
				passMatch = PasswordEncryption.validatePassword(password, account.getPasswordHash());
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
				return -3;
			}
			return passMatch ? 0 : -3;
		} else {
			account = new Account(core.loginService.generateAccountId(), username, hash);
			accountODB.put(username, account);
		}
		
		return account.getId();
	}


}
