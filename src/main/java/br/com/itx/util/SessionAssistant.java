package br.com.itx.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(SessionAssistant.NAME)
@AutoCreate
public class SessionAssistant extends GenericDAO {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "sessionAssistant";
    
    public Session getSession() {
        return (Session) getEntityManager().getDelegate();
    }
    
    public Connection getConnection() throws NamingException, SQLException {
        SessionImpl sessionImpl = (SessionImpl) getEntityManager().unwrap(Session.class);
        SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) sessionImpl.getSessionFactory();
        DatasourceConnectionProviderImpl provider = (DatasourceConnectionProviderImpl) sessionFactoryImpl.getConnectionProvider();
        return provider.getDataSource().getConnection();
    }

}
