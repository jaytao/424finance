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
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.sql.Connection;
import db.Process;
import db.Utils;


public class Company extends JPanel{


	private JTable table;
	private JTextField textField, quoteInformation;
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
				System.out.println(rt);
				if(arg0.getSource() instanceof JButton) {
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
		display.add(new JTextField(10));
		display.add(new JLabel("Start: "));
		display.add( new JTextField(10));

		JPanel d1 = new JPanel();
		d1.add(new JLabel("end: "));
		d1.add( new JTextField(10));
		display.add(d1);

		//third panel
		JPanel out2 = new JPanel(new GridLayout(0,1));
		JPanel compare = new JPanel();
		JButton compareB = new JButton("compare");
		compare.add(compareB);		
		compare.add(new JLabel("stock1"));
		compare.add(new JTextField(10));
		compare.add(new JLabel("stock2"));
		compare.add(new JTextField(10));

		JPanel p3 = new JPanel();
		JButton top25 = new JButton("top 25 stocks in Annualized ROR");
		JButton lowest = new JButton("top five lowest-risk stocks");
		p3.add(top25);
		p3.add(lowest);
		out2.add(compare);
		out2.add(p3);

		JPanel outer = new JPanel(); // for outmost
		outer.setLayout(new GridLayout(0, 1, 10, 10));

		outer.add(out1);
		outer.add(display);
		outer.add(out2);
		add(outer, BorderLayout.NORTH);
		top25.addActionListener(new Stock25Listener());

		//======================================		


		//		quoteInformation = new JTextField(10);
		//	quoteInformation.setEditable(false);
		//		display.add(quoteInformation);

		//	display.setAlignmentX(Component.LEFT_ALIGNMENT);
		//	add(display, BorderLayout.WEST);
		//		b.addActionListener(new Stock25Listener());


		//		//	readData.addActionListener(new MyButtonListener());







	}

	private void showNewFrame(String title, String output) {
		JFrame f = new JFrame();
		f.setTitle(title);
		f.setSize(400, 500);
		f.setLocationRelativeTo(null);
		JPanel showResult = new JPanel();
		JTextArea area = new JTextArea();
		area.setText(output);
		JScrollPane scroll = new JScrollPane(area);
		showResult.add(scroll);

		f.add(showResult);
		f.setVisible(true);

	}




	private class Stock25Listener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {

			//			if(arg0.getSource() instanceof JButton) {
			//				showNewFrame();
			//			}
		}




		//			String text = textField.getText();
		//			try {
		//				ResultSet result = command.select(text);
		//				String re = "";
		//				while(result.next()) {
		//					System.out.println("hello");	
		//					String s = result.getString("age");
		//					re += s; 
		//				}
		//				re = text + "  " + re;
		//				textArea.setText(re);
		//			} catch (SQLException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
	}

}


