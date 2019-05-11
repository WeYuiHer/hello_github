package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import dao.CommDAO;


public class JDBCutils {
	static Connection conn = null;
	static Properties config = null;
	static{
		 config = new Properties(); 
		  // InputStream in = config.getClass().getResourceAsStream("dbconnection.properties");
		
	     InputStream in =  CommDAO.class.getClassLoader().getResourceAsStream("dbconnection.properties");

		  
		 
		   try {
			config.load(in);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		  try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		     String dburl = (String)config.get("dburl");
		     try {
				conn = DriverManager.getConnection(dburl,"root","hekui");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public static Connection getConnection(){
		return conn;
	}
	
	
}
