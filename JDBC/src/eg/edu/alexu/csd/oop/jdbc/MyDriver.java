package eg.edu.alexu.csd.oop.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;


import eg.edu.alexu.csd.oop.db.DbController;

public class MyDriver implements Driver{
	
	DbController engine;
	/*
	 * two drivers on the same database ?????
	 */

	@Override
	public boolean acceptsURL(String arg0) throws SQLException {
		//TODO
		// still don't know what to do here
		return true;
	}

	@Override
	public Connection connect(String url, Properties properties) throws SQLException {
		if(properties == null){
			return null ; 
		}
		//TODO
		engine = new DbController();
		//SQL is case insensitive
		Object temp = properties.get("path") ; 
		System.out.println(temp);
		String fileName = temp.toString()  ;
		if (fileName == null) {
			return null;
		}
		
		engine.set_save_destination(fileName.toLowerCase());
		// we should modify the engine implementation
//		boolean result = engine.executeStructureQuery("create database " + fileName);
//		if (!result) {
//			return null;
//		}
		return new MyConnection(fileName, this, engine);
	}

	@Override
	public int getMajorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getMinorVersion() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new UnsupportedOperationException();
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties properties)
			throws SQLException {
		// TODO	
		final int SIZE = 2;
		
		DriverPropertyInfo[] info = new DriverPropertyInfo[SIZE];
		
		DriverPropertyInfo result = new DriverPropertyInfo("path", properties.getProperty("path"));
		DriverPropertyInfo urlResult = new DriverPropertyInfo("url", url);
		
		info[0] = result;
		info[1] = urlResult;
		
		return info;
	}

	@Override
	public boolean jdbcCompliant() {
		throw new UnsupportedOperationException();
	}
}
