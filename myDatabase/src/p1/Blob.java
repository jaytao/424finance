package p1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
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
			connection = DriverManager.getConnection("jdbc:mysql://Localhost/myDatabase", "root", "ps");
		}catch(Exception e) {
			System.out.println("Error Occured While connecting " + e);
		}
		return connection;
	}
	

	public void insertFile() {
		Connection connection = null;
		PreparedStatement statement = null;
		FileInputStream inputStream = null;
	
	
		try {
			File file = new File("/home/xwang125/input.txt");
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
	
	public void retrieveFile() throws SQLException, IOException {
		Connection connection = null;
		PreparedStatement statement = null;
		FileInputStream inputStream = null;
		String select = "select pic from myBlob;";
		connection = getConnection();
		PreparedStatement st = connection.prepareStatement(select);
		ResultSet rs = st.executeQuery();
		String total = "";
		File output = new File("/home/xwang125/output.txt");
		FileWriter fw = new FileWriter(output.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		while(rs.next()) {
			java.sql.Blob myPic = rs.getBlob("pic");
			java.io.InputStream myInputStream = myPic.getBinaryStream();
			BufferedReader in = new BufferedReader(new java.io.InputStreamReader(myInputStream));
			
			String str;
			while((str = in.readLine()) != null) {
				bw.write(str);
			}
			myInputStream.close();
			bw.close();
		}
		
	}	
		public static void main(String[] args) throws  SQLException, IOException{
		Blob testBlob = new Blob();
		System.out.println("Hello");
		testBlob.retrieveFile();
	}
}
