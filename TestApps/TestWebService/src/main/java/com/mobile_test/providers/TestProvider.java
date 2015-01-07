package com.mobile_test.providers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mobile_test.models.Test;

public class TestProvider {
	
	public static Test getTestByID(int id) throws SQLException {
		Connection connection = null;
	
		try
		{
			connection = DBConnection.getConnection();
			PreparedStatement selectTestByID = connection.prepareStatement("SELECT testdata FROM test WHERE id = ?");
			selectTestByID.setInt(1, id);
			ResultSet result = selectTestByID.executeQuery();
			
			if (!result.next()) {
				return null;
			} else {
				String data = result.getString(1);
				return new Test(id, data);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int updateTest(Test test) throws SQLException {
		Connection connection = null;
		connection = DBConnection.getConnection();
		PreparedStatement statement = connection.prepareStatement("UPDATE test SET testdata=? WHERE id=?");
		statement.setString(1, test.getData());
		statement.setInt(2, test.getId());
		return statement.executeUpdate();
	}
	
	public static int insertTest(String data) throws SQLException {
		Connection connection = null;
		connection = DBConnection.getConnection();
		PreparedStatement statement = connection.prepareStatement("INSERT INTO test (testdata) VALUES (?)");
		statement.setString(1, data);
		return statement.executeUpdate();
		
	}
	
	public static int deleteTestByID(int id) throws SQLException {
		Connection connection = null;
		connection = DBConnection.getConnection();
		PreparedStatement statement = connection.prepareStatement("DELETE FROM testdata WHERE id=?");
		statement.setInt(1, id);
		return statement.executeUpdate();
	}
}
