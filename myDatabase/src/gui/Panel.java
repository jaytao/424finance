package gui;

import gui.Quote.MyButtonListener;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import db.Process;


public class Panel extends JPanel{
	private JTable table;
	private JTextField textField;
	private JTextArea textArea;
	Process command;
	private int width, height;
	public Panel(int width, int height) throws IOException {
		 command = new Process();
		setLayout(new BorderLayout());
		this.width = width;
		this.height = height;
	}
	
	public void getRateOfReturn() {
		JPanel display = new JPanel();
		display.add(new JLabel("Rate of Return: "));
		textArea = new JTextArea();
		textArea.setEditable(false);
		JScrollPane scrollPane2 = new JScrollPane(textArea);
		scrollPane2.setPreferredSize(new Dimension(4 * width / 10, height / 10));
		display.add(scrollPane2);
		add(display, BorderLayout.NORTH);
		
		JPanel inputCompany = new JPanel();
		inputCompany.add(new JLabel("company: "));
		textField = new JTextField(10);
		inputCompany.add(textField);
		inputCompany.add(new JLabel("Start: "));
		inputCompany.add( new JTextField(10));
		inputCompany.add(new JLabel("end: "));
		inputCompany.add( new JTextField(10));
		JButton readData = new JButton("sumit");
		inputCompany.add(readData);
		add(inputCompany, BorderLayout.CENTER);
		readData.addActionListener(new MyButtonListener());
	}
	public void portofolio(){
		JPanel porto = new JPanel();
		setLayout(new FlowLayout());
		porto.add(new JLabel("Information of stock"));
		String[] columnNames = {"Stock", "date", "price"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 9);
		table = new JTable(model);
//		table.setTableHeader(new JTableHeader());
		table.setPreferredScrollableViewportSize(new Dimension(500, 100));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);
		porto.add(scrollPane);
		porto.add(new JButton("submit"));
		add(porto, BorderLayout.CENTER);
		
	//	table.setLocation(200, 200 );
		
	}
	
	public void stockPerformance() {
		JPanel stockP = new JPanel();
		setLayout(new FlowLayout());
		stockP.add(new JLabel("stock performance"));
		
	}

	private class MyButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			String text = textField.getText();
			try {
				ResultSet result = command.select(text);
				String re = "";
				while(result.next()) {
					System.out.println("hello");	
					String s = result.getString("age");
					re += s; 
				}
				re = text + "  " + re;
				textArea.setText(re);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
