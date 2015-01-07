package com.mobile_test.providers;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	private static Connection[] connectionPool;
	private static final int CONNECTION_LIMIT = 8;
	private static int currentConnectionIndex = 0;
	
	static {
		connectionPool = new Connection[CONNECTION_LIMIT];
		
		for (int i = 0; i < CONNECTION_LIMIT; i++) {
			connectionPool[i] = createConnection();
		}
	}
	
	private static Connection createConnection() {
		
		try {
			URI dbUri = new URI(System.getenv("DATABASE_URL"));
			String username = dbUri.getUserInfo().split(":")[0];
		    String password = dbUri.getUserInfo().split(":")[1];
		    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

		    return DriverManager.getConnection(dbUrl, username, password);	
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static synchronized Connection getConnection() {
		Connection connection = connectionPool[currentConnectionIndex++];
		
		if (currentConnectionIndex >= CONNECTION_LIMIT) {
			currentConnectionIndex = 0;
		}
		
		return connection;
	}
	
}
