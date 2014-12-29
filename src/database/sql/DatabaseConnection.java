package database.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
	
	private String host, database, user, pass;
	private Connection connection = null;
	private Statement statement = null;
	private String type;	// Ziggy: TODO DatabaseType enumeration
	
	public DatabaseConnection(String host, String database, String user, String pass, String type) {
		this.host = host;
		this.database = database;
		this.user = user;
		this.pass = pass;
		this.type = type;
		
	}
	
	public boolean connect() {
		ResultSet resultSet = null;
		String connectionStr = 
				"jdbc:"
				+ type 
				+ "://" 
				+ host 
				+ "/"
				+ database
				+ (type.equals("mysql") ? "?autoReconnect=true" : "")
				+ user
				+ pass;
		
		try {			
			connection = DriverManager.getConnection(connectionStr);
			statement = connection.createStatement();
			resultSet = statement.executeQuery("SELECT VERSION()");
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) 
					resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return false;
		
	}
	
	public void close() {
		
		try {
			if (statement != null) 
				statement.close();
			if (connection != null) 
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public ResultSet query(String query) {
		
		if (statement == null) 
			return null;
		
		try {
			synchronized (statement) {
				return statement.executeQuery(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	public PreparedStatement preparedStatement(String statement) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(statement);
		try {
			//preparedStatement.setQueryTimeout(30);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return preparedStatement;
	}

}
