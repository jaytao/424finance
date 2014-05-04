package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import db.Process;

public class Funds extends JPanel{
	private JTextField textField;
	private JTextArea textArea;

	Company portfolio;

	public Funds() throws IOException {

		JPanel out1 = new JPanel(new GridLayout(0,1));

		JPanel p1 = new JPanel();
		JLabel l1= new JLabel("Porfolios");
		l1.setFont(new Font("Serif", Font.BOLD, 20));
		p1.add(l1);
		out1.add(p1);

		//second panel
		JPanel fund = new JPanel();
		JTextField port = new JTextField(7);
		JTextField startDate = new JTextField(7);
		JTextField endDate = new JTextField(7);

		fund.add(new JLabel("portofolio:"));
		fund.add(port);
		fund.add(new JLabel("startDate:"));
		fund.add(startDate);
		fund.add(new JLabel("endDate:"));
		fund.add(endDate);


		//third panel
		JPanel p2 = new JPanel(new GridLayout(0,4));
		JButton retB = new JButton("returnValue");
		JButton worthB = new JButton("worth");
		worthB.setSize(10, 10);
		p2.add(retB);
		p2.add(worthB);
		JButton b1 = new JButton("total return");
		JButton b2= new JButton("fianl net worth");
		p2.add(b1);
		p2.add(b2);

		JPanel out = new JPanel(new GridLayout(0,1));
		out.add(p1);
		out.add(fund);
		out.add(p2);


		b1.addActionListener(new TotalReturn());
		//	b2.addActionListener(new Fnw());
		add(out);
	}




	public class TotalReturn implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {

		}
	}


}
