package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

public class Utils {
	public static Connection connectToSQL(String user, String password) {
		try {
			Connection c = DriverManager.getConnection(
					"jdbc:mysql://Localhost/stocks", user, password);
			return c;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	// returns value of fund at that date
	public static double getFundValue(Connection connection,String fundName, String date) {
		double totalValue = 0;
		// cash

		// algorithm - query owns table for fund before date in descending
		// order.
		// go through and add each stock to a set. if amount > 0, calculate how
		// much worth now
		// if amount = 0, then ignore all instances that come after for that
		// fund
		try {
			PreparedStatement st = connection
					.prepareStatement(Queries.OWNS_FUND_VALUE);
			st.setString(1, fundName);
			st.setString(2, date);
			ResultSet result = st.executeQuery();
			HashSet<String> done = new HashSet<String>();

			while (result.next()) {
				String ticker = result.getString("ticker");
				System.out.println(ticker);
				double amount = result.getDouble("amount");
				String dateExe = result.getString("date_execute");
				System.out.println(dateExe);
				if (done.contains(ticker)) {
					continue;
				}
				if (amount == 0.0) {
					done.add(ticker);
				} else {
					totalValue += amount
							* Queries.stockAppreciation(connection, ticker,
									dateExe, date);
				}

			}
			return totalValue;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}