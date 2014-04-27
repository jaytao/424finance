package db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Company {
	ArrayList<String> companies;
	public Company() {
		 this. companies = new ArrayList<String>();
	}
	
	public ArrayList<String> getCompany() throws IOException {
		String csvFile = "/home/xwang125/Class/cmsc424/project/companies";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	
			br = new BufferedReader(new FileReader(csvFile));
			while((line = br.readLine()) != null) {
				String newL = line.replaceAll("\\s+","");
				String[] st = newL.split(cvsSplitBy);
				for(String s: st) {					
					companies.add(s);
				}
			}
			br.close();
			return companies;
	}
//	public static void main(String[] args) throws IOException {
//		Company comp = new Company();
//		ArrayList<String> list  = comp.getCompany();
//		for(int i = 0; i < list.size(); i++) {
//			System.out.println("Hello" + list.get(i) + "Hello");		 
//		}
//	}
}