package br.com.infox.core.persistence;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import br.com.infox.epp.cdi.util.JNDI;
import br.com.infox.epp.system.Configuration;

public class PersistenceUnitInfoWrapper implements PersistenceUnitInfo {
    
    private PersistenceUnitInfo delegate;
    
    private String persistenceUnitName;
    private String persistenceProviderClassName;
    private PersistenceUnitTransactionType transactionType;
    private DataSource jtaDataSource;
    private DataSource nonJtaDataSource;
    private List<String> mappingFileNames;
    private List<URL> jarFileUrls;
    private URL persistenceUnitRootUrl;
    private List<String> managedClassNames;
    private boolean excludeUnlistedClasses;
    private SharedCacheMode sharedCacheMode;
    private ValidationMode validationMode;
    private Properties properties;
    private String persistenceXMLSchemaVersion;
    private ClassLoader classLoader;
    
    public PersistenceUnitInfoWrapper(PersistenceUnitInfo persistenceUnitInfo, Configuration configuration) {
        this.delegate = persistenceUnitInfo;
        this.persistenceUnitName = persistenceUnitInfo.getPersistenceUnitName();
        this.persistenceProviderClassName = persistenceUnitInfo.getPersistenceProviderClassName();
        this.transactionType = persistenceUnitInfo.getTransactionType();
        this.jtaDataSource = configuration.getDatabase().getJtaDataSource(persistenceUnitName);
        this.nonJtaDataSource = persistenceUnitInfo.getNonJtaDataSource();
        this.mappingFileNames = performMappingFileNames(persistenceUnitInfo.getMappingFileNames(), persistenceUnitName);
        this.jarFileUrls = persistenceUnitInfo.getJarFileUrls();
        this.persistenceUnitRootUrl = persistenceUnitInfo.getPersistenceUnitRootUrl();
        this.managedClassNames = persistenceUnitInfo.getManagedClassNames();
        this.excludeUnlistedClasses = persistenceUnitInfo.excludeUnlistedClasses();
        this.sharedCacheMode = persistenceUnitInfo.getSharedCacheMode();
        this.validationMode = persistenceUnitInfo.getValidationMode();
        this.properties = configuration.configureJpa(persistenceUnitInfo.getProperties(), persistenceUnitName);
        this.persistenceXMLSchemaVersion = persistenceUnitInfo.getPersistenceXMLSchemaVersion();
        this.classLoader = persistenceUnitInfo.getClassLoader();
    }
    
    private List<String> performMappingFileNames(List<String> mappingFileNames, String persistenceUnitName) {
        if (Configuration.EPA_PERSISTENCE_UNIT_NAME.equals(persistenceUnitName)) {
            mappingFileNames.add("META-INF/extended-mappings.xml");
            mappingFileNames.add("META-INF/extended-named-queries.xml");
        }
        return mappingFileNames;
    }

    @Override
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return persistenceProviderClassName;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return transactionType;
    }

    @Override
    public DataSource getJtaDataSource() {
        return jtaDataSource;
    }
    
    public void setJtaDataSource(DataSource jtaDataSource) {
        this.jtaDataSource = jtaDataSource;
    }
    
    public void setJtaDataSource(String datasourceJndi) {
        this.jtaDataSource = JNDI.lookup(datasourceJndi);
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return nonJtaDataSource;
    }

    @Override
    public List<String> getMappingFileNames() {
        return mappingFileNames;
    }

    @Override
    public List<URL> getJarFileUrls() {
        return jarFileUrls;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return persistenceUnitRootUrl;
    }

    @Override
    public List<String> getManagedClassNames() {
        return managedClassNames;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return excludeUnlistedClasses;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return sharedCacheMode;
    }

    @Override
    public ValidationMode getValidationMode() {
        return validationMode;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return persistenceXMLSchemaVersion;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        delegate.addTransformer(transformer);
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return delegate.getNewTempClassLoader();
    }

}
