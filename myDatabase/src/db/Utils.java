package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
	public static String findExecuteDate(Connection connection, String company, String time) {

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

	// at time date, how much is fund worth if we appreciate all stocks to
	// present
	public static double fundCurrentValue(Connection connection, String fund, String date) {
		try {
			HashMap<String, Double> stockAmount = new HashMap<String, Double>();
			double fundValue = Queries.getFundTotalValue(connection, fund, date);
			System.out.println("fund:" + fundValue);
			double newFundValue = 0;

			ResultSet rs = Queries.getFundOwnsStock(connection, fund);
			// update all stocks that fund owns (appreciate to present)
			while (rs.next()) {
				String ticker = rs.getString(2);
				double percent = rs.getDouble(3);
				if (stockAmount.containsKey(ticker)) {
					continue;
				}
				if (percent == 0.0) {
					stockAmount.put(ticker, 0.0);
					continue;
				}
				String dateBought = Queries.getStockDateBought(connection, fund, ticker);
				String exeDate = Utils.findExecuteDate(connection, ticker, date);
				double amount = percent * Queries.stockAppreciation(connection, ticker, dateBought, exeDate)
						* fundValue;
				stockAmount.put(ticker, amount);
				newFundValue += amount;
			}
			
			newFundValue += Queries.getCash(connection, fund, date);
			newFundValue += individualValueInFunds(connection, fund, date);

			if (newFundValue == 0) {
				return -1;
			}
			rs.close();
			return newFundValue;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// if a ind sells their share, then it increases the percent of fund other
	// individuals own
	public static void updateContainsSell(Connection connection, String ind, String fund, String date) {
		double modifier = 1.0 - Queries.getIndividualFundPercent(connection, ind, fund);

		try {
			ResultSet rs = Queries.getIndOwnsFund(connection, ind);
			HashSet<String> finished = new HashSet<String>();

			while (rs.next()) {
				String portfolio = rs.getString(2);
				double percent = rs.getDouble(3);
				if (finished.contains(portfolio)) {
					continue;
				}
				if (percent == 0.0) {
					finished.add(portfolio);
					continue;
				}
				// portfolio we are trying to sell
				if (portfolio.equals(fund)) {
					Queries.insertContains(connection, ind, portfolio, 0.0, date);
				} else {
					Queries.insertContains(connection, ind, portfolio, percent / modifier, date);
				}
				finished.add(portfolio);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void stockTop5Safe(Connection connection) {
		String query = "select a.ticker, a.adjclose, a.time, b.adjclose, b.time, (a.adjclose - b.adjclose)/a.adjclose d from quotes a, quotes b where a.ticker = b.ticker and a.time < b.time and a.adjclose > b.adjclose and a.ticker = ? order by (a.adjclose-b.adjclose)/a.adjclose desc limit 5;";
		Iterator<String> iter = Constants.COMPANIES.iterator();

		while (iter.hasNext()) {
			try {
				PreparedStatement st = connection.prepareStatement(query);
				st.setString(1, iter.next());

				ResultSet rs;
				rs = st.executeQuery();
				rs.next();
				System.out.println(rs.getString(1) + " " + rs.getString(3) + " " + rs.getString(5) + " "
						+ rs.getString(6));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public static double individualValueInFunds(Connection connection, String ind, String date){
		String query = "select a.individual, a.portfolio, c.percent from " +
				"(select individual, portfolio, max(date_order) d from contains where individual=? and date_order<=? group by portfolio) a, " +
				"(select * from contains) c where c.portfolio=a.portfolio and c.individual=a.individual and c.date_order=a.d;";
		double total = 0;
		try {
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, ind);
			st.setString(2, date);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()){
				String fund = rs.getString(2);
				double percent = rs.getDouble(3);
				
				double amount = percent * fundCurrentValue(connection, fund, date);
				System.out.println(fund);
				System.out.println(fundCurrentValue(connection, fund,date));	
				System.out.println(percent);
				System.out.println(amount);
				total += amount;
			}
			
			return total;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total;
	}

}
