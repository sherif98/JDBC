package eg.edu.alexu.csd.oop.db;

import java.io.File;
import java.sql.SQLException;

import eg.edu.alexu.csd.oop.db.Database;
import eg.edu.alexu.csd.oop.db.JdbcExtraStuff;

public class DbController implements Database, JdbcExtraStuff{
	
	private String crnt_db_name = null;
	private String save_folder = "k_save" + Character.toString(File.separatorChar);
	private Node[][] cols_of_select;
	//private static int x = 0;
	
	public DbController() {

		if(!new File(save_folder).exists()){
			new File(save_folder).mkdirs();
		}

	}
	
	public Node[][] getXtraInf(){return cols_of_select;}
	
	public void set_save_destination(String save_destination){
		save_folder = save_destination;
//		if(!save_folder.matches(".+(" + Character.toString(File.separatorChar) + ")")){
//			save_folder += Character.toString(File.separatorChar);
//		}
		if(!new File(save_folder).exists()){
			new File(save_folder).mkdirs();
		}
	}

	@Override
	public String createDatabase(String databaseName, boolean dropIfExists) {
		
		File f;
		f = new File(save_folder + databaseName);

		if(f.exists()){
			if(dropIfExists){
				try {
					executeStructureQuery("DROP DATABASE " + databaseName);
					executeStructureQuery("CREATE DATABASE " + databaseName);
				} catch (SQLException e) {
					return null;
				}
			}
			else{
				crnt_db_name = databaseName;
			}
			
		}
		else{
			try {
				executeStructureQuery("CREATE DATABASE " + databaseName);
			} catch (SQLException e) {
			//	e.printStackTrace();
				return null;
			}
		}
		
		return f.getAbsolutePath();
	}

	@Override
	public boolean executeStructureQuery(String query) throws SQLException {
		
		if(query.matches("\\s*[Cc][Rr][Ee][Aa][Tt][Ee]\\s+[Dd][Aa][Tt][Aa][Bb][Aa][Ss][Ee]\\s+.+")){
			String db_name = query.replaceAll("(\\s*[Cc][Rr][Ee][Aa][Tt][Ee]\\s+[Dd][Aa][Tt][Aa][Bb][Aa][Ss][Ee]\\s+)(.+)", "$2").trim();
			File f = new File(save_folder + db_name);
			if(!f.exists()){
				if(!f.mkdir()){
					return false;
				}
				crnt_db_name = db_name;
				return true;
			}
			else{
				crnt_db_name = db_name;
				return true;
			}
		}
		else if(query
				.matches("(?i)\\s*CREATE\\s+TABLE\\s+(.+)\\s*[(](.+)((int|varchar),)*(.+)((int|varchar))[)]\\s*")) {
			String table_name = save_folder + crnt_db_name + File.separatorChar;
			table_name += query
					.replaceAll(
							"(?i)\\s*CREATE\\s+TABLE\\s+(.+)\\s*[(](.+)((int|varchar),)*(.+)((int|varchar))[)]\\s*",
							"$1").trim().toLowerCase();
			if(crnt_db_name == null){
				throw new SQLException("No Data Base Found");
			}
			
			File f = new File(table_name + ".xml");
			if(!f.exists()){
				try {
					f.createNewFile();
					Validator validator = new Validator();
					String create = "(?i)\\s*create\\s+table\\s+.+[(](.+)[)]\\s*";
					String query_inf = query.replaceAll(create, "$1").trim();
					//System.out.println(query_inf);
					validator.initTable(table_name, query_inf);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
			else{
				return false;
			}
		}
		else if(query.matches("\\s*[Dd][Rr][Oo][Pp]\\s+[Dd][Aa][Tt][Aa][Bb][Aa][Ss][Ee]\\s+.+")){
			String db_name = query.replaceAll("(\\s*[Dd][Rr][Oo][Pp]\\s+[Dd][Aa][Tt][Aa][Bb][Aa][Ss][Ee]\\s+)(.+)", "$2").trim();
			File f = new File(save_folder + db_name);
			if(f.exists()){
				File[] files = f.listFiles();
				for(File file : files){
					file.delete();
				}
				f.delete();
				crnt_db_name = null;
				return true;
			}
			else{
				return false;
			}
		}
		else if(query.matches("\\s*[Dd][Rr][Oo][Pp]\\s+[Tt][Aa][Bb][Ll][Ee]\\s+.+")){
			String table_name = query.replaceAll("(\\s*[Dd][Rr][Oo][Pp]\\s+[Tt][Aa][Bb][Ll][Ee]\\s+)(.+)", "$2");
			
			if(crnt_db_name == null){
				throw new SQLException("No Data Base Found");
			}
			
			File f = new File(save_folder + crnt_db_name + File.separatorChar + table_name + ".xml");
			if(f.exists()){
				f.delete();
				return true;
			}
			else{
				return false;
			}
		}
	
		throw new SQLException("wrong SQL");
	}
	@Override
	public Object[][] executeQuery(String query) throws SQLException {

		if(crnt_db_name == null)throw new SQLException("no data base selected");
		
		Node[][] subSetTable;
		try{
			Slct_xml_parser p = new Slct_xml_parser(query, save_folder + crnt_db_name);
			subSetTable = p.get_selected();
			cols_of_select = subSetTable;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new SQLException(e);
		}
		
		Object[][] tmp = new Object[subSetTable.length][];
		for(int i = 0; i < subSetTable.length; i++){
			tmp[i] = new Object[subSetTable[i].length];
			for(int j = 0; j < subSetTable[i].length; j++){
				tmp[i][j] = subSetTable[i][j].get_element();
			}
		}
		return tmp;
	}
	@Override
	public int executeUpdateQuery(String query) throws SQLException {

		if(crnt_db_name == null)throw new SQLException("no data base selected");
		
		if(query == null)throw new SQLException("invalid query");
		
		if(query.matches("\\s*[Dd][Ee][Ll][Ee][Tt][Ee]\\s+[Ff][Rr][Oo][Mm]\\s+.+\\s+[Ww][Hh][Ee][Rr][Ee]\\s+.+")){

			String query_inf = query.replaceAll("\\s*[Dd][Ee][Ll][Ee][Tt][Ee]\\s+[Ff][Rr][Oo][Mm]\\s+.+\\s+[Ww][Hh][Ee][Rr][Ee]\\s+(.+)", "$1").trim();
			String table_file = save_folder + crnt_db_name + Character.toString(File.separatorChar);
			table_file += query.replaceAll("\\s*[Dd][Ee][Ll][Ee][Tt][Ee]\\s+[Ff][Rr][Oo][Mm]\\s+(.+)\\s+[Ww][Hh][Ee][Rr][Ee]\\s+.+", "$1").trim().toLowerCase();
			
			File temp_file = new File(table_file + ".xml");
			if(!temp_file.exists())throw new SQLException("no such table");
			try{
				Updt_del_query_excuter compare = new Updt_del_query_excuter("delete", query_inf, table_file + ".xml");
				Updt_del_xml_parser parser = new Updt_del_xml_parser(compare);
				return parser.apply_query(table_file + ".xml.xml");
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		else if(query.matches("\\s*[Dd][Ee][Ll][Ee][Tt][Ee]\\s+([*]\\s+)?[Ff][Rr][Oo][Mm]\\s+.+")){
			
			String table_file = save_folder + crnt_db_name + Character.toString(File.separatorChar);
			table_file += query.replaceAll("\\s*[Dd][Ee][Ll][Ee][Tt][Ee]\\s+([*]\\s+)?[Ff][Rr][Oo][Mm]\\s+(.+)", "$2").trim().toLowerCase();
			
			File temp_file = new File(table_file + ".xml");
			if(!temp_file.exists())throw new SQLException("no such table");
			try{
				Updt_del_query_excuter compare = new Updt_del_query_excuter("delete", null, table_file + ".xml");
				Updt_del_xml_parser parser = new Updt_del_xml_parser(compare);
				return parser.apply_query(table_file + ".xml.xml");
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
		}
		else if(query.matches("\\s*[Uu][Pp][Dd][Aa][Tt][Ee]\\s+.+\\s+[Ss][Ee][Tt]\\s+.+")){

			String table_file = save_folder + crnt_db_name + Character.toString(File.separatorChar);
			String query_inf = query.replaceAll("\\s*[Uu][Pp][Dd][Aa][Tt][Ee]\\s+.+\\s+([Ss][Ee][Tt]\\s+.+)", "$1").trim();
			table_file += query.replaceAll("\\s*[Uu][Pp][Dd][Aa][Tt][Ee]\\s+(.+)\\s+[Ss][Ee][Tt]\\s+.+", "$1").trim().toLowerCase();
			
			File temp_file = new File(table_file + ".xml");
			if(!temp_file.exists()){
				throw new SQLException("no such table");
			}
			try{
				Updt_del_query_excuter compare = new Updt_del_query_excuter("update", query_inf, table_file + ".xml");
				Updt_del_xml_parser parser = new Updt_del_xml_parser(compare);
				return parser.apply_query(table_file + ".xml.xml");
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		else if(query.matches("\\s*[Ii][Nn][Ss][Ee][Rr][Tt]\\s+[Ii][Nn][Tt][Oo]\\s+.+\\s+[Vv][Aa][Ll][Uu][Ee][Ss]\\s+[(].+[)]\\s*")){
			
			String table_file = save_folder + crnt_db_name + Character.toString(File.separatorChar);
			String query_inf;
			String regex_table = "\\s*[Ii][Nn][Ss][Ee][Rr][Tt]\\s+[Ii][Nn][Tt][Oo]\\s+(.+)\\s*[(].+[)]\\s+[Vv][Aa][Ll][Uu][Ee][Ss]\\s+[(].+[)]\\s*";
			
			if(query.matches(regex_table)){
				table_file += query.replaceAll("\\s*[Ii][Nn][Ss][Ee][Rr][Tt]\\s+[Ii][Nn][Tt][Oo]\\s+(.+)\\s*[(].+[)]\\s+[Vv][Aa][Ll][Uu][Ee][Ss]\\s+[(].+[)]\\s*", "$1").trim().toLowerCase();
				query_inf = query.replaceAll("\\s*[Ii][Nn][Ss][Ee][Rr][Tt]\\s+[Ii][Nn][Tt][Oo]\\s+.+\\s*([(].+[)]\\s+[Vv][Aa][Ll][Uu][Ee][Ss]\\s+[(].+[)]\\s*)", "$1").trim();

			}else{
				table_file += query.replaceAll("\\s*[Ii][Nn][Ss][Ee][Rr][Tt]\\s+[Ii][Nn][Tt][Oo]\\s+(.+)\\s+[Vv][Aa][Ll][Uu][Ee][Ss]\\s+[(].+[)]\\s*", "$1").trim().toLowerCase();
				query_inf = query.replaceAll("\\s*[Ii][Nn][Ss][Ee][Rr][Tt]\\s+[Ii][Nn][Tt][Oo]\\s+.+\\s+([Vv][Aa][Ll][Uu][Ee][Ss]\\s+[(].+[)]\\s*)", "$1").trim();
			}

			File temp_file = new File(table_file + ".xml");
			if(!temp_file.exists())throw new SQLException("no such table");
			try {
				Insrt_xml_parser parser = new Insrt_xml_parser(table_file + ".xml", query_inf);
				return parser.begin_insert();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
		}
		return 0;
	}



}
