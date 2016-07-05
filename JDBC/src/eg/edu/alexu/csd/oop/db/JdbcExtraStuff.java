package eg.edu.alexu.csd.oop.db;


public interface JdbcExtraStuff {
	
	/**
	* keeps the order in each query such as selecting colum2 , colum1 from
	* a table with colum1 , colum2
	* @return selected information the same as that of method excuteQuery() , 
	* but each element is with its column name
	*/
	public Node[][] getXtraInf();
	
	/**
	* set the destination to save databases manually.
	* is set to "[path to application]\k_save\" by default
	* @param save_destination path to begin saving databases
	*/
	public void set_save_destination(String save_destination);
}
