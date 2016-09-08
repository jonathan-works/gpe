package br.com.infox.epp.system;

import java.util.Properties;

import br.com.infox.core.server.ServerInfo;

public class Configuration {

    private ApplicationServer applicationServer;
    private Database database;
    
    private static Configuration INSTANCE;
    
    private Configuration(ApplicationServer applicationServer, Database database) {
        this.applicationServer = applicationServer;
        this.database = database;
    }

    public synchronized static Configuration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = createInstance();
        }
        return INSTANCE;
    }
    
    private static Configuration createInstance() {
        ApplicationServer applicationServer = ServerInfo.createApplicationServer();
        Database database = ServerInfo.createDatabase(applicationServer);
        Configuration eppConfiguration = new Configuration(applicationServer, database);
        return eppConfiguration;
    }
    
    public void configureQuartz(Properties properties) {
        
    }
    
    public void configureJPA(Properties properties) {
        applicationServer.performJpaCustomProperties(properties);
        database.performJpaCustomizedProperties(properties);
    }

    public ApplicationServer getApplicationServer() {
        return applicationServer;
    }

    public Database getDatabase() {
        return database;
    }
    
    public String getEpaPersistenceUnitName() {
        return "EPAPersistenceUnit";
    }
    
    public String getEpaBinPersistenceUnitName() {
        return "EPABinPersistenceUnit";
    }
    
}
