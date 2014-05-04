package gui;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class Transaction extends JPanel{
	//for transcaion:
	public Transaction() {
		JPanel p1 = new JPanel();
		JLabel l1= new JLabel("transaction");
		l1.setFont(new Font("Serif", Font.BOLD, 20));
		p1.add(l1);
	
		
		JPanel out1 = new JPanel();
		out1.add(new JLabel("individual:"));
		JTextField indiv = new JTextField(8);
		out1.add(indiv);
		JButton trans = new JButton("transaction");
		out1.add(trans);
		
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
		out3.add(buy);
		out3.add(sell);
		
		JButton partici = new JButton("majority participants");
		JButton tnw = new JButton("total net worth");
		out3.add(partici);
		out3.add(tnw);

		JPanel out = new JPanel(new GridLayout(0, 1));
		out.add(p1);
		out.add(out1);
		out.add(out2);
		out.add(out3);
		add(out);
	}
}