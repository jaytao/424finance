package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;

public class Utils {
	public static Connection connectToSQL(String user, String password) {
		try {
			Connection c = DriverManager.getConnection("jdbc:mysql://Localhost/stocks", user, password);
			return c;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static final String QUOTES_EXECUTE_DATE = "select * from quotes where ticker=? and time>=? order by (time) limit 1;";
	// get the real date to buy or sell, finding match date from quotes
	public static String findExecuteDate(Connection connection, String company, String time){

		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(QUOTES_EXECUTE_DATE);
			statement.setString(1, company);
			statement.setString(2, time);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getString("time");
			} else {
				return "2013-12-31";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}