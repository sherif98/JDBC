package eg.edu.alexu.csd.oop.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


public class Slct_xml_parser {
	//---> if exception in integer >> null, if string and matches nullString >> return null
	
	private String query;
	private String[] cols;
	private String condition;
	private String table_file;
	private ArrayList<Object[]> sel_rows;
	private ArrayList<String> cols_names_toReturn;
	
	public Slct_xml_parser(String query, String destination) throws FileNotFoundException {
		
		this.query = query;
		
		sel_rows = new ArrayList<Object[]>();
		cols_names_toReturn = new ArrayList<String>();
		
		table_file =  destination + Character.toString(File.separatorChar);
		set_variables();
	}
	
	private void set_variables(){
		
		String select1 = "\\s*[Ss][Ee][Ll][Ee][Cc][Tt]\\s+";
		String select2 = "(((\\s*\\w+\\s*,)*\\s*\\w+)|[*])";
		String select3 = "\\s+[Ff][Rr][Oo][Mm]\\s+(.+)";
		String all = "(\\s+[Ww][Hh][Ee][Rr][Ee]\\s+(.+\\s*[>=<]\\s*.+)\\s*)";
		
		String cols_to_selects;
		if(query.matches(select1 + select2 + select3 + all)){
			cols_to_selects = query.replaceAll(select1 + select2 + select3 + all, "$1").replaceAll("\\s+", "");
			condition = query.replaceAll(select1 + select2 + select3 + all, "$6").replaceAll("\\s+", "");
			table_file += query.replaceAll(select1 + select2 + select3 + all, "$4").replaceAll("\\s+", "").toLowerCase() + ".xml";
		}
		else if(query.matches(select1 + select2 + select3 + "\\s*")){
			cols_to_selects = query.replaceAll(select1 + select2 + select3 + "\\s*", "$1").replaceAll("\\s+", "");
			condition = "";
			table_file += query.replaceAll(select1 + select2 + select3 + "\\s*", "$4").replaceAll("\\s+", "").toLowerCase() + ".xml";
		}
		else{
			throw new RuntimeException("wrong insert query");
		}
		
	//	System.out.println(condition);
		
		
		if(condition.equals(""))condition = null;
		if(cols_to_selects.equals("*")){
			cols = null;
		}
		else{
			cols = cols_to_selects.split(",");
		}
		
	}
	
	
	private boolean is_matching(ArrayList<Object[]> row){
				
		if(condition == null){
			return true;
		}
		
		String first = condition.replaceAll("(.+)([>|=|<])(.+)", "$1").trim();
		String second = condition.replaceAll("(.+)([>|=|<])(.+)", "$3").trim();
		String oper = condition.replaceAll("(.+)([>|=|<])(.+)", "$2").trim();
		String type = "Integer";
		if(second.matches("'.+'")){
			type = "String";
			second = second.replaceAll("'(.+)'", "$1");
		}
		
		boolean found = false;
	//	System.out.println(row.size());
		for(Object[] o : row){
	//		System.out.println(o[0].toString() + "  " + o[1].toString());
			if(((String) o[0]).equalsIgnoreCase(first)){
				found = true;
				if(oper.equals("=")){
					if(o[1] instanceof String){
						String tmp = (String) o[1];
						if(tmp.compareToIgnoreCase(second) == 0){
							return true;
						}
					}
					else if(o[1] instanceof Integer){
						Integer tmp = (Integer) o[1];
						if(tmp.compareTo(Integer.valueOf(second)) == 0){
							return true;
						}
					}
				}
				else if(oper.equals("<")){
					if(o[1] instanceof String){
						String tmp = (String) o[1];
						if(tmp.compareToIgnoreCase(second) < 0){
							return true;
						}
					}
					else if(o[1] instanceof Integer){
						Integer tmp = (Integer) o[1];
						if(tmp.compareTo(Integer.valueOf(second)) < 0){
							return true;
						}
					}
				}
				else if(oper.equals(">")){
					if(o[1] instanceof String){
						String tmp = (String) o[1];
						if(tmp.compareToIgnoreCase(second) > 0){
							return true;
						}
					}
					else if(o[1] instanceof Integer){
						Integer tmp = (Integer) o[1];
						if(tmp.compareTo(Integer.valueOf(second)) > 0){
							return true;
						}
					}
				}
			}
			
		}
		
		if(!found)throw new RuntimeException("error : column not found");
		
		return false;
	}
	
	public Node[][] get_selected() throws Exception{
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		
		SAXParser parser = factory.newSAXParser();
		
		try{
			parser.parse(new FileInputStream(new File(table_file)), new SAXHandler());
		}catch(Exception e){
			throw e;
		}
		
		for(int i = 0;cols != null && i < cols.length; i++){
			for(int j = i; j < cols_names_toReturn.size(); j++){
				if(!cols[i].equals(cols_names_toReturn.get(j)))continue;
				//swap cols_names_toReturn
				String tmp_i = cols_names_toReturn.get(i);
				String tmp_j = cols_names_toReturn.get(j);
				cols_names_toReturn.remove(j);
				cols_names_toReturn.add(j, tmp_j);
				cols_names_toReturn.remove(i);
				cols_names_toReturn.add(i, tmp_i);
				
				//swap sel_rows
				for(int k = 0; k < sel_rows.size(); k++){
					Object[] row = sel_rows.get(k);
					Object temp = row[i];
					row[i] = row[j];
					row[j] = temp;
				}
				
			}
		}
		
		
		//preparing for return
		Node[][] toReturn = new Node[sel_rows.size()][];
		
		for (int i = 0; i < sel_rows.size(); i++) {

			Object[] tmp = sel_rows.get(i);
			toReturn[i] = new Node[tmp.length];
			for(int j = 0; j < tmp.length; j++){
				toReturn[i][j] = new Node(tmp[j], cols_names_toReturn.get(j));
			}
		}
		
		return toReturn;
	}
	
	private class SAXHandler extends DefaultHandler{
		
		private boolean is_in_sample = true;
		private ArrayList<Object> row;
		private ArrayList<Object[]> whole_row;
		private String type;
		private String content;
		private int n_rows_matched = 0;
		
		@Override
		public void startElement(String Uri, String localName, String qName, Attributes attributes){
			
			if(qName.equals("table"))return;
			
			if(qName.equals("row")){
				row = new ArrayList<Object>();
				whole_row = new ArrayList<Object[]>();
				return;
			}
			
			if(!is_in_sample){
				type = attributes.getValue("type");
				return;
			}
			
			if(cols == null)return;
			
			for(String col : cols){
				if(col.equalsIgnoreCase(qName)){
					n_rows_matched++;
					return;
				}
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName){
			
			if(qName.equals("srow")){
				is_in_sample = false;
				if(cols != null && n_rows_matched != cols.length)throw new RuntimeException("invalid columns");
				return;
			}
			if(qName.equals("table"))return;
			if(qName.equals("row")){
				if(is_matching(whole_row))sel_rows.add(row.toArray());
			}
			if(is_in_sample){
				if(cols == null){
					cols_names_toReturn.add(qName);
					return;
				}
				for(String s : cols){
					if(s.equals(qName)){
						cols_names_toReturn.add(qName);
						break;
					}
				}
				return;
			}
			Object col;
			Object[] col_whole_row = new Object[2];
			col_whole_row[0] = new String(qName);

			if(type.equals("String")){
				col = new String(content);
				col_whole_row[1] = new String(content);
			}
			else{
				col = new Integer(Integer.parseInt(content));
				col_whole_row[1] = new Integer(Integer.parseInt(content));
			}
			
			whole_row.add(col_whole_row);
			
			if(cols == null){
				row.add(col);
				return;
			}
			
			for(String s : cols){
				if(qName.equalsIgnoreCase(s)){
					row.add(col);
					return;
				}
			}
		}
		
		@Override
		public void characters(char[] chars, int start, int length){
			
			content = String.copyValueOf(chars, start, length).trim();
			
		}
	}
	
}
