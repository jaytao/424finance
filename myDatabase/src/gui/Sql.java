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
			while(result.next()) {
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
		String input ="create view temp as (select fund,  max(time) as mt from value group by fund order by value);"; 
		String select = "select temp.fund, value.value from temp, value where temp.fund = value.fund and temp.mt = value.time\n " 
				+ "order by value.value; ";
		try{
			statement = connection.prepareStatement(drop);
			int i = statement.executeUpdate();
			statement = connection.prepareStatement(input);
			int j = statement.executeUpdate();

			statement = connection.prepareStatement(select);
			ResultSet result = statement.executeQuery();
			return result;
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}