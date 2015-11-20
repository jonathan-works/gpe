package br.com.infox.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import br.com.infox.epp.cdi.util.JNDI;
import br.com.infox.epp.system.EppProperties;

public final class ConnectionFactory {
	
	public static Connection getConnection() throws SQLException {
		String dataSourceName = EppProperties.getProperty(EppProperties.PROPERTY_DATASOURCE);
		DataSource dataSource = JNDI.lookup(dataSourceName);
		return dataSource.getConnection();
	}

}
