package eg.edu.alexu.csd.oop.db;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Updt_del_query_excuter {

	private String query;
	private String query_inf;
	private String table_file;
	private BufferedWriter writer;
	
	public Updt_del_query_excuter(String query, String query_inf, String table_file) throws IOException {
		
		this.query = query;
		this.query_inf = query_inf;
		this.table_file = table_file;
		File f = new File(table_file);
		f.renameTo(new File(table_file + ".xml"));
		
		writer = new BufferedWriter(new FileWriter(new File(table_file)));
		
	}
	
	protected int excute(ArrayList<Object[]> row) throws IOException{
		
		if(row == null)throw new RuntimeException("error");
		
		int num_successes = 0;
		
		String set = "(\\s*[A-Za-z_0-9]+\\s*=\\s*('[A-Za-z_0-9]+'|\\d+)\\s*,)*\\s*([A-Za-z_0-9]+\\s*=\\s*('[A-Za-z_0-9]+'|\\d+))\\s*";
		String regex_update = "\\s*[Ss][Ee][Tt]\\s+(" + set + ")\\s*([Ww][Hh][Ee][Rr][Ee](\\s+.+\\s*[>=<]\\s*.+\\s*))?";
		
		if(query.equals("delete")){
			
			if(query_inf == null || query_inf.replaceAll("\\s+", "").matches(".+[>|=|<].+")){
				if(delete(row))num_successes++;
			}
			else{
				writer.close();
				throw new RuntimeException("error");
			}
		}
	
		else if(query.equals("update")){
			
			if(!query_inf.trim().matches(regex_update))throw new RuntimeException("error no match for update");
			
		//	System.out.println(query_inf.replaceAll(regex_update, "$1"));
			String[] tmp_orders = query_inf.replaceAll(regex_update, "$1").replaceAll("\\s+", "").split(",");
			Object[][] orders = new Object[tmp_orders.length][];
			for(int i = 0;i < tmp_orders.length; i++){
				orders[i] = tmp_orders[i].split("=");

				if(((String) orders[i][1]).matches("'.+'")){
					String tmp = ((String) orders[i][1]).replaceAll("'(.+)'", "$1");
					orders[i][1] = new String(tmp);
				}
				else{
				//	System.out.println("|" + (String) orders[i][0] + "|" + (String) orders[i][1] + "|");

					String[] tmp = (String[]) orders[i];
					orders[i] = new Object[2];
					orders[i][0] = tmp[0];
					orders[i][1] = Integer.parseInt((tmp[1]));
					
				}
			}
			
			String where = query_inf.replaceAll(regex_update, "$7").replaceAll("\\s+", "");
			
			if(where.equals(""))where = null;
			
			num_successes += update(row, where, orders);
			

		}

		return num_successes;
	}
	
	private int update(ArrayList<Object[]> row, String where, Object[][] orders) throws IOException{
		
		ArrayList<Integer> result = null;
		try{
			result = compare(row, where);
		}catch(Exception e){
			throw e;
		}
		if(result == null){
	//		System.out.println("hjkhkjhjh");
			write_row(row);
			return 0;
		}
		
		boolean num_successes = false;
		for(Object[] order : orders){
			
			for(Object[] col : row){
		//		System.out.println((String) col[0]);
		//		System.out.println((String) order[0]);
				if(((String) col[0]).equalsIgnoreCase((String) order[0])){
					if(!col[1].getClass().equals(order[1].getClass()))throw new RuntimeException("error different types");
					col[1] = order[1];
					num_successes = true;
				}
				
			}
			
		}
		
		write_row(row);
		
		if(num_successes)return 1;
		return 0;
	}
	
	private boolean delete(ArrayList<Object[]> row) throws IOException{
		
		ArrayList<Integer> result;
		try{
			result = compare(row, query_inf);
			if(result == null)write_row(row);
			else return true;
		}catch(Exception e){
			throw e;
		}
		return false;
	}
	
	private void write_row(ArrayList<Object[]> row) throws IOException{
		
		writer.write("<row>");
		writer.newLine();
		for(Object[] o : row){
			
			writer.write("<" + (String) o[0] + " type=\"" + o[1].getClass().getSimpleName() + "\">");
			writer.write(o[1].toString());
			writer.write("</" + (String) o[0] + ">");
			writer.newLine();
			
		}
		writer.write("</row>");
		writer.newLine();
		
	}
	
	private ArrayList<Integer> compare(ArrayList<Object[]> row, String where){
		
		ArrayList<Integer> list_indices = new ArrayList<Integer>();

		
		if(where == null){
			for(int i = 0;i < row.size(); i++)list_indices.add(new Integer(i));
			return list_indices;
		}
		String first = where.replaceAll("(.+)([>|=|<])(.+)", "$1").trim();
		String second = where.replaceAll("(.+)([>|=|<])(.+)", "$3").trim();
		String oper = where.replaceAll("(.+)([>|=|<])(.+)", "$2").trim();
		String type = "Integer";
		System.out.println(second);
		if(second.matches("'.+'")){
			type = "String";
			second = second.replaceAll("'(.+)'", "$1");
		}
		//System.out.println(where);
		
		boolean found = false;
		int i = 0;
		
		for(Object[] o : row){
			
			if(((String) o[0]).equalsIgnoreCase(first)){
				found = true;
				if(oper.equals("=")){
					if(o[1] instanceof String){
						String tmp = (String) o[1];
						if(tmp.compareToIgnoreCase(second) == 0){
							list_indices.add(new Integer(i));
						}
					}
					else if(o[1] instanceof Integer){
						Integer tmp = (Integer) o[1];
						if(tmp.compareTo(Integer.valueOf(second)) == 0){
							list_indices.add(new Integer(i));
						}
					}
				}
				else if(oper.equals("<")){
					if(o[1] instanceof String){
						String tmp = (String) o[1];
						if(tmp.compareToIgnoreCase(second) < 0){
							list_indices.add(new Integer(i));
						}
					}
					else if(o[1] instanceof Integer){
						Integer tmp = (Integer) o[1];
						if(tmp.compareTo(Integer.valueOf(second)) < 0){
							list_indices.add(new Integer(i));
						}
					}
				}
				else if(oper.equals(">")){
					if(o[1] instanceof String){
						String tmp = (String) o[1];
						if(tmp.compareToIgnoreCase(second) > 0){
							list_indices.add(new Integer(i));
						}
					}
					else if(o[1] instanceof Integer){
						Integer tmp = (Integer) o[1];
						if(tmp.compareTo(Integer.valueOf(second)) > 0){
							list_indices.add(new Integer(i));
						}
					}
				}
			}
			i++;
		}
		
		if(!found)throw new RuntimeException("error : column not found");
		
		if(list_indices.size() != 0)return list_indices;
		return null;
	}
	
	protected BufferedWriter get_writer(){
		return writer;
	}
}
