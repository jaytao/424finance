package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.Queries;
import db.Utils;

public class Sql {
	static Connection connection = null;
	PreparedStatement statement = null;

	String lastDate = "2013-12-31";
	String firstDate = "2005-01-03";
	public Sql() {

	}

	//	public static void main(String[] args) {
	//		connection = Utils.connectToSQL("root", "dingding1016"); 
	//		Sql sql = new Sql();
	//		sql.rankPortROR(connection);
	//	}
	public double stockQuote(Connection connection, String stock, String date) {
		String input = "select adjclose from quotes where ticker = ? and time = ?";
		try {
			double rs = -1;
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

	public ResultSet portofolioTotalNetWorth(Connection connection, int i, String startdate, String endDate) {
		String drop = "drop table temp";
		String s1 = "select name from fund where isindividual = ?";
		String s2 = "create table temp(fund varchar(10), value dec(50, 20))";


		try {
			PreparedStatement statement = connection.prepareStatement(drop);
			statement.executeUpdate();
			PreparedStatement statement1 = connection.prepareStatement(s1);
			statement1.setInt(1, i);
			PreparedStatement statement2 = connection.prepareStatement(s2);
			statement2.executeUpdate();
			ResultSet rt = statement1.executeQuery();
			while(rt.next()) {
				String fundName = rt.getString("name");
				double values = Utils.fundCurrentValue(connection, fundName, "2013-12-31");
				String s3 = "insert into temp values(?,?)";
				PreparedStatement statement3 = connection.prepareStatement(s3);
				System.out.println(fundName);
				System.out.println(values);
				statement3.setString(1, fundName);
				statement3.setDouble(2, values);
				statement3.executeUpdate();

			}
			String s4 = "select * from temp order by value";
			PreparedStatement statement4 = connection.prepareStatement(s4);
			ResultSet rt2 = statement4.executeQuery();
			return rt2;

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
				"pow(q2.adjclose/q1.adjclose,1/9)-1 AnnualRoR from quotes q1, quotes q2, " +
				"(select distinct ticker t from quotes) a where q1.ticker = q2.ticker " +
				"and q1.ticker=t and q1.time=? and q2.time=? " +
				"order by AnnualRoR desc limit 25;";
		System.out.println(query);
		try {
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, firstDate);
			st.setString(2, lastDate);
			ResultSet rs = st.executeQuery();
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

	public double portfolioWorth(Connection connection, String fund, String date) {
		String input = "select t.value from" + 
				"(select fund, value, max(time) from value where time <? and fund =? group by fund) as t";
		PreparedStatement st;
		try {

			st = connection.prepareStatement(input);
			st.setString(1, date);
			st.setString(2, fund);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				return rs.getDouble("value");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	public double portfolioWorthEnd(Connection connection, String fund){
		String query = "select a.ticker, a.d, c.percent, b.value, c.percent * b.value from " +
				"(select fund, ticker, max(date_execute) d from owns where fund=? group by ticker) a, " +
				"(select * from owns) c, (select * from value) b " +
				"where b.fund=a.fund and a.d=b.time and c.fund=a.fund and c.ticker=a.ticker and c.date_execute=a.d;";

		try{
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, fund);
			ResultSet rs = st.executeQuery();

			double totalWorth = 0.0;
			while (rs.next()){
				String ticker = rs.getString(1);
				String date = rs.getString(2);
				double amount = rs.getDouble(5);

				totalWorth += (amount*Queries.stockAppreciation(connection, ticker, date, lastDate));
			}
			return totalWorth;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}

	public double portfolioContainsPercent(Connection connection, String fund, String end){
		//this query returns the percent of fund that is owned by individuals
		String query = "select sum(percent) from " +
				"(select individual, portfolio, max(date_order) d from contains " +
				"where portfolio=? and date_order<=? group by individual) a, " +
				"(select * from contains) c " +
				"where c.portfolio=a.portfolio and c.individual=a.individual and c.date_order=a.d;";
		try{
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, fund);
			st.setString(2, end);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1.0;
	}

	//gets the portofolio total rate of return
	public double portfolioRateOfReturn(Connection connection, String fund, String begin, String end){
		double start = Queries.getFundTotalValue(connection, fund, begin);
//		if(start == -1.0) {
//			return -100.0;
//		}
		double total = portfolioWorthEnd(connection, fund);
//		if(total == 0.0) {
//			return -100.0;
//		}
		Queries.getCash(connection,fund, end);
		total += Queries.getCash(connection, fund, end);
		double contains = portfolioContainsPercent(connection, fund, end);
		System.out.println(fund);
		System.out.println(start);
		System.out.println(total);
		System.out.println(contains);
		return (total*(1-contains))/start;
	}

	public ResultSet rankPortROR(Connection connection, int i) {
		
		String s1 = "select name from fund where isindividual = ? ";
		String s11 = "drop table rankPortROR";
		String s2 = "create table rankPortROR(fund varchar(10), rateOfReturn dec(50, 25));";
		try {
			PreparedStatement statement1 = connection.prepareStatement(s1);
			statement1.setInt(1, i);
			PreparedStatement statement11 = connection.prepareStatement(s11);
			PreparedStatement statement2 = connection.prepareStatement(s2);
			ResultSet result1 = statement1.executeQuery();
			statement11.executeUpdate();
			statement2.executeUpdate();
			while(result1.next()) {
				String fundName = result1.getString("name");
				double ror = portfolioRateOfReturn(connection, fundName, "2005-01-01", "2013-12-31");
				String s3 = "insert into rankPortROR values(?,?)";
				PreparedStatement statement3 = connection.prepareStatement(s3);
				statement3.setString(1, fundName);
				statement3.setDouble(2, ror);
				statement3.executeUpdate();
			}

			String s4 = "select * from rankPortROR order by rateOfReturn";
			PreparedStatement statement4 = connection.prepareStatement(s4);
			ResultSet result4 = statement4.executeQuery();
			return result4;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

//	public ResultSet rankIndivROR(Connection connection) {
//		String s1 = "select action, col_1, col_2 from activity where action = 'individual';";
//		PreparedStatement statement1;
//		try {
//			statement1 = connection.prepareStatement(s1);
//			ResultSet rs1 = statement1.executeQuery();
//			while(rs1.next()) {
//				String indiv = rs1.getString("col_1");
//				double cash = rs1.getDouble("col_2");
//				double worthEnd = portfolioWorthEnd(connection, indiv);
//				double indivROR = worthEnd/cash;
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}

	public static void main(String args[]){
		Sql s = new Sql();
		double a = s.portfolioWorthEnd(Utils.connectToSQL("root", "toor"), "fund_10");
		System.out.println(a);
	}
}
