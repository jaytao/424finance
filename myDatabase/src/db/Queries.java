package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Queries {
	private static final String QUOTES_APPRECIATION = "(select * " +
			"from quotes where ticker=? and time >=? order " +
			"by time asc limit 1) union (select * from quotes where " +
			"ticker=? and time>=? order by time asc limit 1)";
	
	public static final String OWNS_FUND_VALUE = "select * from owns " +
			"where fund=? and date_order<=? order by date_execute desc;";
	
	//return ratio of stock appreciation between two dates
	public static double stockAppreciation(Connection connection,
			String ticker, String date1, String date2) {
		
		try {
			PreparedStatement st = connection.prepareStatement(Queries.QUOTES_APPRECIATION);
			st.setString(1, ticker);
			st.setString(2, date1);
			st.setString(3, ticker);
			st.setString(4, date2);
			
			ResultSet result = st.executeQuery();
			
			result.next();
			double price1 = result.getDouble(3);
			result.next();
			double price2 = result.getDouble(3);
			
			return price2/price1;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return 0;
	}
}