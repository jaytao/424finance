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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Process {
	Connection connection = null;
	PreparedStatement statement = null;



	public Process(String file ) throws IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://Localhost/stocks", "root", "toor");
		} catch (Exception e) {
			System.out.println("Error Occured While connecting " + e);
		}
		System.out.println("Fill activity");
		fillActivity(file);

	}

	// parses csv file and puts the info into the activity table
	public void fillActivity(String file) {

		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			int i = 0;
			br = new BufferedReader(new FileReader(file));
			System.out.println(file);
			while ((line = br.readLine()) != null) {
				String[] found = line.split(cvsSplitBy);
				System.out.println(i++ + ": " + found[0]);
				if (found[0].equals("fund")) {
					Queries.insertActivity(connection, found[0], found[1], found[2], "", found[3]);

					fund(found[1], Double.parseDouble(found[2]), found[3], false);
				}
				else if (found[0].equals("individual")) {
					Queries.insertActivity(connection, found[0], found[1], found[2], "", found[3]);

					fund(found[1], Double.parseDouble(found[2]), found[3], true);
				}

				else if (found[0].equals("sell")) {
					Queries.insertActivity(connection, found[0], found[1], found[2], "", found[3]);
					sell(found[1], found[2], found[3]);
				} else if (found[0].equals("buy")) {
					Queries.insertActivity(connection, found[0], found[1], found[2], found[3], found[4]);
					buy(found[1], found[2], Double.parseDouble(found[3]),found[4]);
				} else if (found[0].equals("sellbuy")) {
					sellbuy(found[1], found[2], found[3], found[4]);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fund(String name, double amount, String date, boolean isInd) {
		double fundValue = Utils.fundCurrentValue(connection, name, date);
		if (fundValue <= 0) {
			Queries.insertCash(connection, name, 1.0, date);
			Queries.insertValue(connection, name, amount, date);
			Queries.insertFund(connection, name, isInd);
		} else {
			Queries.insertValue(connection, name, fundValue + amount, date);
		}
	}

	private void sell(String fund, String obj, String date) {

		// ResultSet rs = Queries.getFund(connection, fund);
		// rs.next();

		// if fund selling stock
		if (Constants.COMPANIES.contains(obj)) {
			try {
				double fundValue = Queries.getFundTotalValue(connection, fund, date);
				double newFundValue = 0;
				String executeDate = Utils.findExecuteDate(connection, obj, date);
				HashMap<String, Double> stockAmount = new HashMap<String, Double>();
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

					double amount = percent * Queries.stockAppreciation(connection, ticker, dateBought, executeDate)
							* fundValue;
					stockAmount.put(ticker, amount);
					newFundValue += amount;
				}
				rs.close();
				// calculate new ratios and insert back into owns
				double cash = Queries.getCash(connection, fund, date);
				newFundValue += cash;
				Iterator<String> iter = stockAmount.keySet().iterator();
				
				while (iter.hasNext()) {
					String ticker = iter.next();
					double amount = stockAmount.get(ticker);
					if (ticker.equals(obj)) {
						cash += amount;
						Queries.insertOwns(connection, fund, ticker, 0.0, date, executeDate);
					} else if (amount == 0) {
						continue;
					} else {
						Queries.insertOwns(connection, fund, ticker, amount / newFundValue, date, executeDate);
					}
				}

				// new cash amount
				Queries.insertCash(connection, fund, cash / newFundValue, date);
				// new value amount
				Queries.insertValue(connection, fund, newFundValue, date);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		// if ind selling fund
		else {

			// fundValue = appreciated value of the portfolio
			double fundValue = Utils.fundCurrentValue(connection, obj, date);
			if (fundValue < 0) {
				return;
			}
			double percent = Queries.getIndividualFundPercent(connection, fund, obj);

			// update individual's cash amount and value
			// amount to be added to cash
			double originalBuyAmount = Queries.getIndBuyFundAmount(connection, fund, obj);
			double plusAmount = percent * fundValue;
			double nowAmount = Queries.getFundTotalValue(connection, fund, date) + plusAmount - originalBuyAmount;

			double nowCash = Queries.getCash(connection, fund, date) + plusAmount;
			Queries.insertCash(connection, fund, nowCash / nowAmount, date);
			Queries.insertValue(connection, fund, nowAmount, date);
			// decrement fund's total value
			fundValue = Queries.getFundTotalValue(connection, obj, date);
			fundValue *= (1.0 - percent);
			Queries.insertValue(connection, obj, fundValue, date);

			Utils.updateContainsSell(connection, fund, obj, date);
		}

	}

	private void buy(String fund, String obj, double amount, String date) {

		String exeDate = Utils.findExecuteDate(connection, obj, date);

		// stock being bought
		if (Constants.COMPANIES.contains(obj)) {
			double fundValue = Queries.getFundTotalValue(connection, fund, date);
			if (fundValue < 0) {
				System.out.println("Fund value" + fundValue);
			}
			double percent = amount / fundValue;

			double cash = Queries.getCash(connection, fund, exeDate);
			Queries.insertOwns(connection, fund, obj, percent, date, exeDate);
			cash -= amount;
			Queries.insertCash(connection, fund, cash / fundValue, exeDate);
		}
		// individual buying fund
		else {
			// value for fund individual is buying
			double value = Queries.getFundTotalValue(connection, obj, date);
			if (value < 0) {
				return;
			}

			// increment the fund value
			value += amount;
			double percent = amount / value;
			Queries.insertContains(connection, fund, obj, percent, date);
			Queries.insertValue(connection, obj, value, date);

			// subtract the amount bought from the cash table
			double cash = Queries.getCash(connection, fund, exeDate);
			double fundValue = Queries.getFundTotalValue(connection, fund, date);
			cash -= amount;
			Queries.insertCash(connection, fund, cash / fundValue, date);
		}

	}

	private void sellbuy(String fund, String obj1, String obj2, String date) {
		double obj1Value;
		// figure out amount that obj1 is worth
		if (Constants.COMPANIES.contains(obj1)) {
			obj1Value = Queries.getStockOwnsPercent(connection, fund, obj1);
			obj1Value *= Utils.fundCurrentValue(connection, fund, date);
		} else {
			double fundValue = Utils.fundCurrentValue(connection, obj1, date);
			if (fundValue < 0) {
				return;
			}
			double percent = Queries.getIndividualFundPercent(connection, fund, obj1);
			obj1Value = percent * fundValue;
		}

		// execute a sell
		sell(fund, obj1, date);

		// execute the buy for the amount previously calculated
		buy(fund, obj2, obj1Value, date);
	}

	public static void main(String[] args) throws SQLException, IOException, ParseException {

<<<<<<< HEAD
		Process testBlob = new Process("/home/xwang125/Class/cmsc424/project/script4.csv");
		// Connection c = Utils.connectToSQL("root", "toor");
=======
		Process testBlob = new Process("/home/jeff/424/424finance/script4.csv");
//		 Connection c = Utils.connectToSQL("root", "toor");
//		 Utils.stocksIncreasing(c);
>>>>>>> 4b578baea400e004cb08cd8b99421b14a5ceef72
	}

}

