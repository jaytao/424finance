package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import db.Process;
import db.Queries;
import db.Utils;

/*
 * provide informations for portofolios
 */
public class Funds extends JPanel{
	private JTextField textField, port, startDate, endDate;
	private JTextArea textArea;
	private Connection connection;

	public Funds(Connection c) throws IOException {
		connection = c; 
		//the title of panel
		JPanel out1 = new JPanel(new GridLayout(0,1));
		JPanel p1 = new JPanel();
		JLabel l1= new JLabel("Portfolios");
		l1.setFont(new Font("Serif", Font.BOLD, 20));
		p1.add(l1);
		out1.add(p1);

		//input portofolio data
		JPanel fund = new JPanel();
		port = new JTextField(7);
		startDate = new JTextField(7);
		endDate = new JTextField(7);

		fund.add(new JLabel("Portfolio:"));
		fund.add(port);
		fund.add(new JLabel("Start Date:"));
		fund.add(startDate);
		fund.add(new JLabel("End Date:"));
		fund.add(endDate);

		//buttons for any portofolio's return value and net worth
		JPanel p2 = new JPanel(new GridLayout(0,4));
		JButton retB = new JButton("Return");
		JButton worthB = new JButton("Worth");
		worthB.setSize(10, 10);
<<<<<<< HEAD
		// buttons to rank total rate of return and final net worth 
		JButton b1 = new JButton("total rate of return");
		JButton b2= new JButton("final net worth");
=======
		
		JButton b1 = new JButton("Rate of Return (All)");
		JButton b2= new JButton("Net Worth (All)");
>>>>>>> 4b578baea400e004cb08cd8b99421b14a5ceef72
		p2.add(b1);
		p2.add(b2);
		p2.add(retB);
		p2.add(worthB);
<<<<<<< HEAD
		//output button
		JButton totalreturno = new JButton("totalreturn output");
		JButton networtho = new JButton("networth output");
=======
		JButton reto = new JButton("Value");
		JButton wortho = new JButton("Worth");
		JButton totalreturno = new JButton("Save to File");
		JButton networtho = new JButton("Save to File");
	//	p2.add(reto);
	//	p2.add(wortho);
>>>>>>> 4b578baea400e004cb08cd8b99421b14a5ceef72
		p2.add(totalreturno);
		p2.add(networtho);

		JPanel out = new JPanel(new GridLayout(0,1));
		out.add(p1);
		out.add(fund);
		out.add(p2);
		add(out);

		//register return button, for any portofolio's rate of return
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

		//register worth button, for any portofolio's net worth
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

		// for ranking rate of return
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame f = showNewFrame("rate of return");
				Sql sql = new Sql();
				ResultSet set = sql.rankPortROR(connection, false);
				JTable t = createTable(set, 2);
				JScrollPane scrollPane = new JScrollPane(t);
				JPanel p = new JPanel();
				p.add(scrollPane);
				f.add(p);
			}
		});
		
		// export data of ranking total return to csv file 
		totalreturno.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				ResultSet set = sql.rankPortROR(connection, false);
<<<<<<< HEAD
				Output.getCsvFile("/home/xwang125/Desktop/totalReturnOutput.csv", set);
			}
		});
=======
					Output.getCsvFile("portfolioTotalReturn.csv", set);
				}
			});
		
		
		
>>>>>>> 4b578baea400e004cb08cd8b99421b14a5ceef72

		//rank total net worth for all the portofolios
		b2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFrame f = showNewFrame("total net worth");
				Sql sql = new Sql();
				ResultSet set = sql.portofolioTotalNetWorth(connection, 0,"2005-01-04", "2013-12-31");
				JTable t = createTable(set, 2);
				JScrollPane scrollPane = new JScrollPane(t);
				JPanel p = new JPanel();
				p.add(scrollPane);
				f.add(p);
			}
		});

		// export total net worth data to csv file
		networtho.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				ResultSet set = sql.portofolioTotalNetWorth(connection, 0,"2005-01-04", "2013-12-31");
<<<<<<< HEAD
				Output.getCsvFile("/home/xwang125/Desktop/networthreturn.csv", set);
			}
		});
=======
					Output.getCsvFile("portfolioNetWorth.csv", set);
				}
			});

>>>>>>> 4b578baea400e004cb08cd8b99421b14a5ceef72
	}

	//create table for data of return value and final net worth
	private JTable createTable(ResultSet rs, int numberOfColumns) {
		if(rs == null) {
			return null;
		}

		JPanel porto = new JPanel();
		setLayout(new FlowLayout());
		porto.add(new JLabel("Information of stock"));
		String[] columnNames = {"portofolio", "value"};
		DefaultTableModel model = null;
		JTable table = null;
		int rowcount = 0;

		try {
			if (rs.last()) {
				rowcount = rs.getRow();
				// not rs.first() because the rs.next() below will move on, 
				//missing the first element
				rs.beforeFirst(); 
			}
		
			JScrollPane scrollPane = new JScrollPane(table);
			porto.add(scrollPane);
			Object[][] data = new Object[rowcount][numberOfColumns];
			int j = 0;
			while (rs.next()) { //read data
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

	//create frame for table
	private JFrame showNewFrame(String title) {
		JFrame f = new JFrame();
		f.setTitle(title);
		f.setSize(400, 500);
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		return f;
	}
	//pop window with error message
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
