package db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class Process {
	Connection connection = null;
	PreparedStatement statement = null;

	// String csvFile = "/home/xwang125/Class/cmsc424/project/script1.csv";
	String csvFile = "/home/jeff/424/424finance/script1.csv";

	public Process() throws IOException {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://Localhost/stocks", "root", "toor");
		} catch (Exception e) {
			System.out.println("Error Occured While connecting " + e);
		}
		fillActivity();
		executeActions();
	}

	public void executeActions() {
		ResultSet result = Queries.getActivity(connection);
		try {
			while (result.next()) {
				String action = result.getString(1);
				String date = result.getString(5);
				if (action.equals("sell")) {
					String fund = result.getString(2);
					String ticker = result.getString(3);
					sell(fund, ticker, date);
				} else if (action.equals("buy")) {
					String fund = result.getString(2);
					String bought = result.getString(3);
					double amount = result.getDouble(4);
					buy(fund, bought, amount, date);
				} else if (action.equals("sellbuy")) {
					String fund = result.getString(2);
					String ticker1 = result.getString(3);
					String ticker2 = result.getString(4);
					sellbuy(fund, ticker1, ticker2, date);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void fillActivity() {
		
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] found = line.split(cvsSplitBy);
				if (found[0].equals("fund")) {
					Queries.insertActivity(connection, found[0], found[1], found[2], "", found[3]);

					Queries.insertCash(connection, found[1], 1.0, found[3]);
					Queries.insertValue(connection, found[1], Double.parseDouble(found[2]), found[3]);
					Queries.insertFund(connection, found[1], false);
				}

				else if (found[0].equals("individual")) {
					Queries.insertActivity(connection, found[0], found[1], found[2], "", found[3]);

					Queries.insertCash(connection, found[1], 1.0, found[3]);
					Queries.insertValue(connection, found[1], Double.parseDouble(found[2]), found[3]);
					Queries.insertFund(connection, found[1], true);
				}

				else if (found[0].equals("sell")) {
					Queries.insertActivity(connection, found[0], found[1], found[2], "", found[3]);
				} else {
					Queries.insertActivity(connection, found[0], found[1], found[2], found[3], found[4]);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sell(String fund, String obj, String date) {

	}

	private void buy(String fund, String obj, double amount, String date) {
		
		try {
			ResultSet rs = Queries.getFund(connection, fund);
			rs.next();
			String exeDate = Utils.findExecuteDate(connection, obj, date);

			// stock being bought
			if (Constants.COMPANIES.contains(obj)) {
				double fundValue = Queries.getFundTotalValue(connection, fund, date);
				double percent = amount / fundValue;
				
				double cash = Queries.getCash(connection, fund, exeDate);
				Queries.insertOwns(connection, fund, obj, percent, date, exeDate);
				cash -= amount;
				Queries.insertCash(connection, fund, cash/fundValue, exeDate);

			}
			// individual buying fund
			else {
				//value for fund individual is buying
				double value = Queries.getFundTotalValue(connection, obj, date);
				if (value < 0){
					return;
				}
				value += amount;
				double percent = amount / value;
				Queries.insertContains(connection, fund, obj, percent, date);
				Queries.insertValue(connection, obj, value, date);
				
				double cash = Queries.getCash(connection, fund, exeDate);
				double fundValue = Queries.getFundTotalValue(connection, fund, date);
				cash -= amount;
				Queries.insertCash(connection, fund, cash/fundValue, exeDate);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void sellbuy(String fund, String obj1, String obj2, String date) {

	}

	public static void main(String[] args) throws SQLException, IOException, ParseException {
		Process testBlob = new Process();
	
		Connection c = Utils.connectToSQL("root", "toor");
		//System.out.println(Queries.getCash(c, "fund_1", "2013-01-02"));
		
	}

}
