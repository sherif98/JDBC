package eg.edu.alexu.csd.oop.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import eg.edu.alexu.csd.oop.db.DbController;

public class Main {
	public static void main(String[] args) throws Exception {
			
		Driver driver = new MyDriver() ;
		Properties info = new Properties();
		File dbDir = new File("/debug/db/test/sample");
		info.put("path", dbDir.getAbsoluteFile());
		Connection connection = driver.connect("jdbc:xmldb://localhost", info);
		Statement statement = connection.createStatement();
		statement.execute("drop database test") ; 
		statement.execute("CREATE DATABASE test");
		statement.execute("create table students (id int, name varchar)") ; 
		statement.execute("CREATE TABLE incomplete_table_name1") ; 
		statement.executeUpdate("insert into students values (5, 'sherif')");
		statement.execute("insert into students values (50, 'tarek')");
		ResultSet res =  statement.executeQuery("select * from students") ;
		MyResultSet res2 = (MyResultSet) res ;
		System.out.println(res2.cursor);
		res2.next() ;
		System.out.println(res2.cursor);
		res2.previous() ;
		res2.previous() ;
		System.out.println(res2.cursor);
		res2.absolute(10) ; 
		System.out.println(res2.cursor);
		res2.absolute(5) ; 
		System.out.println(res2.cursor);
		res2.absolute(-1) ;
		int t = res2.getInt(1) ;
		System.out.println(t);
		System.out.println(res2.cursor);
		res2.absolute(-2) ; 
		System.out.println(res2.cursor);
		res2.next() ;
		System.out.println(res2.cursor);
		System.out.println(res2.isLast());
		res2.next() ;
		System.out.println(res2.cursor);
		System.out.println(res2.isAfterLast());
		System.out.println(res2.isLast());
	}
}
