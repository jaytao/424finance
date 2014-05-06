package gui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.Queries;
import db.Utils;

public class Sql {
	Connection connection = null;
	PreparedStatement statement = null;
	String lastDate = "2013-12-31";
	String firstDate = "2005-01-03";
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
				"pow(q2.adjclose/q1.adjclose,1/9)-1 AnnualRoR from quotes q1, quotes q2, " +
				"(select distinct ticker t from quotes) a where q1.ticker = q2.ticker " +
				"and q1.ticker=t and q1.time=? and q2.time=? " +
				"order by ror desc limit 25;";
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
	
	public double portfolioWorthEnd(Connection connection, String fund){
		String query = "select a.ticker, a.d, c.percent, b.value, c.percent * b.value from " +
				"(select fund, ticker, max(date_execute) d from owns where fund=? group by ticker) a, " +
				"(select * from owns) c, (select * from value) b " +
				"where b.fund=a.fund and a.d=b.time and c.fund=a.fund and c.ticker=a.ticker and c.date_execute=a.d;";
		
		try{
			PreparedStatement st = connection.prepareStatement(query);
			st.setString(1, fund);
			ResultSet rs = st.executeQuery();
			
			double totalWorth = 0;
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
			
			return rs.getDouble(1);
			
		} catch (SQLException e){
			e.printStackTrace();
		}
		return -1;
	}
	
	//gets the portofolio total rate of return
	public double portfolioRateOfReturn(Connection connection, String fund, String begin, String end){
		double start = Queries.getFundTotalValue(connection, fund, begin);
		double total = portfolioWorthEnd(connection, fund);
		total += Queries.getCash(connection, fund, end);
		double contains = portfolioContainsPercent(connection, fund, end);
		return (total*(1-contains))/start;
	}
	
	public ResultSet mysteryQuery(Connection connection){
		String query = "select c.individual, c.portfolio, c.date_order, c.percent from " +
				"(select individual, portfolio, max(date_order) latest_d from contains where portfolio=? group by individual) a, " +
				"contains c where c.individual=a.individual and c.portfolio=a.portfolio and " +
				"c.date_order=a.latest_d order by c.percent limit 1;";
		
		String insert = "insert into mystery values (?,?) on duplicate key update";
		
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
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String args[]){
		Sql s = new Sql();
		double a = s.portfolioWorthEnd(Utils.connectToSQL("root", "toor"), "fund_10");
		System.out.println(a);
	}
}