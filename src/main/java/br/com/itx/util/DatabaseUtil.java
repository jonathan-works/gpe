package br.com.itx.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseUtil {
	
	private static final String fileNameProperties = "/database.properties";
	
	/*
	 * Retorna a conexão com a url, userName e password definidos 
	 * no arquivo de propriedades
	 */
	public Connection getConnection() throws Exception {
		Properties p = getProperties();
		return getConnection(p.getProperty("url"));
	}
	
	/*
	 * Retorna a conexão com o userName e password definidos 
	 * no arquivo de propriedades
	 */
	public Connection getConnection(String url) throws Exception {
		Properties p = getProperties();
		Class.forName(p.getProperty("driverClass"));
		return DriverManager.getConnection(url, p.getProperty("userName"), p.getProperty("password"));
	}
	
	private Properties getProperties() throws IOException {
		InputStream inStream = null;
		try {
			inStream = getClass().getResourceAsStream(getFileNameProperties());
			
			Properties p = new Properties();
			p.load(inStream);
			return p;
		} finally {
			inStream.close();
		}
	}
	
	protected String getFileNameProperties() {
		return fileNameProperties;
	}
	
	public static void close(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
