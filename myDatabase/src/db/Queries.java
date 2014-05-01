package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Queries {
	private static final String VALUE_INSERT = "insert into value values(?,?,?);";
	private static final String OWNS_INSERT = "insert into owns values(?,?,?,?,?);";
	private static final String CONTAINS_INSERT = "insert into contains values(?,?,?,?);";
	private static final String CASH_INSERT = "insert into cash values(?,?,?);";
	private static final String ACTIVITY_INSERT = "insert into activity values(?,?,?,?,?);";
	private static final String FUND_INSERT = "insert into fund values(?,?)";

	private static final String QUOTES_APPRECIATION = "(select * " + "from quotes where ticker=? and time >=? order "
			+ "by time asc limit 1) union (select * from quotes where "
			+ "ticker=? and time>=? order by time asc limit 1)";


	public static final String ACTIVITY_GET = "select * from activity order by time asc;";

	public static void insertCash(Connection connection, String name, double cash, String date) {
		PreparedStatement st;
		try {
			st = connection.prepareStatement(CASH_INSERT);
			st.setString(1, name);
			st.setDouble(2, cash);
			st.setString(3, date);

			st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void insertOwns(Connection connection, String fund, String ticker, double percent, String date_order,
			String date_execute) {
		PreparedStatement st;
		try {
			st = connection.prepareStatement(OWNS_INSERT);
			st.setString(1, fund);
			st.setString(2, ticker);
			st.setDouble(3, percent);
			st.setString(4, date_order);
			st.setString(5, date_execute);

			st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void insertValue(Connection connection, String fund, double value, String date) {
		PreparedStatement st;
		try {
			st = connection.prepareStatement(VALUE_INSERT);
			st.setString(1, fund);
			st.setDouble(2, value);
			st.setString(3, date);

			st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void insertContains(Connection connection, String individual, String fund, double percent, String date) {
		PreparedStatement st;
		try {
			st = connection.prepareStatement(CONTAINS_INSERT);
			st.setString(1, individual);
			st.setString(2, fund);
			st.setDouble(3, percent);
			st.setString(4, date);

			st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void insertActivity(Connection connection, String s1, String s2, String s3, String s4, String date) {
		PreparedStatement st;
		try {
			st = connection.prepareStatement(ACTIVITY_INSERT);
			st.setString(1, s1);
			st.setString(2, s2);
			st.setString(3, s3);
			st.setString(4, s4);
			st.setString(5, date);
			st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void insertFund(Connection connection, String name, boolean isInd) {
		PreparedStatement st;
		try {
			st = connection.prepareStatement(FUND_INSERT);
			st.setString(1, name);
			st.setBoolean(2, isInd);
			st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// return ratio of stock appreciation between two dates
	// date1 -start, date2 - end
	public static double stockAppreciation(Connection connection, String ticker, String date1, String date2) {

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

			return price2 / price1;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return 0;
	}

	private static final String VALUE_FUND = "select * from value where fund=? and time<=? order by time desc;";

	public static double getFundTotalValue(Connection connection, String fund, String date){
		PreparedStatement st;
		try {
			st = connection.prepareStatement(VALUE_FUND);
			st.setString(1, fund);
			st.setString(2, date);

			ResultSet result = st.executeQuery();
			if (!result.next()){
				return -1;
			}
			return result.getDouble("value");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

	public static ResultSet getActivity(Connection connection) {
		try {
			PreparedStatement st = connection.prepareStatement(ACTIVITY_GET);
			return st.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String FUND_GET = "select * from fund where name=?;";
	public static ResultSet getFund(Connection connection, String name){
		try{
			PreparedStatement st = connection.prepareStatement(FUND_GET);
			st.setString(1, name);
			return st.executeQuery();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String CASH_GET = "select * from cash where name=? and time<=? order by time desc;";
	public static double getCash(Connection connection, String name, String date){
		try{
			PreparedStatement st = connection.prepareStatement(CASH_GET);
			st.setString(1, name);
			st.setString(2, date);
			ResultSet rs = st.executeQuery();
			rs.next();
			double percent = rs.getDouble(2);
			double value = Queries.getFundTotalValue(connection, name, date);
			
			return percent * value;
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}