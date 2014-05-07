package gui;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.ResultSetMetaData;

public class Output {
	public static void getCsvFile(String fileName, ResultSet set) {
		try {
			FileWriter writer = new FileWriter(fileName);
			java.sql.ResultSetMetaData rsmd = set.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
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
