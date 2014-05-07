package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import db.Utils;

/*
 *Create a frame as interface 
 * 
 */
public class Frame {
	private static void createFrame() throws IOException {
		Connection con = Utils.connectToSQL("root", "dingding1016");
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Stock");
		int width = 1080, height = 800;
		//set size
		frame.setSize(new Dimension(width, height));
		//set layout for the frame
		frame.setLayout(new GridLayout(0, 2, 20, 50));
		//add containers
		frame.add(new Company(con,50,60));
		frame.add(new Transaction(con));
		frame.add(new Funds(con));
		frame.add(new Individual(con));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int upperLeftCornerX = (screenSize.width - frame.getWidth()) / 2;
		int upperLeftCornerY = (screenSize.height - frame.getHeight()) / 2;
		frame.setLocation(upperLeftCornerX, upperLeftCornerY);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createFrame();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
