package br.com.infox.epp.system;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;

import br.com.infox.epp.cdi.util.JNDI;

public class TomeeApplicationServer extends AbstractApplicationServer {

    @Override
    public DataSource getListaDadosDataSource() {
        return JNDI.lookup("openejb:Resource/ListaDadosDataSource");
    }

    @Override
    public DataSource getEpaDataSource() {
        return JNDI.lookup("openejb:Resource/EPADataSource");
    }

    @Override
    public DataSource getEpaBinDataSource() {
        return JNDI.lookup("openejb:Resource/EPADataSourceBin");
    }
    
    @Override
    public DataSource getQuartzDataSource() {
        return JNDI.lookup("openejb:Resource/EPAQuartzDataSource");
    }

    @Override
    public String getHibernateCacheRegionClass() {
        return "org.hibernate.cache.infinispan.InfinispanRegionFactory";
    }

    @Override
    public String getMailSession() {
        return "openejb:Resource/mail/epp";
    }

    @Override
    public String getHibernateJtaPlataform() {
        return "org.apache.openejb.hibernate.OpenEJBJtaPlatform";
    }
    
    @Override
    public TransactionManager getTransactionManager() {
        return JNDI.lookup("java:comp/TransactionManager");
    }
    
    @Override
    public String getInstanceName() {
        String instanceName = System.getProperty("tomcat.node.name");
        return instanceName == null ? super.getInstanceName() : instanceName;
    }

    @Override
    public String getLogDir() {
        String logDir = System.getProperty("tomcat.server.log.dir");
        return logDir == null ? super.getLogDir() : logDir;
    }

    @Override
    public String getTransactionManagerLookupClass() {
        return "org.apache.openejb.hibernate.TransactionManagerLookup";
    }

}
