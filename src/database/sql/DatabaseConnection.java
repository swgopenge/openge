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
	private String type;
	
	public DatabaseConnection() { }
	public DatabaseConnection(String database, String user, String pass, String type) {
		
		this.database = database;
		this.user = user;
		this.pass = pass;
		this.type = type;
		
	}
	
	public boolean connect() {
		
		if (database == null || user == null || pass == null)
			return false;
		
		return connect(host, database, user, pass, type);
		
	}
	public boolean connect(String host, String database, String user, String pass, String type) {
		
		ResultSet resultSet = null;
		
		try {
			if(type == "mysql") {
				connection = DriverManager.getConnection("jdbc:" + type + "://" + host + "/" + database + "?autoReconnect=true", user, pass);
				statement = connection.createStatement();
				resultSet = statement.executeQuery("SELECT VERSION()");
				return true;
			} else {
				connection = DriverManager.getConnection("jdbc:" + type + "://" + host + "/" + database, user, pass);
				statement = connection.createStatement();
				resultSet = statement.executeQuery("SELECT VERSION()");
				return true;
			}
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
