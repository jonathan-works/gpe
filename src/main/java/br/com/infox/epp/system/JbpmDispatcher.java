package br.com.infox.epp.system;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jbpm.JbpmConfiguration;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Services;

import br.com.infox.hibernate.util.HibernateUtil;

@Startup
@Singleton
public class JbpmDispatcher implements Serializable {

    private static final long serialVersionUID = 1553516381647020219L;
    
    @PostConstruct
    private void init() {
        JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
        DbPersistenceServiceFactory persistenceServiceFactory = (DbPersistenceServiceFactory) jbpmConfiguration.getServiceFactory(Services.SERVICENAME_PERSISTENCE);
        persistenceServiceFactory.setSessionFactory(HibernateUtil.getSessionFactoryImpl());
        persistenceServiceFactory.setSessionExpression("#{hibernateSession}");
    }
}
