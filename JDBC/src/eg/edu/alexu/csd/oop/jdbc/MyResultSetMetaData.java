package eg.edu.alexu.csd.oop.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import eg.edu.alexu.csd.oop.db.DbController;
import eg.edu.alexu.csd.oop.db.Node;

public class MyResultSetMetaData implements ResultSetMetaData{
	/*
	 * note that all the columns I am talking about
	 * are the columns stored in the result set
	 */
	
	private MyResultSet resultSet;
	private DbController engine;
	private Node[][] myColumns;
	public MyResultSetMetaData(MyResultSet resultSet, DbController engine) {
		this.resultSet = resultSet;
		this.engine = engine;
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCatalogName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getColumnClassName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getColumnCount() throws SQLException {
		// TODO Auto-generated method stub
		//Returns the number of columns in this ResultSet object.
		myColumns = engine.getXtraInf();
		
		// returns the width of the node array
		return myColumns[0].length;
	}

	@Override
	public int getColumnDisplaySize(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getColumnLabel(int index) throws SQLException {
		// TODO Auto-generated method stub
		// specified with the as clause in the SQL statement --> don't know what it is
		return null;
	}

	@Override
	public String getColumnName(int index) throws SQLException {
		// TODO Auto-generated method stub
		//get the label of the column ex id, name ...
		// note that count starts from 1, 2 ..
		if (resultSet == null) {
			throw new SQLException();
		}
		myColumns = engine.getXtraInf();
		// I don't know, when the columns are null throw SQLException or return null
		if (myColumns == null) {
			return null;
		}
		if (index < 1) {
			// index out of range
			throw new SQLException();
		}
		return myColumns[0][index - 1].get_col_name();
	}

	@Override
	public int getColumnType(int index) throws SQLException {
		//TODO
		//get the column type ex: int, varchar ..
		if (resultSet == null) {
			throw new SQLException();
		}
		myColumns = engine.getXtraInf();
		if (myColumns == null) {
			throw new SQLException("getXtraInfo returned null");
		}
		if (index < 1 || index > myColumns[0].length) {
			throw new SQLException("Index out of bounds");
		}
		String columnType = myColumns[0][index - 1].getColumnType();
		if (columnType.equalsIgnoreCase("int")) {
			return Types.INTEGER;
		} else if (columnType.equalsIgnoreCase("varchar")) {
			return Types.VARCHAR;
		}
		// don't know when this is going to happen or what to do
		throw new SQLException();
	}

	@Override
	public String getColumnTypeName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getPrecision(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getScale(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSchemaName(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTableName(int index) throws SQLException {
		// TODO Auto-generated method stub
		// gets the table name for the specified column which means that each column must hold its table name
		if (resultSet == null) {
			throw new SQLException();
		}
		myColumns = engine.getXtraInf();
		if (myColumns == null) {
			throw new SQLException("engine returned no columns");
		}
		if (index < 1 || index > myColumns[0].length) {
			throw new SQLException("Index out of bound");
		}
		return myColumns[0][index - 1].getTableName();
	}

	@Override
	public boolean isAutoIncrement(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCaseSensitive(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCurrency(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDefinitelyWritable(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public int isNullable(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSearchable(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSigned(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isWritable(int arg0) throws SQLException {
		throw new UnsupportedOperationException();
	}

}
