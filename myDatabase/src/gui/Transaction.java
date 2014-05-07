package gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import db.Process;
import db.Utils;

public class Transaction extends JPanel{
	//for transcaion:
	JTextField path;
	Connection connection;
	public Transaction(Connection c) {
		connection = c; 
		JPanel p1 = new JPanel();
		JLabel l1= new JLabel("transaction");
		l1.setFont(new Font("Serif", Font.BOLD, 20));
		p1.add(l1);


		JPanel file = new JPanel();
		JButton uploadFile = new JButton("uploadFile");
		file.add(uploadFile);
		path = new JTextField(25);
		file.add(path);


		JPanel out1 = new JPanel();
		out1.add(new JLabel("or"));
		JButton trans = new JButton("input");
		out1.add(trans);
		out1.add(new JLabel("individual:"));
		JTextField indiv = new JTextField(8);
		out1.add(indiv);

		uploadFile.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) {
				String fileName = path.getText(); 
				try {
					Process p = new Process(fileName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//
			}
		});





		JPanel out2 = new JPanel(new GridLayout(0, 4));
		out2.add(new JLabel("stock"));
		JTextField stock = new JTextField(8);
		out2.add(stock);
		out2.add(new JLabel("amount:"));
		JTextField amountS = new JTextField(8);
		out2.add(amountS);
		out2.add(new JLabel("portofolio:"));
		JTextField portfolio = new JTextField(8);
		out2.add(portfolio);
		out2.add(new JLabel("amount:"));
		JTextField amountP = new JTextField(8);
		out2.add(amountP);

		JPanel out3 = new JPanel(new GridLayout(0, 2));
		JRadioButton buy= new JRadioButton("buy");
		JRadioButton sell = new JRadioButton("sell");
	//	out3.add(buy);
	//	out3.add(sell);

		JButton partici = new JButton("majority participants");
		
		out3.add(partici);
		

		JPanel out = new JPanel(new GridLayout(0, 1));
		out.add(p1);
		out.add(file);
	//	out.add(out1);
	//	out.add(out2);
		out.add(out3);
		add(out);

		partici.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Sql sql = new Sql();
				ResultSet set = sql.mysteryQuery(connection);
				JFrame f = showNewFrame("majority participants");
				JTable t = createTable(set, 2);
				JPanel p = new JPanel();
				p.add(t);
				f.add(p);
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
		String[] columnNames = {"individual", "total networth"};
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


	private JFrame showNewFrame(String title) {
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