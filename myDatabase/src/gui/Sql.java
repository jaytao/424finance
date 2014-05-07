package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.MysqlDataTruncation;

import db.Queries;
import db.Utils;

public class Sql {
	static Connection connection = null;
	PreparedStatement statement = null;

	String lastDate = "2100-12-31";
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
	
	public double hightestQuote(Connection connection, String stock) {
		String input = "select t.maxt from" +  
				  "(select ticker, max(adjclose) as maxt from quotes where ticker = ? group by ticker) as t;";
		try {
			double rs = -1;
			statement = connection.prepareStatement(input);
			statement.setString(1, stock);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				rs = result.getDouble("maxt");
			}
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;

	}
	
	
	public double lowestQuote(Connection connection, String stock) {
		String input = "select t.mint from" +  
				  "(select ticker, min(adjclose) as mint from quotes where ticker = ? group by ticker) as t;";
		try {
			double rs = -1;
			statement = connection.prepareStatement(input);
			statement.setString(1, stock);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				rs = result.getDouble("mint");
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
				try{
					statement3.executeUpdate();
				}catch(MysqlDataTruncation e){
					
				}

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

	public ResultSet stockRateOfReturn(Connection connection, String ticker, String begin, String end) {
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
			if (rs.next()) {
				return rs;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return null;
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
//
//	public double portfolioWorth(Connection connection, String fund,  String date) {
//		String input =  
//				"select fund, value, max(time) as maxt from value where time <? and fund =? group by fund as t;";
//		 
//
//			
//			double value1 = Utils.fundCurrentValue(connection, fund, date); 
//			
//	}
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
		double start = Utils.fundCurrentValue(connection, fund, begin);
		System.out.println("--------------------------------");
		System.out.println("Start: " + start);
//		if(start == -1.0) {
//			return -100.0;
//		}
		double total = Utils.fundCurrentValue(connection, fund, end);
		System.out.println("End: " + total);
//		if(total == 0.0) {
//			return -100.0;
//		}
		double contains = portfolioContainsPercent(connection, fund, end);
		
		System.out.println("Percent owned: " + contains);
		return (total*(1-contains))/start;
	}

	
	public ResultSet mysteryQuery(Connection connection){
		String query = "select c.individual, c.portfolio, c.date_order, c.percent from " +
				"(select individual, portfolio, max(date_order) latest_d from contains where portfolio=? group by individual) a, " +
				"contains c where c.individual=a.individual and c.portfolio=a.portfolio and " +
				"c.date_order=a.latest_d order by c.percent limit 1;";
		           
		String insert = "insert ignore into mystery values (?,?) ";
		
		String mystery = "select * from mystery order by value desc;";
		
		String remove = "delete from mystery";
		
		ResultSet rsFunds = Queries.getFund(connection);
		try {
			PreparedStatement stQuery = connection.prepareStatement(query);
			PreparedStatement stRemove = connection.prepareStatement(remove);
			PreparedStatement stInsert = connection.prepareStatement(insert);
			PreparedStatement stMystery = connection.prepareStatement(mystery);
			
			stRemove.executeUpdate();
			
			//go through all funds and run query
			while(rsFunds.next()){
				stQuery.setString(1, rsFunds.getString(1));
				ResultSet rsQuery = stQuery.executeQuery();
				
				//if returned a majority individual
				if(rsQuery.next()){
					
					//make sure its not 0
					if (rsQuery.getDouble(4) > 0.0){
						String ind = rsQuery.getString(1);
						double amount = Utils.fundCurrentValue(connection, ind, rsQuery.getString(3));
						
						stInsert.setString(1, ind);
						stInsert.setDouble(2, amount);
						
						stInsert.executeUpdate();
					}
				}
			}
			
			ResultSet rsMystery = stMystery.executeQuery();
			return rsMystery;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return null;
	}
			
	public ResultSet rankPortROR(Connection connection, boolean i) {
		String s1 = "select name from fund where isindividual = ? ";
		String s11 = "delete from rankPortROR";
		//String s2 = "create table rankPortROR(fund varchar(10), rateOfReturn dec(50, 25));";
		try {
			PreparedStatement statement1 = connection.prepareStatement(s1);
			statement1.setBoolean(1, i);
			PreparedStatement statement11 = connection.prepareStatement(s11);
			//PreparedStatement statement2 = connection.prepareStatement(s2);
			
			ResultSet result1 = statement1.executeQuery();
			statement11.executeUpdate();
			//statement2.executeUpdate();
			while(result1.next()) {
				String fundName = result1.getString("name");
				String startD = "select min(time) from value where fund = ?;";
				PreparedStatement state = connection.prepareStatement(startD);
				state.setString(1, fundName);
				ResultSet rst = state.executeQuery();
				String time = "";
				if(rst.next()) {
					time = rst.getString("min(time)");
				}
				System.out.println(time);
				double ror = portfolioRateOfReturn(connection, fundName, time, "2013-12-31");
				if (ror < 0){
					continue;
				}
				String s3 = "insert into rankPortROR values(?,?)";
				PreparedStatement statement3 = connection.prepareStatement(s3);
				statement3.setString(1, fundName);
				statement3.setDouble(2, ror);
				try{
					statement3.executeUpdate();
				}catch(MysqlDataTruncation e){
					
				}
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

	public ResultSet compareSet(String stock1, String stock2) {
		return null;
	}

//	public static void main(String args[]){
//		Sql s = new Sql();
//		double a = s.portfolioWorthEnd(Utils.connectToSQL("root", "toor"), "fund_10");
//		System.out.println(a);
//	}
}
