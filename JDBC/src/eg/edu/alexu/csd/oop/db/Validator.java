package eg.edu.alexu.csd.oop.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Validator {
	
	public void initTable(String table_file, String query) throws IOException{

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(table_file + ".xml")));
		
		String[] tmp = query.split(",");
		
		for(int i = 0; i < tmp.length; i++){
			tmp[i] = tmp[i].trim();
		}
	//	String[][] cols = new String[tmp.length][];
		
		writer.write("<?xml version=\"1.0\"?>");
		writer.newLine();
		writer.write("<table>");
		writer.newLine();
		writer.write("<srow>");
		writer.newLine();
		
		for(int i = 0; i < tmp.length; i++){
			String[] cols = tmp[i].split("\\s+");
			
			String type;
			if(cols[1].equalsIgnoreCase("int")){
				type = "Integer";
			}
			else{
				type = "String";
			}
			writer.write("<" + cols[0] + " type=\"" + type + "\"/>");
			writer.newLine();
			
		}
		
		writer.write("</srow>");
		writer.newLine();
		writer.write("</table>");
		writer.close();
		
	}
	
}
