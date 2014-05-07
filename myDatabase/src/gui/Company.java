package gui;

//import gui.Funds.MyButtonListener;

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

	public Company(int width, int height) throws IOException {

		this.width = width;
		this.height = height;
		setLayout(new BorderLayout());
		connection = Utils.connectToSQL("root", "dingding1016"); 

		JPanel out1 = new JPanel(new GridLayout(0, 1));

		JPanel p1 = new JPanel();
		JLabel l1= new JLabel("Stocks");
		l1.setFont(new Font("Serif", Font.BOLD, 20));
		p1.add(l1);
		out1.add(p1);

		JPanel Iquote = new JPanel();
		JButton quote = new JButton("quote");
		Iquote.add(quote);
		Iquote.add(new JLabel("stock: "));
		stock = new JTextField(10);
		Iquote.add(stock);
		Iquote.add(new JLabel("date(yyyy-mm-dd): "));
		date = new JTextField(10);
		Iquote.add(date);
		out1.add(Iquote); 


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

		//for next Panel
		JPanel display = new JPanel();
		//	display.setSize(new Dimension(500,20));
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
		//third panel
		JPanel out2 = new JPanel(new GridLayout(0,1));
		JPanel compare = new JPanel();
		JButton compareB = new JButton("compare");
		compare.add(compareB);		
		compare.add(new JLabel("stock1"));
		stock1 = new JTextField(10);
		compare.add(stock1);
		stock2 = new JTextField(10);
		compare.add(new JLabel("stock2"));
		compare.add(stock2);
		JButton output = new JButton("output file");
		compare.add(output);
		JPanel p3 = new JPanel();
		JButton top25 = new JButton("top 25 stocks in Annualized ROR");

		p3.add(top25);

		out2.add(compare);
		out2.add(p3);

		JPanel outer = new JPanel(); // for outmost
		outer.setLayout(new GridLayout(0, 1, 10, 10));

		outer.add(out1);
		outer.add(display);
		outer.add(out2);
		add(outer, BorderLayout.NORTH);

		//		compareB.addActionListener(new ActionListener(){
		//			public void actionPerformed(ActionEvent arg0) {
		//				Sql sql = new Sql();
		//				String stockN1 = stock1.getText();
		//				String stockN2 = stock2.getText();
		//				Double v1 = sql.stockRateOfReturn(connection, stockN1, "2005-01-03", "2013-12-31");
		//				Double v2 = sql.stockRateOfReturn(connection, stockN2, "2005-01-03", "2013-12-31");
		//				Double v11 = Math.pow(v1,1/9)-1;
		//				Double v22 = Math.pow(v2, 1/9) - 1;
		//				
		//				JFrame f = showNewFrame2("comparison");
		//
		//				//create table
		//				JPanel p = new JPanel();
		//				setLayout(new FlowLayout());
		//				String[] columnNames = {stockN1, stockN2};
		//				DefaultTableModel model = null;
		//				Object[][] data = new Object[10][2];
		//				Object[] rowData = new Object[2];
		//				rowData[0] = v1;
		//				System.out.println(rowData[0]);
		//				rowData[1] = v2;
		//				data[0] = rowData;
		//				System.out.println(data[0][0]);
		//				rowData[0] = v11;
		//				rowData[1] = v22;
		//				data[1] = rowData;
		//				System.out.println(data[0][0]);
		//				table = new JTable(data, columnNames);
		//				table.setFillsViewportHeight(true);
		//				JScrollPane scrollPane = new JScrollPane(table);
		//				p.add(scrollPane);
		//				f.add(p);
		//			} 
		//	}); 



		top25.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				ResultSet rt = sql.stockTop25Return(connection);
				JTable table= createTable(rt, 6);
				JFrame f = showNewFrame2("annualized rate of return");
				JScrollPane scrollPane = new JScrollPane(table);
				JPanel p = new JPanel();
				p.add(scrollPane);
				f.add(p);
			}

		});

		output.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				ResultSet set = sql.stockTop25Return(connection);

				String stockN1 = stock1.getText();
				String stockN2 = stock2.getText();
				ResultSet set1 = sql.stockRateOfReturn(connection, stockN1, "2005-01-03", "2013-12-31");			
				ResultSet set2 = sql.stockRateOfReturn(connection, stockN2, "2005-01-03", "2013-12-31");

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
						FileWriter writer = new FileWriter("/home/xwang125/Desktop/compare.csv");
						writer.append(stockN1);
						writer.append(',');
						writer.append(stockN2);	
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


	private JTable createTable(ResultSet rs, int numberOfColumns) {
		if(rs == null) {
			return null;
		}

		JPanel porto = new JPanel();
		setLayout(new FlowLayout());
		porto.add(new JLabel("Information of stock"));
		String[] columnNames = {"stock", "2", "3", "4", "5", "annulized total rate of return"};
		DefaultTableModel model = null;
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

	private JFrame showNewFrame2(String title) {
		JFrame f = new JFrame();
		f.setTitle(title);
		f.setSize(700, 500);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		return f;
	}

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


