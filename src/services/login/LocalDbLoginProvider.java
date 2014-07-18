package services.login;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.sql.DatabaseConnection;

public class LocalDbLoginProvider implements ILoginProvider {

	
	DatabaseConnection databaseConnection;
	
	public LocalDbLoginProvider(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}
	
	//FIXME: now this would be a real use case for a DB function.
	//would condense things to like 1 line.
	@Override
	public int getAccountId(String username, String password, String remoteAddress) {
		return getAccount(username, password, remoteAddress);
	}
	
	private int getAccount(String username, String password, String remoteAddress) {
		//don't check bans, only check for cleartext passwords stored in DB
		try {
			while (true) {
				PreparedStatement ps = databaseConnection.preparedStatement("SELECT id FROM accounts WHERE LOWER(\"user\")=LOWER(?) AND \"pass\"=?");
				ps.setString(1, username);
				ps.setString(2, password);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return rs.getInt(1);
				}
			
				//lazy
				ps = databaseConnection.preparedStatement("SELECT id FROM accounts WHERE LOWER(\"user\")=LOWER(?)");
				ps.setString(1, username);
				rs = ps.executeQuery();
				if (rs.next()) {
					return -2;
				}
		
				//lazy... be nice if there was a db function to insert and retrieve in one go.
				//if you flood your DB by mistyping username, it's not my fault. :p
				ps = databaseConnection.preparedStatement("INSERT INTO accounts (\"user\",\"pass\") VALUES(LOWER(?),?)");
				ps.setString(1, username);
				ps.setString(2, password);
				rs = ps.executeQuery();
				//now this is lazy...
			}
		} catch (SQLException e) {
			return -1;
		}
	}
	
}
