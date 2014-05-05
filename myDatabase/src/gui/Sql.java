package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Sql {
	Connection connection = null;
	PreparedStatement statement = null;

	public Sql() {

	}

	public double stockQuote(Connection connection, String stock, String date) {
		String input = "select adjclose from quotes where ticker = ? and time = ?";
		try {
			double rs = 0.0;
			statement = connection.prepareStatement(input);
			statement.setString(1, stock);
			statement.setString(2, date);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				rs = result.getDouble("adjclose");
			}
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}

	public ResultSet portofolioTotalNetWorth(Connection connection, String startdate, String endDate) {
		String drop = "drop view temp";
		String input = "create view temp as (select fund,  max(time) as mt from value group by fund order by value);";
		String select = "select temp.fund, value.value from temp, value where temp.fund = value.fund and temp.mt = value.time\n "
				+ "order by value.value; ";
		try {
			statement = connection.prepareStatement(drop);
			int i = statement.executeUpdate();
			statement = connection.prepareStatement(input);
			int j = statement.executeUpdate();

			statement = connection.prepareStatement(select);
			ResultSet result = statement.executeQuery();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public double stockRateOfReturn(Connection connection, String ticker, String begin, String end) {
		String query = "SELECT b.adjclose/a.adjclose FROM"
				+ " (SELECT * FROM quotes where ticker=? and time>=? order by time asc limit 1) a,"
				+ " (select * from quotes where ticker=? and time>=? order by time asc limit 1) b;";

		PreparedStatement st;
		try {
			st = connection.prepareStatement(query);
			st.setString(1, ticker);
			st.setString(2, begin);
			st.setString(3, ticker);
			st.setString(4, end);

			ResultSet rs = st.executeQuery();
			rs.next();
			return rs.getDouble(1);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public ResultSet stockTop25Return(Connection connection) {
		String query = "select t Ticker, q1.time, q1.adjclose, q2.time, q2.adjclose, " +
				"q2.adjclose/q1.adjclose ror from quotes q1, quotes q2, " +
				"(select distinct ticker t from quotes) a where q1.ticker = q2.ticker " +
				"and q1.ticker = t and q1.time='2005-01-03' and q2.time='2012-10-12' " +
				"order by ror desc limit 25;";
		try {
			ResultSet rs;
			rs = connection.prepareStatement(query).executeQuery();
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultSet stockTop25Safe(Connection connection){
		String query = "select ticker, std(adjclose) std from quotes group by ticker order by std asc";
		try {
			ResultSet rs;
			rs = connection.prepareStatement(query).executeQuery();
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}