package br.com.infox.epp.system;

import javax.mail.Session;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import br.com.infox.epp.cdi.util.JNDI;

public class JBossEapApplicationServer extends AbstractApplicationServer {
    
    @Override
    public String getQuartzDataSourceJndi() {
        return "java:jboss/datasources/EPAQuartzDataSource";
    }

    @Override
    public String getEpaDataSourceJndi() {
        return "java:jboss/datasources/EPADataSource";
    }
    
    @Override
    public DataSource getListaDadosDataSource() {
        return JNDI.lookup("java:jboss/datasources/ListaDadosDataSource");
    }

    @Override
    public DataSource getEpaDataSource() {
        return JNDI.lookup("java:jboss/datasources/EPADataSource");
    }

    @Override
    public DataSource getEpaBinDataSource() {
        return JNDI.lookup("java:jboss/datasources/EPADataSourceBin");
    }
    
    @Override
    public DataSource getQuartzDataSource() {
        return JNDI.lookup("java:jboss/datasources/EPAQuartzDataSource");
    }

    @Override
    public String getHibernateCacheRegionClass() {
        return "org.jboss.as.jpa.hibernate4.infinispan.InfinispanRegionFactory";
    }

    @Override
    public Session getMailSession() {
        return JNDI.lookup("java:jboss/mail/epp");
    }

    @Override
    public String getHibernateJtaPlataform() {
        return "org.hibernate.service.jta.platform.internal.JBossAppServerJtaPlatform";
    }

    @Override
    public TransactionManager getTransactionManager() {
        return JNDI.lookup("java:jboss/TransactionManager");
    }

    @Override
    public String getInstanceName() {
        String instanceName = System.getProperty("jboss.node.name");
        return instanceName == null ? super.getInstanceName() : instanceName;
    }

    @Override
    public String getLogDir() {
        String logDir = System.getProperty("jboss.server.log.dir");
        return logDir == null ? super.getLogDir() : logDir;
    }
    
}
