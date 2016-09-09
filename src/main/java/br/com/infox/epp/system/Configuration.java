package br.com.infox.epp.system;

import java.util.Properties;

import br.com.infox.core.server.ServerInfo;

public class Configuration {

    private ApplicationServer applicationServer;
    private Database database;
    private boolean desenvolvimento;
    
    private static Configuration INSTANCE;
    
    private Configuration(ApplicationServer applicationServer, Database database) {
        this.applicationServer = applicationServer;
        this.database = database;
        this.desenvolvimento = System.getProperties().get("epp.desenvolvimento") == null ? false : (boolean) System.getProperties().get("epp.desenvolvimento");
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
        applicationServer.performQuartzProperties(properties);
        database.performQuartzProperties(properties);
    }
    
    public Properties configureJpa(Properties properties) {
        applicationServer.performJpaCustomProperties(properties);
        database.performJpaCustomProperties(properties);
        return properties;
    }

    public ApplicationServer getApplicationServer() {
        return applicationServer;
    }

    public Database getDatabase() {
        return database;
    }
    
    public boolean isDesenvolvimento() {
        return desenvolvimento;
    }
    
    public String getEpaPersistenceUnitName() {
        return "EPAPersistenceUnit";
    }
    
    public String getEpaBinPersistenceUnitName() {
        return "EPABinPersistenceUnit";
    }
    
}
