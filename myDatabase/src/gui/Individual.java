package gui;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;


import db.Utils;

/*
 * provide informations for individual
 */
public class Individual extends JPanel{
	JTextField port, startDate, endDate;
	private Connection connection;

	//constructor
	public Individual(Connection c) {
		connection = c; 
		//first panel, 
		JPanel out1 = new JPanel(new GridLayout(0,1));

		JPanel p1 = new JPanel();
		JLabel l1= new JLabel("Individual");
		l1.setFont(new Font("Serif", Font.BOLD, 20));
		p1.add(l1);
		out1.add(p1);

		//input individual's data
		JPanel fund = new JPanel();
		port = new JTextField(7);
		startDate = new JTextField(7);
		endDate = new JTextField(7);

		fund.add(new JLabel("Individual:"));
		fund.add(port);
		fund.add(new JLabel("startDate:"));
		fund.add(startDate);
		fund.add(new JLabel("endDate:"));
		fund.add(endDate);

		//for individual return value, net worth
		JPanel p2 = new JPanel(new GridLayout(0,4));
		JButton retB = new JButton("returnValue");
		JButton worthB = new JButton("worth");
		worthB.setSize(10, 10);
		//rank the total return and final net worth for all individuals
		JButton b1 = new JButton("total return");
		JButton b2= new JButton("fianl net worth");
		p2.add(b1);
		p2.add(b2);
		p2.add(retB);
		p2.add(worthB);
		JButton totalreturno = new JButton("totalreturn output");
		JButton networtho = new JButton("networth output");
		p2.add(totalreturno);
		p2.add(networtho);
		JPanel out = new JPanel(new GridLayout(0,1));
		out.add(p1);
		out.add(fund);
		out.add(p2);
		JPanel outfin = new JPanel(new GridLayout(0,1));
		outfin.add(out);
		add(outfin);

		//register retB button, for rate of return data
		retB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {

				Sql sql = new Sql();
				String portName = port.getText();
				String startD = startDate.getText();
				String endD = endDate.getText();
				Double rt = sql.portfolioRateOfReturn(connection, portName, startD, endD);
				if(rt < -99) {
					popError();
				}
				else {
					JFrame f = showNewFrame("rate of return");
					JPanel p = new JPanel();
					JTextArea area = new JTextArea();
					area.setText(rt.toString());
					p.add(area);
					f.add(p);
				}
			}

		});

		//register worthB button, for net worth data
		worthB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				String portName = port.getText();
				String startD = startDate.getText();
				String endD = endDate.getText();
				Double value = Utils.fundCurrentValue(connection, portName, endD); 
				JFrame f = showNewFrame("net worth");
				JPanel p = new JPanel();
				JTextArea area = new JTextArea();
				area.setText(value.toString());
				p.add(area);
				f.add(p);
			}
		});

		//register rateOfReturn button, to rank rate of return
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame f = showNewFrame("rate of return");
				Sql sql = new Sql();
				ResultSet set = sql.rankPortROR(connection, true);
				JTable t = createTable(set, 2);
				JScrollPane scrollPane = new JScrollPane(t);
				JPanel p = new JPanel();
				p.add(scrollPane);
				f.add(p);
			}

		});

		//export data of total return to csv file 
		totalreturno.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				ResultSet set = sql.rankPortROR(connection, true);
				Output.getCsvFile("/home/xwang125/Desktop/totalReturnOutputIndi.csv", set);
			}
		});

		//register totalNetWorth button, for ranking total net worth
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFrame f = showNewFrame("total net worth");
				Sql sql = new Sql();
				ResultSet set = sql.portofolioTotalNetWorth(connection, 1, "2005-01-04", "2013-12-31");
				JTable t = createTable(set, 2);
				JScrollPane scrollPane = new JScrollPane(t);
				JPanel p = new JPanel();
				p.add(scrollPane);
				f.add(p);
			}
		});

		//export data of ranking net worth to csv file
		networtho.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				ResultSet set = sql.portofolioTotalNetWorth(connection, 1, "2005-01-04", "2013-12-31");
				Output.getCsvFile("/home/xwang125/Desktop/networthreturnIndi.csv", set);
			}
		});

	}
	
	//create table for ranking net worth and total return value
	private JTable createTable(ResultSet rs, int numberOfColumns) {
		if(rs == null) {
			return null;
		}

		JPanel porto = new JPanel();
		setLayout(new FlowLayout());
		porto.add(new JLabel("Information of stock"));
		String[] columnNames = {"individual", "value"};
		DefaultTableModel model = null;
		JTable table = null;
		int rowcount = 0;

		try {
			//check rows number
			if (rs.last()) {
				rowcount = rs.getRow();
				rs.beforeFirst(); 
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

	//create new frame for table
	private JFrame showNewFrame(String title) {
		JFrame f = new JFrame();
		f.setTitle(title);
		f.setSize(400, 500);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		return f;
	}
	
	//pop window with error message when input invalid data 
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