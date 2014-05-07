package gui;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Export table data to csv file
 */

public class Output {
	public static void getCsvFile(String fileName, ResultSet set) {
		try {
<<<<<<< HEAD
			//create writer for output file
			FileWriter writer = new FileWriter(fileName);
=======
			PrintWriter writer = new PrintWriter(fileName, "UTF-8");
			
>>>>>>> 4b578baea400e004cb08cd8b99421b14a5ceef72
			java.sql.ResultSetMetaData rsmd = set.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			//write data to output file
			while(set.next()) {
				for(int i = 1; i <= columnsNumber; i++) {
					String s = set.getString(i);
					writer.append(s);
					writer.append(',');
				}
				writer.append('\n');
			}
			writer.flush();
		    writer.close();
		}catch(IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
