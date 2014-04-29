package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;

public class Process {
	Connection connection = null;
	PreparedStatement statement = null;
//	Statement statement = null;
	Hashtable<ArrayList<String>, Integer> trackBuy = new Hashtable<ArrayList<String>, Integer>();
	HashSet<String> funds;
	HashSet<String> individual;
	public Process() throws IOException {
		 
		funds = new HashSet<String>();
		individual = new HashSet<String>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}catch (ClassNotFoundException e){
			 System.out.println("No jdbc found");			
		}
		try {
			//connection = DriverManager.getConnection("jdbc:mysql://Localhost/stocks", "root", "ps");
			connection = DriverManager.getConnection("jdbc:mysql://Localhost/stocks", "root", "toor");
		}catch(Exception e) {
			System.out.println("Error Occured While connecting " + e);
		}
	//	return connection;
	}
	
	
	public void fillActivity() {
		//String csvFile = "/home/xwang125/Class/cmsc424/project/script1.csv";
		String csvFile = "/home/jeff/424/424finance/script1.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int i = 0;
		//ArrayList<String[]> list = new ArrayList<String[]>(); 
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while((line = br.readLine()) != null) {
				String[] found = line.split(cvsSplitBy);
				if(found[0].equals("fund")) {
					funds.add(found[1]);
				}
				else if(found[0].equals("individual")) {
					individual.add(found[1]);
				}
				String st = "insert into activity values(?,?,?,?,?);";
					try {
						statement = connection.prepareStatement(st);
						statement.setString(1, found[0]);
						statement.setString(2, found[1]);
						statement.setString(3, found[2]);
						statement.setString(4, found[3]);
						if(found.length == 5) {
							statement.setString(5, found[4]);
						}
						else {
							statement.setNull(5, java.sql.Types.VARCHAR);
						}
						statement.execute();					
					}catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
			}
		}catch(FileNotFoundException e) {
			System.out.println("can not find the file" + e);
		}catch(IOException e) {
			System.out.println("can not read from file" + e);
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		System.out.println(individual.size());	
}
	
	
	
	public void fillFund() {
		String csvFile = "/home/jeff/424/424finance/script1.csv";
		//String csvFile = "/home/xwang125/Class/cmsc424/project/script1.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int i = 0;
		//ArrayList<String[]> list = new ArrayList<String[]>(); 
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while((line = br.readLine()) != null) {
				String[] found = line.split(cvsSplitBy);
				if(found[0].equals("fund") || found[0].equals("individual")) {
					String st = "insert into fund values(?, ?, ?);";
					try {
						statement = connection.prepareStatement(st);
						statement.setString(1, found[1]);
						statement.setInt(2, Integer.parseInt(found[2]));
						boolean isIndividual = found[0].equals("fund")? false:true;
						statement.setBoolean(3, isIndividual); 
						statement.executeUpdate();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//String name = "(\"Alex\", 20);";
				}
			}
		}catch(FileNotFoundException e) {
			System.out.println("can not find the file" + e);
		}catch(IOException e) {
			System.out.println("can not read from file" + e);
		}
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void fillContains() throws ParseException { // track individual invest funds
		//String csvFile = "/home/xwang125/Class/cmsc424/project/script1.csv";
		String csvFile = "/home/jeff/424/424finance/script1.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int i = 0;
		//ArrayList<String[]> list = new ArrayList<String[]>(); 
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while((line = br.readLine()) != null) {
				
				String[] found = line.split(cvsSplitBy);
				if((individual.contains(found[1]) && funds.contains(found[2])) 
						|| (individual.contains(found[1]) && Constants.COMPANIES.contains(found[2]))) {
					String insert = "insert into contains value(?, ?, ?, ?, ?)";
					try {
						statement = connection.prepareStatement(insert);
						statement.setString(1, found[1]);
						statement.setString(2, found[2]);
						if(found[0].equals("buy")) {
							statement.setInt(3, Integer.parseInt(found[3]));
							statement.setInt(4, 0); // percentage, calculate later
						//	String ss = update(found[4]);
							statement.setString(5, found[4]);
							statement.execute();
							ArrayList<String> l = new ArrayList<String>();
							l.add(found[1]);
							l.add(found[2]);
							trackBuy.put(l, Integer.parseInt(found[3]));
						}
						else if(found[0].equals("sell")) {
							statement.setInt(3, 0);
							statement.setInt(4, 0);
							statement.setString(5, found[3]);
							statement.execute();
							System.out.println("sellsellsellsell");
						}
						else { //buysell for sell
							statement.setInt(3, 0);
							statement.setInt(4, 0);
							statement.setString(5, found[4]);
							statement.execute();
							//for buy
							String s1 = "insert into contains value(?,?,?,?,?);";
							statement = connection.prepareStatement(s1);
							statement.setString(1, found[1]);
							statement.setString(2, found[3]);
						
							ArrayList<String> l = new ArrayList<String>();
							l.add(found[1]);
							l.add(found[2]);
							int transfer = trackBuy.get(l);
							statement.setInt(3, transfer);
							statement.setInt(4, 0);
							statement.setString(5, found[4]);
							statement.execute();
						}            
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}catch(FileNotFoundException e) {
			System.out.println("can not find the file" + e);
		}catch(IOException e) {
			System.out.println("can not read from file" + e);
		} //catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
	}
			
		
			
	public void fillOwn() throws ParseException {
		//String csvFile = "/home/xwang125/Class/cmsc424/project/script1.csv";
		String csvFile = "/home/jeff/424/424finance/script1.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int i = 0;
		//ArrayList<String[]> list = new ArrayList<String[]>(); 
		try {
			br = new BufferedReader(new FileReader(csvFile));
			while((line = br.readLine()) != null) {
		//		System.out.println(line);
				String[] found = line.split(cvsSplitBy);
		if(funds.contains(found[1])) { // only for funds
		//	System.out.println("????????????????????");		
			
				if(found[0].equals("buy")){ //check if company exist
					System.out.println(i++);
					System.out.println(found[2]);
					if(!(Constants.COMPANIES.contains(found[2]))) {
			//	System.out.println("**************************");	
						continue;
					}
			
					try{
						String exeTime = update(found[2], found[4]);
						
						String myString = "insert into owns values(?,?,?,?,?)";
						statement = connection.prepareStatement(myString);
						statement.setString(1, found[1]);
						statement.setString(2, found[2]);
							statement.setInt(3, Integer.parseInt(found[3])); //amount
							statement.setString(4, found[4]); //date
			//				System.out.println(")))))))))))))))" + found[4]);
			//				statement.setString(5, "adfadfadfaf");
							
			//				System.out.println("************" + exeTime);
							statement.setString(5, exeTime);
							statement.executeUpdate();
							ArrayList<String> list = new ArrayList <String>();
							list.add(found[1]);
							list.add(found[2]);
							if(!trackBuy.contains(list)) {
								trackBuy.put(list, Integer.parseInt(found[3]));
							}
							else {
								trackBuy.put(list, trackBuy.get(list) + Integer.parseInt(found[3]));
							}
					}catch(SQLException e) {
						System.out.println("error occur " + e);
					}
				}
						else if (found[0].equals("sell")){ // for sell
							if(!(Constants.COMPANIES.contains(found[2]))) {
								//	System.out.println("**************************");	
											continue;
							}
				//			System.out.println("hammpen" + found[3]);
							String exeTime = update(found[2], found[3]);
				//			System.out.println("what awat" + found[3]);
							String myString = "insert into owns values(?,?,?,?,?)";
							statement = connection.prepareStatement(myString);
							statement.setString(1, found[1]);
							statement.setString(2, found[2]);
							statement.setInt(3, 0);
							statement.setString(4, found[3]);
							statement.setString(5, exeTime);
							statement.executeUpdate();
						}
					
			
				
				else if(found[0].equals("sellbuy")) { //for buysell case
					if(!(Constants.COMPANIES.contains(found[2])) || !(Constants.COMPANIES.contains(found[3]))) {
						continue;
					}
					String exeTime = update(found[2], found[4]);
				//	String exeTime2 = update(found[3], found[4]);
					double price = sellAmount(found[1], found[2], exeTime);
					String myString = "insert into owns values(?,?,?,?,?)";
					statement = connection.prepareStatement(myString);
					statement.setString(1, found[1]);
					statement.setString(2, found[2]);
					statement.setInt(3, 0); //amount
					statement.setString(4, found[4]);
					
					statement.setString(5, exeTime);
					statement.executeUpdate();
					//for invest to another company
					String st4 = "insert into owns values(?, ? ,? ,?, ?);";
					statement = connection.prepareStatement(st4);
					statement.setString(1, found[1]);
					statement.setString(2, found[3]);
				
					statement.setDouble(3, price);
					statement.setString(4, found[4]); //date
					statement.setString(5, exeTime);
					statement.executeUpdate();
					}
		}	
			}
 }catch(FileNotFoundException e) {
		System.out.println("can not find the file" + e);
	}catch(IOException e) {
		System.out.println("can not read from file" + e);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
		
	//get the real date to buy or sell, finding match date from quotes
	private String update(String company, String time) throws ParseException, SQLException {
		String st = "select * from quotes where ticker = " +  "'" + company + "'" + " and " +  "time >= " +  "'" + time + "'" + "order by (time) limit 1" + ";";
//		System.out.println("????????????" + st);
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
//		Date date = formatter.parse(time);
//		Calendar c = Calendar.getInstance();
		String newTime = time;
		statement = connection.prepareStatement(st);
		ResultSet result = statement.executeQuery();
		if(result.next()) {
			return result.getString("time"); 
		}
		else {
			return "2013-12-31";
		}
	}
	
	private double sellAmount(String funds, String company, String time) throws SQLException {
		double finalPrice = 0;
		String st1 = "select * from owns where ticker = '" + company  + "' and fund = '" + funds + "' ;"; 
		System.out.println(st1);
		statement = connection.prepareStatement(st1);
		ResultSet set1 = statement.executeQuery();
		if(!set1.next()) {
			return 0;
		}
		String time1 = set1.getString("date_execute");
		
		ArrayList<String> ls = new ArrayList<String>();
		ls.add(funds);
		ls.add(company);
		int amount = trackBuy.get(ls);
		finalPrice = amount * Queries.stockAppreciation(connection, company, time1, time);
		return finalPrice;
	}
	
	
	public static void main(String[] args) throws  SQLException, IOException, ParseException{
//		Process testBlob = new Process();
//		String name = "Peter";
//		testBlob.fillActivity();
//		testBlob.fillFund();
//		testBlob.fillContains();
//		testBlob.fillOwn();
		
//		Connection c = Utils.connectToSQL("root", "toor");
//		System.out.println(Utils.getFundValue(c, "fund_1", "2013-01-01"));
	}
	
//public void insert() {
//		
//		//FileInputStream inputStream = null;
//try {
////			String select = "select * from people;";
////			connection = getConnection();
////			statement = connection.createStatement();
////			ResultSet result = statement.executeQuery(select);
////			System.out.println(result);
//			//String name = "Kate";
//			String insert = "insert into people values (?, ?)";
//		//	System.out.println(insert);
//			statement = connection.prepareStatement(insert);
//			//String name = "(\"Alex\", 20);";
//			statement.setString(1, "Xu");
//		    statement.setInt(2, 20);
//		    System.out.println(statement);
//			statement.executeUpdate();
//		}catch (SQLException e) {
//			System.out.println("SQLException: " + e);
//		}finally {
//			try {
//				connection.close();
//				statement.close();
//			}catch (SQLException e) {
//				System.out.println("SQLException: " + e);
//			}
//		}
//		
//	}
	
//	public ResultSet select(String name) throws SQLException {
//		String select = "select age from people where name = \"" + name + "\"";
//	//	System.out.println(select);
//	//	connection = getConnection();
//		statement = connection.prepareStatement(select);
//		ResultSet result = statement.executeQuery(select);
//		return result;
//	
//	}
}

