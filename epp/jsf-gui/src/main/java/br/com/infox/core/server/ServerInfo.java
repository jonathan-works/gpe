package br.com.infox.core.server;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.sql.DataSource;

import br.com.infox.epp.cdi.util.JNDI;
import br.com.infox.epp.system.ApplicationServer;
import br.com.infox.epp.system.Database;
import br.com.infox.epp.system.DatabaseFactory;
import br.com.infox.epp.system.JBossEapApplicationServer;
import br.com.infox.epp.system.TomeeApplicationServer;

public class ServerInfo {
    
    private static final String JBOSS = "jboss";
    private static final String TOMEE = "tomee";
    
    private static String serverName;
    
    static {
        Object object = JNDI.lookup("java:jboss");
        if (object != null) {
            serverName = JBOSS;
        } else {
            object = JNDI.lookup("openejb:Resource");
            if (object != null) {
                serverName = TOMEE;
            } else {
                throw new IllegalStateException("Application Server not supported");
            }
        }
    }
    
    public static ApplicationServer createApplicationServer() {
        if (isJboss()) {
            return new JBossEapApplicationServer();
        } else if (isTomee()) {
            return new TomeeApplicationServer();
        } else {
            throw new IllegalStateException("Application Server not supported"); 
        }
    }
    
    public static Database createDatabase(ApplicationServer applicationServer) {
        DataSource dataSource = applicationServer.getEpaDataSource();
        try (Connection conn = dataSource.getConnection()){
            DatabaseMetaData metaData = conn.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();            
            return DatabaseFactory.create(databaseProductName);
        } catch (Exception e) {
            throw new IllegalStateException("Error discovering database", e);
        }
    }
    
    public static String getServerName() {
        return serverName;
    }
    
    public static boolean isJboss() {
        return JBOSS.equals(serverName);
    }
    
    public static boolean isTomee() {
        return TOMEE.equals(serverName);
    }
    
}
