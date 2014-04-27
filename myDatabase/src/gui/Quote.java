package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import db.Process;

public class Quote extends JPanel{
	private JTextField textField;
	private JTextArea textArea;
	Process command;
	Panel portfolio;
	public Quote() throws IOException {
		command = new Process();
		add(new JLabel("company "));
		textField = new JTextField(10);
	//	textArea = new JTextArea(5, 20);
		JScrollPane scrollPane = new JScrollPane(textArea);
		JButton readData = new JButton("quote");
		add(textField);
	//	add(textArea);
		add(readData);
		readData.addActionListener(new MyButtonListener());
	}
	
	public class MyButtonListener implements ActionListener{
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
				textField.setText(re);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
