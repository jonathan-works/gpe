package br.com.infox.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import br.com.infox.epp.system.Configuration;

public final class ConnectionFactory {
	
	public static Connection getConnection() throws SQLException {
		DataSource dataSource = Configuration.getInstance().getApplicationServer().getEpaDataSource();
		return dataSource.getConnection();
	}

}
