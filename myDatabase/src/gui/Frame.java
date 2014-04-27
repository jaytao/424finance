package gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Frame {
	private static void createFrame() throws IOException {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Stock information");
		int width = 1000, height = 900;
		frame.setSize(new Dimension(width, height)); 
	//	frame.setContentPane(new Quote());
		Panel panel = new Panel(width, height);
		panel.getRateOfReturn();
		panel.portofolio();
		frame.setContentPane(panel);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int upperLeftCornerX = (screenSize.width - frame.getWidth()) / 2;
		int upperLeftCornerY = (screenSize.height - frame.getHeight()) / 2;
		frame.setLocation(upperLeftCornerX, upperLeftCornerY);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		System.out.println("==================");
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
