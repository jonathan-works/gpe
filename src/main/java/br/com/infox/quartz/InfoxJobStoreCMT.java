package br.com.infox.quartz;

import java.sql.Connection;

import org.quartz.JobPersistenceException;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;

public class InfoxJobStoreCMT  extends JobStoreCMT{
	
	
	@Override
	protected Connection getNonManagedTXConnection() throws JobPersistenceException {
		Connection nonManagedTXConnection = null;
		try {
			nonManagedTXConnection = super.getNonManagedTXConnection();
			if(!nonManagedTXConnection.isValid(100))
				  throw new JobPersistenceException(
			                "Could not get connection from DataSource '"
			                        + getNonManagedTXDataSource() + "'");
		} catch (Exception e) {
			  throw new JobPersistenceException(
		                "Could not get connection from DataSource '"
		                        + getNonManagedTXDataSource() + "'"); 
		} 
		return nonManagedTXConnection;
	}
	
	@Override
	protected void rollbackConnection(Connection conn) {
		try {
			if(conn != null && conn.isValid(100)){
				super.rollbackConnection(conn);
			}
		} catch (Exception e) {
		}
	}

}
