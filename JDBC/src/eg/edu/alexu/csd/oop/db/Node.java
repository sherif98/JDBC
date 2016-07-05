package eg.edu.alexu.csd.oop.db;

public class Node {
	private Object element;
	private String col_name;
	
	/*
	 * make sure you remove the underscores in the names 
	 * this is not a java convenient 
	 */
	
	public Node(Object element, String col_name) {
		this.element = element;
		this.col_name = col_name;
	}
	
	public String get_col_name(){return col_name;}
	public Object get_element(){return element;}
	
	/*
	 * I need a method to return the column type. example varchar, int ....
	 */
	
	public String getColumnType() {
		return null;
	}
	
	/*
	 *  I need a method to return the name of the table which this column belongs to
	 */
	
	public String getTableName() {
		return null;
	}
}
