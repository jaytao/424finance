package p1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Blob {
	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}catch (ClassNotFoundException e){
			 System.out.println("No jdbc found");			
		}
		try {
			connection = DriverManager.getConnection("jdbc:mysql://Localhost/myDatabase", "root", "dingding1016");
		}catch(Exception e) {
			System.out.println("Error Occured While connecting " + e);
		}
		return connection;
	}
	
	public void select() 
	{
		Connection connection = null;
		PreparedStatement statement = null;
		//FileInputStream inputStream = null;
try {
//			String select = "select * from people;";
//			connection = getConnection();
//			statement = connection.createStatement();
//			ResultSet result = statement.executeQuery(select);
//			System.out.println(result);
			//String name = "Kate";
			String insert = "insert into people values (?, ?)";
			System.out.println(insert);
			connection = getConnection();
			statement = connection.prepareStatement(insert);
			//String name = "(\"Alex\", 20);";
			statement.setString(1, "Xu");
		    statement.setInt(2, 20);
		    System.out.println(statement);
			statement.executeUpdate();
		}catch (SQLException e) {
			System.out.println("SQLException: " + e);
		}finally {
			try {
				connection.close();
				statement.close();
			}catch (SQLException e) {
				System.out.println("SQLException: " + e);
			}
		}
		
	}
	public void insertFile() {
		Connection connection = null;
		PreparedStatement statement = null;
		FileInputStream inputStream = null;
	
	
		try {
			File file = new File("/home/xwang125/sample.txt");
			inputStream = new FileInputStream(file);
			String insert = "insert into myBlob values(1, ?, ?)";
			connection = getConnection();
			statement = connection.prepareStatement(insert);
			statement.setString(1, "image1");
		    statement.setBinaryStream(2,  (InputStream)inputStream, (int)file.length());
			statement.executeUpdate();
		}catch (FileNotFoundException e){
			System.out.println("File Not Found: " + e);
		}catch (SQLException e) {
			System.out.println("SQLException: " + e);
		}finally {
			try {
				connection.close();
				statement.close();
			}catch (SQLException e) {
				System.out.println("SQLException: " + e);
			}
		}
	}
	
	public static void main(String[] args) throws  SQLException{
		Blob testBlob = new Blob();
		System.out.println("Hello");
		testBlob.select();
	}
}
