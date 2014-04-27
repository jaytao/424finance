package db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Command {
	
	public Command() {
		
	}
	
	
	public void run() {
		String input = "select * from activity";
		
	}
}
		//		String csvFile = "/home/xwang125/Class/cmsc424/project/script1.csv";
//		BufferedReader br = null;
//		String line = "";
//		String cvsSplitBy = ",";
//		ArrayList<String[]> list = new ArrayList(); 
//		try {
//			br = new BufferedReader(new FileReader(csvFile));
//			while((line = br.readLine()) != null) {
//				String[] found = line.split(cvsSplitBy);
//				list.add(found);
//			}
//					
//		}catch(FileNotFoundException e) {
//			System.out.println("can not find the file" + e);
//		}catch(IOException e) {
//			System.out.println("can not read from file" + e);
//		}
//		try {
//			br.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	//	System.out.println(list.get(0)[0], list.get(0)[1],list.get(0)[2],list.get(0)[3],list.get(0)[4] );
//		
//	}
//	public static void main(String[] args) {
//		Readfile obj = new Readfile();
//		obj.run();
//	}
//}
