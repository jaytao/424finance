package gui;

/*
 * provide informations for stocks
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.sql.Connection;
import java.util.ArrayList;

import db.Process;
import db.Utils;


public class Company extends JPanel{


	private JTable table;
	private JTextField textField, quoteInformation, stockName, start, end;
	private JTextField stock1, stock2;
	Process command;
	private int width, height;
	private Utils utility = new Utils();
	private Connection connection;

	private JTextField stock, date;

	//constructor
	public Company(Connection c, int width, int height) throws IOException {

		this.width = width;
		this.height = height;
		setLayout(new BorderLayout());
		this.connection = c; 

		JPanel out1 = new JPanel(new GridLayout(0, 1));

		JPanel p1 = new JPanel();
		JLabel l1= new JLabel("Stocks");
		l1.setFont(new Font("Serif", Font.BOLD, 20));
		p1.add(l1);
		out1.add(p1);

		//information for stock quotes
		JPanel Iquote = new JPanel();
		JButton quote = new JButton("Get Quote");
		Iquote.add(quote);
		Iquote.add(new JLabel("stock: "));
		stock = new JTextField(10);
		Iquote.add(stock);
		Iquote.add(new JLabel("date(yyyy-mm-dd): "));
		date = new JTextField(10);
		Iquote.add(date);
		out1.add(Iquote); 

		//register quote button, to get share price
		quote.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				String stockName = stock.getText();
				String dateName = date.getText();
				Double rt = sql.stockQuote(connection, stockName, dateName);
				if(rt < 0) {
					popError();
				}
				else {
					showNewFrame("Quote of stock", rt.toString());
				}
			}
		} );

		//information of stocks, including rank stock rate of return, net worth,
		// single stock rate of return and worth
		JPanel display = new JPanel();
		JButton readData = new JButton("RateOfReturn");
		display.add(readData);
		display.add(new JLabel("stock: "));
		stockName = new JTextField(10);
		display.add(stockName);
		display.add(new JLabel("Start: "));
		start = new JTextField(10);
		display.add(start);

		JPanel d1 = new JPanel();
		d1.add(new JLabel("end: "));
		end = new JTextField(10);
		d1.add(end);
		display.add(d1);

		//register "rateofreturn" button, to show the rate of return for input stock 
		readData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFrame f = showNewFrame2("rate of return");
				Sql sql = new Sql();
				String stock = stockName.getText();
				String startD = start.getText();
				String endD = end.getText();
				ResultSet set = sql.stockRateOfReturn(connection, stock, startD, endD);
				Double result;
				try {
					result = set.getDouble(1);
					JPanel showResult = new JPanel();
					JTextArea area = new JTextArea();
					area.setText(result.toString());
					JScrollPane scroll = new JScrollPane(area);
					showResult.add(scroll);
					f.add(showResult);
					f.setVisible(true);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		//third panel, compare stocks, and rank top 25 stocks in 
		//annualized rate of return
		JPanel out2 = new JPanel(new GridLayout(0,1));
		JPanel compare = new JPanel();
		JLabel compareB = new JLabel("Compare:");
		compare.add(compareB);		
		compare.add(new JLabel("Stock 1"));
		stock1 = new JTextField(10);
		compare.add(stock1);
		stock2 = new JTextField(10);
		compare.add(new JLabel("Stock 2"));
		compare.add(stock2);
		JButton output = new JButton("To File");
		compare.add(output);
		JPanel p3 = new JPanel();
		JButton top25 = new JButton("Top 25 Stocks in Rate of Return");
		JButton top25output = new JButton("Save Top25 to File");
		p3.add(top25);
		p3.add(top25output);
		out2.add(compare);
		out2.add(p3);

		JPanel outer = new JPanel(); // for outmost
		outer.setLayout(new GridLayout(0, 1, 10, 10));

		outer.add(out1);
		outer.add(display);
		outer.add(out2);
		add(outer, BorderLayout.NORTH);

		//register top25 button, for annualized rate of return 
		top25.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("hello");
				Sql sql = new Sql();
				ResultSet rt = sql.stockTop25Return(connection);
				JTable table= createTable(rt, 6);
				JFrame f = showNewFrame2("Annualized Rate of Return");
				JScrollPane scrollPane = new JScrollPane(table);
				JPanel p = new JPanel();
				p.add(scrollPane);
				f.add(p);
			}

		});

		//export data of top annualized rate of return to csv file
		top25output.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				ResultSet rt = sql.stockTop25Return(connection);
				Output.getCsvFile("top25output.csv", rt);
			}
		});

		//export data of comparison to csv file
		output.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				String stockN1 = stock1.getText();
				String stockN2 = stock2.getText();
				//get stock rate of return data
				ResultSet set1 = sql.stockRateOfReturn(connection, stockN1, "2005-01-03", "2013-12-31");			
				ResultSet set2 = sql.stockRateOfReturn(connection, stockN2, "2005-01-03", "2013-12-31");
				//export data to csv file
				try {
					ArrayList<Double> list = new ArrayList<Double>();
					Double v11 = set1.getDouble(1);
					Double v12 = set2.getDouble(1);
					Double v21 = sql.hightestQuote(connection, stockN1);
					Double v22 = sql.hightestQuote(connection, stockN2);
					Double v31 = sql.lowestQuote(connection, stockN1);
					Double v32 = sql.lowestQuote(connection, stockN2);
					list.add(v11);
					list.add(v12);
					list.add(v21);
					list.add(v22);
					list.add(v31);
					list.add(v32);
					try {
						PrintWriter writer = new PrintWriter("compare.csv", "UTF-8");
						
						writer.append(stockN1);
						writer.append(',');
						writer.append(stockN2 + " (RoR;High;Low)");	
						writer.append('\n');
						int i = 0;
						while(i < list.size()) {
							writer.append(list.get(i).toString());
							if(i % 2 == 0) {
								writer.append(',');
							}
							else {
								writer.append("\n");
							}
							i++;
						}

						writer.flush();
						writer.close();
					}catch(IOException e) {
						e.printStackTrace();
					} 
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}


	//create table for data of top 25 annulized total rate of return
	private JTable createTable(ResultSet rs, int numberOfColumns) {
		if(rs == null) {
			System.out.println("nullnull");
			return null;
		}

		JPanel porto = new JPanel();
		setLayout(new FlowLayout());
		porto.add(new JLabel("Information of stock"));
		String[] columnNames = {"stock", "startdate", "price", "enddate", "price", "annulized total rate of return"};
		JTable table = null;
		int rowcount = 0;

		try {
			if (rs.last()) {
				rowcount = rs.getRow();
				rs.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
			}


			JScrollPane scrollPane = new JScrollPane(table);
			porto.add(scrollPane);
			Object[][] data = new Object[rowcount][numberOfColumns];
			int j = 0;
			while (rs.next()) {
				Object[] rowData = new Object[numberOfColumns];
				System.out.println(rowData.length);
				for(int i  = 0; i < rowData.length; i++) {
					rowData[i] = rs.getObject(i+1);
				}
				data[j] = rowData;
				j++;
			}
			table = new JTable(data, columnNames);
			table.setFillsViewportHeight(true);
			return table;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return table;
	}


	// to create a new table to display data
	private JFrame showNewFrame(String title, Object output) {
		JFrame f = new JFrame();
		f.setTitle(title);
		f.setSize(200, 200);
		f.setLocationRelativeTo(null);
		JPanel showResult = new JPanel();
		JTextArea area = new JTextArea();
		area.setText((String)output);
		JScrollPane scroll = new JScrollPane(area);
		showResult.add(scroll);
		f.add(showResult);
		f.setVisible(true);
		return f;
	}
	//create another new table with different parameter
	private JFrame showNewFrame2(String title) {
		JFrame f = new JFrame();
		f.setTitle(title);
		f.setSize(700, 500);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		return f;
	}

	//pop the window with error message when invalid data is input
	private JFrame popError() {
		JFrame f = new JFrame();
		f.setTitle("Error!");
		f.setSize(200, 200);
		f.setLocationRelativeTo(null);
		JPanel showResult = new JPanel();
		JTextArea area = new JTextArea("No such Record!");
		showResult.add(area);
		f.add(showResult);
		f.setVisible(true);
		return f;
	}


}


