package eg.edu.alexu.csd.oop.jdbc;

import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;

import eg.edu.alexu.csd.oop.db.*;

public class MyStatement implements Statement {

	private ArrayList<String> commands;
	private Connection connection;
	private DbController engine;
	private static boolean callMeOnce ; 
	private boolean fileCreated ;
	public MyStatement(Connection connection, DbController engine) {
		
		commands = new ArrayList<>();
		this.connection = connection;
		this.engine = engine;
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void addBatch(String query) throws SQLException {
		// TODO Auto-generated method stub
		// if the given SQL query is invalid we should throw sql exception
		if (connection == null) {
			throw new SQLException();
		}
		// if (!MyEngin.isValidQuery(query)) {
		// throw new SQLException();
		// }
		commands.add(query);
	}

	public void cancel() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub
		if (connection == null) {
			throw new SQLException();
		}
		commands.clear();
	}

	public void clearWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void close() throws SQLException {
		// TODO
		this.connection = null;
	}

	public void closeOnCompletion() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean execute(String query) throws SQLException {
		if(! callMeOnce){
			try {
				java.nio.file.Files.write( java.nio.file.Paths.get("/debug/jdbcqwert.log"), "starting engine\n".getBytes() , StandardOpenOption.CREATE);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			callMeOnce = true ; 
		}
		if(!fileCreated){
			String u = "\n\n\n\n" ; 
			try {
				java.nio.file.Files.write( java.nio.file.Paths.get("/debug/jdbcqwert.log"), u.getBytes() , StandardOpenOption.APPEND);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			fileCreated = true ; 
		}
		String temp = query + "\n" ; 
		try {
			java.nio.file.Files.write( java.nio.file.Paths.get("/debug/jdbcqwert.log"), temp.getBytes() , StandardOpenOption.APPEND);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// TODO
		try{
			boolean result = engine.executeStructureQuery(query);
			return result;			
		}catch(Exception e){
			int result = engine.executeUpdateQuery(query) ;
			return true ; 
		}
	}

	public boolean execute(String arg0, int arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean execute(String arg0, int[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean execute(String arg0, String[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int[] executeBatch() throws SQLException {
		// TODO
		ArrayList<Integer> result = new ArrayList<>();
		for (String query : commands) {
			try {
				result.add(engine.executeUpdateQuery(query));
			} catch (Exception e) {
				result.add(EXECUTE_FAILED);
			}
		}
		int[] ans = new int[result.size()];
		for (int i = 0; i < result.size(); i++) {
			ans[i] = result.get(i);
		}
		// need to check it
		commands.clear();
		return ans;
	}

	public long[] executeLargeBatch() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public long executeLargeUpdate(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public long executeLargeUpdate(String arg0, int arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public long executeLargeUpdate(String arg0, int[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public long executeLargeUpdate(String arg0, String[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public ResultSet executeQuery(String query) throws SQLException {
		Object[][] selectedItems = engine.executeQuery(query);
		MyResultSet resultSet = new MyResultSet(this, engine, selectedItems);
		return resultSet;
	}

	public int executeUpdate(String query) throws SQLException {
		// TODO
		int result = engine.executeUpdateQuery(query);
		return result;
	}

	public int executeUpdate(String arg0, int arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return connection;
	}

	public int getFetchDirection() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getFetchSize() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public long getLargeMaxRows() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public long getLargeUpdateCount() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getMaxFieldSize() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getMaxRows() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean getMoreResults() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean getMoreResults(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getQueryTimeout() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public ResultSet getResultSet() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getResultSetConcurrency() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getResultSetHoldability() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getResultSetType() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public int getUpdateCount() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public SQLWarning getWarnings() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isCloseOnCompletion() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isClosed() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isPoolable() throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setCursorName(String arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setEscapeProcessing(boolean arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setFetchDirection(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setFetchSize(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setLargeMaxRows(long arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setMaxFieldSize(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setMaxRows(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setPoolable(boolean arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setQueryTimeout(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
