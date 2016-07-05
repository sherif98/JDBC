package eg.edu.alexu.csd.oop.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class Insrt_xml_parser {
	
	private String table_file;
	private String query;
	private ArrayList<Object[]> row;
	private ArrayList<Object[]> sample_row;
	BufferedWriter writer;
	
	public Insrt_xml_parser(String table_file, String query) throws IOException {
		this.table_file = table_file;
		this.query = query;
		
		row = new ArrayList<Object[]>();
		sample_row = new ArrayList<Object[]>();
		
		File file = new File(table_file);
		file.renameTo(new File(table_file + ".xml"));
		writer = new BufferedWriter(new FileWriter(new File(table_file)));
		
	}
	
	private void separate_query(){
		
		String insert = "\\s*([(]((\\s*[A-Za-z_0-9]+\\s*,)*\\s*([A-Za-z_0-9]+)\\s*)[)]\\s+)?";
		String all = insert + "[Vv][Aa][Ll][Uu][Ee][Ss]\\s+[(](\\s*(\\s*('[A-Za-z_0-9]+'|\\d+)\\s*,)*\\s*('[A-Za-z_0-9]+'|\\d+)\\s*)[)]\\s*";
		
	//	System.out.println(query.replaceAll(all, "$2"));
	//	System.out.println(query.replaceAll(all, "$5"));
		String[] cols = query.replaceAll(all, "$2").replaceAll("\\s+", "").split(",");
		String[] values = query.replaceAll(all, "$5").replaceAll("\\s+", "").split(",");
	//	System.out.println(query.replaceAll(all, "$2"));
		
		
		if(cols[0].equals(""))cols = null;

		
		for(int i = 0;i < values.length && cols == null; i++){
			Object[] sRow = sample_row.get(i);
			
			Object[] tmp = new Object[2];
			if(values[i].matches("'.+'"))tmp[1] = new String(values[i].replaceAll("'(.+)'", "$1"));
			else tmp[1] = Integer.parseInt(values[i]);
			
			if(!sRow[1].equals(tmp[1].getClass().getSimpleName()))throw new RuntimeException("error diff types");
			tmp[0] = sRow[0];
			row.add(tmp);
		}
		int num_cols = 0;
		for(Object[] sRow : sample_row){
			if(cols == null)break;
	//		System.out.println("s**" + (String) sRow[0]);
			boolean is_found = false;
			for(int i = 0; cols != null && i < cols.length; i++){
	//			System.out.println("c**" + (String) cols[0]);
				if(((String) sRow[0]).equalsIgnoreCase(cols[i])){
					num_cols++;
					is_found = true;
					
					Object[] tmp = new Object[2];
					tmp[0] = new String((String) cols[i]);
					if(values[i].matches("'.+'"))tmp[1] = new String(values[i].replaceAll("'(.+)'", "$1"));
					else tmp[1] = Integer.parseInt(values[i]);
					
					if(!sRow[1].equals(tmp[1].getClass().getSimpleName()))throw new RuntimeException("error diff types");
					row.add(tmp);
				}

			}
			if(!is_found){
				Object[] tmp = new Object[2];
				tmp[0] = new String((String) sRow[0]);
				tmp[1] = new String("null" + sRow[1]);
				row.add(tmp);
			}

		}
		if(cols != null && num_cols != cols.length)throw new RuntimeException("error wrong columns");
	}

	public int begin_insert() throws Exception{
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		
		SAXParser parser = factory.newSAXParser();
		write_in_file("<?xml version=\"1.0\"?>");
		try {
			parser.parse(new FileInputStream(new File(table_file + ".xml")), new SAXHandler());
			File f = new File(table_file + ".xml");
			f.delete();
			writer.close();
		} catch (Exception e) {
			throw e;
		}
		
		
		return 1;
	}
	
	private void write_in_file(String line){
		try {
			writer.write(line);
			writer.newLine();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("error");
		}
		
	}
	
	private void write_row(){
		write_in_file("<row>");
		for(Object[] tmp : row){
			String type = tmp[1].getClass().getSimpleName();
			if(tmp[1] instanceof String && ((String)tmp[1]).matches("null.+")){
				type = ((String) tmp[1]).replaceAll("null(.+)", "$1");
			}
			write_in_file("<" + (String) tmp[0] + " type=\"" + type + "\">");
			write_in_file(tmp[1].toString());
			write_in_file("</" + (String) tmp[0] + ">");
		}
		write_in_file("</row>");
	}
	
	private class SAXHandler extends DefaultHandler{
		
		boolean is_sample_row = true;
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes){
			
			if(is_sample_row && !qName.equals("table") && !qName.equals("srow")){
				
				Object[] cols = new Object[2];
				cols[0] = new String(qName);
				cols[1] = new String(attributes.getValue("type"));
				sample_row.add(cols);
			}
			
			String line = new String("");
			line += "<" + qName;
			
			if(attributes.getValue("type") != null){
				line += " type=\"" + attributes.getValue("type") + "\"";
			}
			line += ">";
			write_in_file(line);
			
		}
		
		@Override
		public void endElement(String uri, String localName, String qName){
			

			if(qName.equals("srow")){
				is_sample_row = false;
				separate_query();
			}
			else if(qName.equals("table")){
				write_row();
			}
			
			
			write_in_file("</" + qName + ">");
			
		}
		
		@Override
		public void characters(char[] chars, int start, int length){
			String content = String.copyValueOf(chars, start, length).trim();
			if(!content.equals(""))write_in_file(content);
		}

	
		
	}
	
}
