package br.com.infox.hibernate.session;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name(SessionAssistant.NAME)
@AutoCreate
public class SessionAssistant {

    public static final String NAME = "sessionAssistant";
    
    @In
    private transient EntityManager entityManager;
    
    public Session getSession() {
        return (Session) entityManager.getDelegate();
    }
    
    public Connection getConnection() throws NamingException, SQLException {
        SessionImpl sessionImpl = (SessionImpl) entityManager.unwrap(Session.class);
        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionImpl.getSessionFactory();
        DatasourceConnectionProviderImpl provider = (DatasourceConnectionProviderImpl) sessionFactoryImpl.getConnectionProvider();
        return provider.getDataSource().getConnection();
    }

}
