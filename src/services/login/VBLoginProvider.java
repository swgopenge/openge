package services.login;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import database.sql.DatabaseConnection;

public class VBLoginProvider implements ILoginProvider {

	protected DatabaseConnection databaseConnection;
	protected DatabaseConnection databaseConnection2;
	
	public VBLoginProvider(DatabaseConnection databaseConnection, DatabaseConnection databaseConnection2) {
		this.databaseConnection = databaseConnection;
		this.databaseConnection2 = databaseConnection2;
	}

	@Override
	public long getAccountId(String username, String password, String remoteAddress) {
		return getAccount(username, password, remoteAddress);
	}
		
	private long getAccount(String username, String password, String remoteAddress) {
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		
		try {
			
			// this sucks a bit, MySQL doesn't support passing arrays (yet) apparently.
			preparedStatement = databaseConnection2.preparedStatement("SELECT u.userid, u.email FROM user u WHERE LOWER(u.username)=LOWER(?) AND usergroupid NOT IN(?,?,?,?,?) AND MD5(CONCAT(MD5(?),u.salt))=u.password");
			preparedStatement.setString(1, username);
			preparedStatement.setInt(2, 1); // Guests
			preparedStatement.setInt(3, 3); // Awaiting Email Confirmation
			preparedStatement.setInt(4, 4); // Awaiting Moderation
			preparedStatement.setInt(5, 233); // Banned Users
			preparedStatement.setInt(6, 210); // Inactive Users
			preparedStatement.setString(7, password);
		
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
			{
				
				int accId = resultSet.getInt("u.userid");
				
				if (checkBanlistforUser(accId) || checkBanlistforIP(remoteAddress)) {
					return -3;
				}
				
				if (!checkIfAccountExistinGameDB(accId, username)) {
						//FIXME: remove null constraint from passwords. passwords are not to be imported through the game server.
						createAccountForGameDB(username, resultSet.getInt("u.userid"), resultSet.getString("u.email"), new BigInteger(130, new Random()).toString(32));
				}
				
				return accId;
						
			} else {
				return -2;
			}
			
		} catch (SQLException e) {
			return -1;
		}
		
	}
	
	
	private boolean checkBanlistforIP(String address)
	{
		
		if (databaseConnection2 == null) { return false; }
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		
		try
		{
			preparedStatement = databaseConnection2.preparedStatement("SELECT ipbanid FROM ipban WHERE ip=(inet_aton(?) &  power(2,32) - power(2, (32-cidr)) )");
			
			preparedStatement.setString(1, address);
			//System.out.println(preparedStatement);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet.next()) {
				return true;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			
		}
		
		return false;
	}
	
	private boolean checkBanlistforUser(int accountId)
	{
		
		if (databaseConnection2 == null) { return false; }
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		
		try
		{
			preparedStatement = databaseConnection2.preparedStatement("SELECT userid FROM userban WHERE userid=?");
			preparedStatement.setInt(1, accountId);
			resultSet = preparedStatement.executeQuery();
			//System.out.println(preparedStatement);
			if (resultSet.next()) { return true; }
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void createAccountForGameDB(String username, int id, String email, String pass)
	{
		PreparedStatement ps;
		try
		{
			ps = databaseConnection.preparedStatement("INSERT INTO accounts (id, \"user\", \"email\", \"pass\") VALUES (?, ?, ?, ?)");
			ps.setInt(1, id);
			ps.setString(2, username);
			ps.setString(3, email);
			ps.setString(4, pass);
			ps.executeUpdate();
			ps.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private boolean checkIfAccountExistinGameDB(int accId, String username)
	{
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		boolean success = false;
		boolean needRename = false;
		String vbUsername = "undef";
		// = username;
		//String coreUsername = "";
		
		try
		{
			preparedStatement = databaseConnection.preparedStatement("SELECT \"id\",\"user\" FROM \"accounts\" WHERE \"id\"=?");
			// preparedStatement.setString(1, client.getAccountName());
			preparedStatement.setInt(1, accId);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
			{
				vbUsername = resultSet.getString("user"); 
				success = true;
				if (!username.equalsIgnoreCase(vbUsername)) {
					needRename = true;
				}
				
			}
			preparedStatement.close();
			if (needRename)
			{
				System.out.println("userid needs renaming: AccId " + accId + " , core name: " + username + " , vB name: " + vbUsername);
				preparedStatement = databaseConnection.preparedStatement("UPDATE \"accounts\" SET \"user\"=? WHERE \"id\"=?");
				preparedStatement.setString(1, vbUsername);
				preparedStatement.setLong(2, accId);
				preparedStatement.executeUpdate();
				
			}
			preparedStatement.close();
			
			if (success) { return true; }
			
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	
	
}
