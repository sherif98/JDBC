package eg.edu.alexu.csd.oop.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Updt_del_xml_parser {
	
	
	private Updt_del_query_excuter compare;
	private int num_successes;
	
	public Updt_del_xml_parser(Updt_del_query_excuter compare) throws IOException {
		this.compare = compare;
		num_successes = 0;
		compare.get_writer().write("<?xml version=\"1.0\"?>");
		compare.get_writer().newLine();
		compare.get_writer().write("<table>");
		compare.get_writer().newLine();
	}
	
	public int apply_query(String table_file) throws ParserConfigurationException, SAXException, IOException{
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		
		SAXParser parser = factory.newSAXParser();
		
		try{
			File tmp = new File(table_file);
			tmp.deleteOnExit();
			parser.parse(new FileInputStream(tmp), new SAXHandler());
			compare.get_writer().write("</table>");
			compare.get_writer().close();
		}catch(Exception e){
			throw e;
		}
		
		
		return num_successes;
	}
	
	
	private class SAXHandler extends DefaultHandler{
		
		private ArrayList<Object[]> row;
		private String type, content;
		private boolean is_sample = true;
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes){
			
			if(is_sample && !qName.equals("table")){
				if(qName.equals("srow")){
					try {
						compare.get_writer().write("<srow>");
						compare.get_writer().newLine();
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("error");
					}
					
				}
				else{
					try {
						compare.get_writer().write("<" + qName + " type=\"" + attributes.getValue("type") + "\">");
						compare.get_writer().newLine();
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("error");
					}
				}
				return;
			}
			if(qName.equals("row")){
				row = new ArrayList<Object[]>();
			}
			else if(!qName.equals("table")){
				type = attributes.getValue("type");
			}
			
			
		}
		
		@Override
		public void endElement(String uri, String localName, String qName){
			
			if(qName.equals("table"))return;
			
			if(is_sample){
				if(qName.equals("srow")){
					try {
						compare.get_writer().write("</srow>");
						compare.get_writer().newLine();
						is_sample = false;
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("error");
					}
					
				}
				else{
					try {
						compare.get_writer().write("</" + qName + ">");
						compare.get_writer().newLine();
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("error");
					}
				}
				return;
			}
			
			else if(qName.equals("row")){
				try {
					num_successes += compare.excute(row);
				} catch (IOException e) {
					throw new RuntimeException("error");
				}
			}
			
			Object[] tmp = new Object[2];
			
			tmp[0] = qName;
			if(type.equals("String")){
				tmp[1] = content;
			}
			else{
				tmp[1] = Integer.parseInt(content);
			}

			row.add(tmp);
			
		}
		
		@Override
		public void characters(char[] chars, int start, int length){
			
			content = String.valueOf(chars, start, length).trim();
			
		}
		
	}
}
